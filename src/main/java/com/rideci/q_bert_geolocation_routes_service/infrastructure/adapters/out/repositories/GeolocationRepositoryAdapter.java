package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.rideci.q_bert_geolocation_routes_service.domain.model.Route;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.out.GeolocationRepositoryOutPort;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.entities.RouteDocument;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.mapper.RouteMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Repository
public class GeolocationRepositoryAdapter implements GeolocationRepositoryOutPort {

    private final GeolocationRepository geolocationRepository;
    private final RouteMapper routeMapper;

    @Override
    public Route saveRoute(Route route) {

        Route newRoute = Route.builder()
                .id(UUID.randomUUID().toString())
                .tripId(route.getTripId())
                .origin(route.getOrigin())
                .destination(route.getDestination())
                .totalDistance(route.getTotalDistance())
                .remainingDistance(route.getRemainingDistance())
                .estimatedArrivalTime(route.getEstimatedArrivalTime())
                .polyline(route.getPolyline())
                .pickupPoints(route.getPickupPoints())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        RouteDocument routeDocument = routeMapper.toDocument(newRoute);
        RouteDocument savedRouteDocument = geolocationRepository.save(routeDocument);

        return routeMapper.toDomain(savedRouteDocument);
    }

    @Override
    public Optional<Route> updateRoute(String id, Route newRoute) {

        Route route = findRouteById(id)
                .orElseThrow(() -> new RuntimeException("Route with id " + id + " not found"));

        route.setOrigin(newRoute.getOrigin());
        route.setDestination(newRoute.getDestination());
        route.setTotalDistance(newRoute.getTotalDistance());
        route.setRemainingDistance(newRoute.getRemainingDistance());
        route.setEstimatedArrivalTime(newRoute.getEstimatedArrivalTime());
        route.setPolyline(newRoute.getPolyline());
        route.setPickupPoints(newRoute.getPickupPoints());
        route.setUpdatedAt(LocalDateTime.now());

        RouteDocument updatedRouteDocument = routeMapper.toDocument(route);
        RouteDocument saveUpdatedRouteDocument = geolocationRepository.save(updatedRouteDocument);

        return Optional.of(routeMapper.toDomain(saveUpdatedRouteDocument));
    }

    @Override
    public Optional<Route> findRouteById(String id) {
        RouteDocument routeDocument = geolocationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Route with id " + id + " not found"));

        return Optional.of(routeMapper.toDomain(routeDocument));
    }

    @Override
    public Optional<List<Route>> findAllRoutes() {
        List<RouteDocument> routeDocuments = geolocationRepository.findAll();

        return Optional.of(routeMapper.toDomainList(routeDocuments));
    }

}
