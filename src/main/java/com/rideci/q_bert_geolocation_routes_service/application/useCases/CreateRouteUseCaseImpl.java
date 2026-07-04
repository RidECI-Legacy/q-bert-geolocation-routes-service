package com.rideci.q_bert_geolocation_routes_service.application.useCases;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.rideci.q_bert_geolocation_routes_service.application.annotation.UseCase;
import com.rideci.q_bert_geolocation_routes_service.domain.model.PickUpPoint;
import com.rideci.q_bert_geolocation_routes_service.domain.model.Route;
import com.rideci.q_bert_geolocation_routes_service.domain.model.RouteInfo;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.in.CreateRouteUseCase;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.out.GeolocationRepositoryOutPort;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.out.TomTomOutPort;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@UseCase
@RequiredArgsConstructor
public class CreateRouteUseCaseImpl implements CreateRouteUseCase {

    private final GeolocationRepositoryOutPort geolocationRepositoryOutPort;
    private final TomTomOutPort tomTomOutPort;

    @Override
    public Mono<Route> createRoute(Route route) {
        return tomTomOutPort.optimizeWaypoints(route.getPickupPoints())
                .flatMap(optimizedPickupPoints -> tomTomOutPort
                        .calculateRoute(route.getOrigin(), route.getDestination(), optimizedPickupPoints)
                        .map(routeInfo -> buildRoute(route, optimizedPickupPoints, routeInfo)))
                .flatMap(geolocationRepositoryOutPort::save);
    }

    private Route buildRoute(Route route, List<PickUpPoint> optimizedPickupPoints, RouteInfo routeInfo) {
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
                .pickupPoints(optimizedPickupPoints)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

}
