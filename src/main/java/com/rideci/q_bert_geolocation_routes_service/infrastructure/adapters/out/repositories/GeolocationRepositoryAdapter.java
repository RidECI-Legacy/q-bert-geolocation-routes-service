package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.repositories;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;

import com.rideci.q_bert_geolocation_routes_service.domain.exception.RouteNotFoundException;
import com.rideci.q_bert_geolocation_routes_service.domain.exception.TravelTrackingNotFoundException;
import com.rideci.q_bert_geolocation_routes_service.domain.model.LocationShare;
import com.rideci.q_bert_geolocation_routes_service.domain.model.PickUpPoint;
import com.rideci.q_bert_geolocation_routes_service.domain.model.Route;
import com.rideci.q_bert_geolocation_routes_service.domain.model.RouteHistory;
import com.rideci.q_bert_geolocation_routes_service.domain.model.RouteInfo;
import com.rideci.q_bert_geolocation_routes_service.domain.model.TrackingConfiguration;
import com.rideci.q_bert_geolocation_routes_service.domain.model.TravelTracking;
import com.rideci.q_bert_geolocation_routes_service.domain.model.enums.ShareStatus;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.out.GeolocationRepositoryOutPort;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.out.TomTomOutPort;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.entities.LocationShareDocument;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.entities.RouteDocument;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.entities.RouteHistoryDocument;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.entities.TrackingConfigurationDocument;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.entities.TravelTrackingDocument;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.mapper.RouteMapper;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.mapper.TravelTrackingMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Slf4j
@Repository
public class GeolocationRepositoryAdapter implements GeolocationRepositoryOutPort {

    private static final String TRAVEL_TRACKING_KEY_PREFIX = "travel-tracking:";

    private final GeolocationRepository geolocationRepository;
    private final RouteMapper routeMapper;
    private final TomTomOutPort tomTomOutPort;
    private final RouteHistoryRepository routeHistoryRepository;
    private final LocationShareRepository locationShareRepository;
    private final TravelTrackingMapper travelTrackingMapper;
    private final ReactiveRedisTemplate<String, TravelTrackingDocument> travelTrackingRedisTemplate;
    private final ReactiveRedisTemplate<String, TrackingConfigurationDocument> trackingConfigurationRedisTemplate;

    @Override
    public Mono<Route> save(Route route) {
        return tomTomOutPort.optimizeWaypoints(route.getPickUpPoints())
                .flatMap(optimizedPickUpPoints -> tomTomOutPort
                        .calculateRoute(route.getOrigin(), route.getDestination(), optimizedPickUpPoints)
                        .map(routeInfo -> buildRoute(route, optimizedPickUpPoints, routeInfo)))
                .map(routeMapper::toDocument)
                .flatMap(geolocationRepository::save)
                .map(routeMapper::toDomain);

    }

    private Route buildRoute(Route route, List<PickUpPoint> optimizedPickUpPoints, RouteInfo routeInfo) {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        return Route.builder()
                .id(UUID.randomUUID().toString())
                .tripId(route.getTripId())
                .origin(route.getOrigin())
                .destination(route.getDestination())
                .totalDistance(routeInfo.getTotalDistance())
                .remainingDistance(routeInfo.getTotalDistance())
                .estimatedArrivalTime(routeInfo.getEstimatedArrivalTime())
                .polyline(routeInfo.getPolyline())
                .pickUpPoints(optimizedPickUpPoints)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    @Override
    public Mono<Route> update(String id, Route updatedRoute) {
        return geolocationRepository.findById(id)
                .switchIfEmpty(Mono.error(() -> new RouteNotFoundException(id)))
                .map(routeMapper::toDomain)
                .map(existingRoute -> mergeEditableFields(existingRoute, updatedRoute))
                .flatMap(mergedRoute -> tomTomOutPort.optimizeWaypoints(mergedRoute.getPickUpPoints())
                        .flatMap(optimizedPickupPoints -> tomTomOutPort
                                .calculateRoute(mergedRoute.getOrigin(), mergedRoute.getDestination(),
                                        optimizedPickupPoints)
                                .map(routeInfo -> applyRouteInfo(mergedRoute, optimizedPickupPoints, routeInfo))))
                .map(routeMapper::toDocument)
                .flatMap(geolocationRepository::save)
                .map(routeMapper::toDomain);
    }

    private Route mergeEditableFields(Route existingRoute, Route updatedRoute) {
        existingRoute.setOrigin(updatedRoute.getOrigin());
        existingRoute.setDestination(updatedRoute.getDestination());
        existingRoute.setPickUpPoints(updatedRoute.getPickUpPoints());
        return existingRoute;
    }

    private Route applyRouteInfo(Route route, List<PickUpPoint> optimizedPickupPoints, RouteInfo routeInfo) {
        route.setPickUpPoints(optimizedPickupPoints);
        route.setTotalDistance(routeInfo.getTotalDistance());
        route.setRemainingDistance(routeInfo.getTotalDistance());
        route.setEstimatedArrivalTime(routeInfo.getEstimatedArrivalTime());
        route.setPolyline(routeInfo.getPolyline());
        route.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
        return route;
    }

    @Override
    public Mono<Route> findRouteById(String id) {
        Mono<RouteDocument> route = geolocationRepository.findById(id)
                .switchIfEmpty(Mono.error(() -> new RouteNotFoundException(id)));

        return routeMapper.toDomain(route);
    }

    @Override
    public Flux<Route> findAllRoutes() {
        return routeMapper.listToDomain(geolocationRepository.findAll());
    }

    @Override
    public Mono<TravelTracking> getUserLocation(String tripId, String participantId) {
        return travelTrackingRedisTemplate.opsForValue().get(travelTrackingKey(tripId, participantId))
                .switchIfEmpty(Mono.error(() -> new TravelTrackingNotFoundException(tripId, participantId)))
                .map(travelTrackingMapper::toDomain);
    }

    @Override
    public Flux<TravelTracking> getUsersLocation(String tripId) {
        return travelTrackingRedisTemplate.keys(travelTrackingKey(tripId, "*"))
                .flatMap(key -> travelTrackingRedisTemplate.opsForValue().get(key))
                .map(travelTrackingMapper::toDomain);
    }

    @Override
    public Mono<TravelTracking> updateUserLocation(String tripId, TravelTracking newUsertracking) {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        newUsertracking.setTripId(tripId);
        newUsertracking.setUpdatedAt(now);

        String key = travelTrackingKey(tripId, newUsertracking.getParticipantId());

        return trackingConfigurationRedisTemplate.opsForValue()
                .get(trackingConfigurationKey(tripId, newUsertracking.getParticipantId()))
                .map(config -> {
                    newUsertracking.setTrackingConfiguration(travelTrackingMapper.toDomain(config));
                    return newUsertracking;
                })
                .switchIfEmpty(Mono.just(newUsertracking))
                .map(travelTrackingMapper::toDocument)
                .flatMap(document -> travelTrackingRedisTemplate.opsForValue().set(key, document)
                        .then(routeHistoryRepository.save(buildHistoryEntry(newUsertracking, now)))
                        .thenReturn(document))
                .map(travelTrackingMapper::toDomain);
    }

    private RouteHistoryDocument buildHistoryEntry(TravelTracking tracking, LocalDateTime recordedAt) {
        RouteHistory entry = RouteHistory.builder()
                .id(UUID.randomUUID().toString())
                .tripId(tracking.getTripId())
                .participantId(tracking.getParticipantId())
                .speed(tracking.getSpeed())
                .heading(tracking.getHeading())
                .location(tracking.getCurrentLocation())
                .participantRole(tracking.getParticipantRole())
                .recordedAt(recordedAt)
                .build();

        return travelTrackingMapper.toDocument(entry);
    }

    @Override
    public Flux<TravelTracking> updateUsersLocation(String tripId, List<TravelTracking> newUserstracking) {
        return Flux.fromIterable(newUserstracking)
                .flatMap(tracking -> updateUserLocation(tripId, tracking));
    }

    @Override
    public Mono<TrackingConfiguration> updateConfigurableInterval(String tripId, String participantId, int newUpdateIntervalSeconds) {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        TrackingConfigurationDocument configuration = TrackingConfigurationDocument.builder()
                .tripId(tripId)
                .participantId(participantId)
                .updateIntervalSeconds(newUpdateIntervalSeconds)
                .updatedAt(now)
                .build();

        return trackingConfigurationRedisTemplate.opsForValue()
                .set(trackingConfigurationKey(tripId, participantId), configuration)
                .thenReturn(configuration)
                .map(travelTrackingMapper::toDomain);
    }

    @Override
    public Flux<RouteHistory> getTravelReplay(String tripId, String participantId, double speedMultiplier) {
        double effectiveSpeed = speedMultiplier > 0 ? speedMultiplier : 1.0;

        return travelTrackingMapper
                .toDomainHistory(routeHistoryRepository.findByTripIdAndParticipantIdOrderByRecordedAtAsc(tripId, participantId))
                .collectList()
                .flatMapMany(history -> replay(history, effectiveSpeed));
    }

    private Flux<RouteHistory> replay(List<RouteHistory> history, double speedMultiplier) {
        return Flux.range(0, history.size())
                .concatMap(index -> emitWithOriginalTiming(history, index, speedMultiplier));
    }

    private Mono<RouteHistory> emitWithOriginalTiming(List<RouteHistory> history, int index, double speedMultiplier) {
        RouteHistory point = history.get(index);
        if (index == 0) {
            return Mono.just(point);
        }

        Duration gap = Duration.between(history.get(index - 1).getRecordedAt(), point.getRecordedAt());
        Duration scaledGap = Duration.ofMillis((long) (gap.toMillis() / speedMultiplier));

        return Mono.just(point).delayElement(scaledGap);
    }


    private String travelTrackingKey(String tripId, String participantId) {
        return TRAVEL_TRACKING_KEY_PREFIX + tripId + ":" + participantId;
    }

    private String trackingConfigurationKey(String tripId, String participantId) {
        return "tracking-configuration:" + tripId + ":" + participantId;
    }

    @Override
    public Mono<LocationShare> shareLocation(String tripId, String passengerId, String emergencyContactId) {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        LocationShareDocument document = LocationShareDocument.builder()
                .id(UUID.randomUUID().toString())
                .tripId(tripId)
                .participantId(passengerId)
                .emergencyContactId(emergencyContactId)
                .shareStatus(ShareStatus.ACTIVE)
                .createdAt(now)
                .updatedAt(now)
                .build();

        return locationShareRepository.save(document)
                .map(travelTrackingMapper::toDomain);
    }

    @Override
    public Mono<LocationShare> getLocationShare(String shareId) {
        return locationShareRepository.findById(shareId)
                .map(travelTrackingMapper::toDomain);
    }

}
