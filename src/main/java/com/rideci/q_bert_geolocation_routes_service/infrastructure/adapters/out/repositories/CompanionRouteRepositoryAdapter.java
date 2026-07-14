package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.repositories;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.rideci.q_bert_geolocation_routes_service.domain.exception.CompanionRouteNotFoundException;
import com.rideci.q_bert_geolocation_routes_service.domain.model.CompanionRoute;
import com.rideci.q_bert_geolocation_routes_service.domain.model.TransitItinerary;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.out.CompanionRouteRepositoryOutPort;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.out.OtpOutPort;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.entities.CompanionRouteDocument;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.mapper.CompanionRouteMapper;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Repository
public class CompanionRouteRepositoryAdapter implements CompanionRouteRepositoryOutPort {

    private final CompanionRouteRepository companionRouteRepository;
    private final CompanionRouteMapper companionRouteMapper;
    private final OtpOutPort otpOutPort;

    @Override
    public Mono<CompanionRoute> save(CompanionRoute companionRoute) {
        return otpOutPort.planTrip(companionRoute.getOrigin(), companionRoute.getDestination(), companionRoute.getDepartureTime())
                .map(itinerary -> buildCompanionRoute(companionRoute, itinerary))
                .map(companionRouteMapper::toDocument)
                .flatMap(companionRouteRepository::save)
                .map(companionRouteMapper::toDomain);
    }

    private CompanionRoute buildCompanionRoute(CompanionRoute companionRoute, TransitItinerary itinerary) {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        return CompanionRoute.builder()
                .id(UUID.randomUUID().toString())
                .tripId(companionRoute.getTripId())
                .origin(companionRoute.getOrigin())
                .destination(companionRoute.getDestination())
                .departureTime(companionRoute.getDepartureTime())
                .estimatedArrivalTime(itinerary.getEstimatedArrivalTime())
                .totalDistance(itinerary.getTotalDistance())
                .totalTransfers(itinerary.getTotalTransfers())
                .legs(itinerary.getLegs())
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    @Override
    public Mono<CompanionRoute> update(String id, CompanionRoute companionRoute) {
        return companionRouteRepository.findById(id)
                .switchIfEmpty(Mono.error(() -> new CompanionRouteNotFoundException(id)))
                .map(companionRouteMapper::toDomain)
                .flatMap(existingRoute -> otpOutPort
                        .planTrip(companionRoute.getOrigin(), companionRoute.getDestination(), companionRoute.getDepartureTime())
                        .map(itinerary -> applyItinerary(existingRoute, companionRoute, itinerary)))
                .map(companionRouteMapper::toDocument)
                .flatMap(companionRouteRepository::save)
                .map(companionRouteMapper::toDomain);
    }

    private CompanionRoute applyItinerary(CompanionRoute existingRoute, CompanionRoute updatePayload, TransitItinerary itinerary) {
        existingRoute.setOrigin(updatePayload.getOrigin());
        existingRoute.setDestination(updatePayload.getDestination());
        existingRoute.setDepartureTime(updatePayload.getDepartureTime());
        existingRoute.setEstimatedArrivalTime(itinerary.getEstimatedArrivalTime());
        existingRoute.setTotalDistance(itinerary.getTotalDistance());
        existingRoute.setTotalTransfers(itinerary.getTotalTransfers());
        existingRoute.setLegs(itinerary.getLegs());
        existingRoute.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
        return existingRoute;
    }

    @Override
    public Mono<CompanionRoute> findCompanionRouteById(String id) {
        Mono<CompanionRouteDocument> companionRoute = companionRouteRepository.findById(id)
                .switchIfEmpty(Mono.error(() -> new CompanionRouteNotFoundException(id)));

        return companionRouteMapper.toDomain(companionRoute);
    }

    @Override
    public Flux<CompanionRoute> findAllCompanionRoutes() {
        return companionRouteMapper.listToDomain(companionRouteRepository.findAll());
    }

}
