package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.rideci.q_bert_geolocation_routes_service.domain.exception.RouteNotFoundException;
import com.rideci.q_bert_geolocation_routes_service.domain.model.PickUpPoint;
import com.rideci.q_bert_geolocation_routes_service.domain.model.Route;
import com.rideci.q_bert_geolocation_routes_service.domain.model.RouteInfo;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.out.GeolocationRepositoryOutPort;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.out.TomTomOutPort;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.entities.RouteDocument;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.mapper.RouteMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Slf4j
@Repository
public class GeolocationRepositoryAdapter implements GeolocationRepositoryOutPort {

    private final GeolocationRepository geolocationRepository;
    private final RouteMapper routeMapper;
    private final TomTomOutPort tomTomOutPort;

    @Override
    public Mono<Route> save(Route route) {
        Mono<Route> newRoute = tomTomOutPort.optimizeWaypoints(route.getPickUpPoints())
                .flatMap(optimizedPickUpPoints -> tomTomOutPort
                        .calculateRoute(route.getOrigin(), route.getDestination(), optimizedPickUpPoints)
                        .map(routeInfo -> buildRoute(route, optimizedPickUpPoints, routeInfo)));

        RouteDocument routeDocument = routeMapper.toDocument(route);
        geolocationRepository.save(routeDocument);

        return newRoute;
    }

    private Route buildRoute(Route route, List<PickUpPoint> optimizedPickUpPoints, RouteInfo routeInfo) {
        LocalDateTime now = LocalDateTime.now();
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
        route.setUpdatedAt(LocalDateTime.now());
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

}
