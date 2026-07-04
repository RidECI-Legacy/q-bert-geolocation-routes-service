package com.rideci.q_bert_geolocation_routes_service.application.useCases;

import java.time.LocalDateTime;
import java.util.List;

import com.rideci.q_bert_geolocation_routes_service.application.annotation.UseCase;
import com.rideci.q_bert_geolocation_routes_service.domain.exception.RouteNotFoundException;
import com.rideci.q_bert_geolocation_routes_service.domain.model.PickUpPoint;
import com.rideci.q_bert_geolocation_routes_service.domain.model.Route;
import com.rideci.q_bert_geolocation_routes_service.domain.model.RouteInfo;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.in.UpdateRouteUseCase;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.out.GeolocationRepositoryOutPort;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.out.TomTomOutPort;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@UseCase
@RequiredArgsConstructor
public class UpdateRouteUseCaseImpl implements UpdateRouteUseCase {

    private final GeolocationRepositoryOutPort geolocationRepositoryOutPort;
    private final TomTomOutPort tomTomOutPort;

    @Override
    public Mono<Route> updateRoute(String routeId, Route updatedRoute) {
        return geolocationRepositoryOutPort.findRouteById(routeId)
                .switchIfEmpty(Mono.error(() -> new RouteNotFoundException(routeId)))
                .map(existingRoute -> mergeEditableFields(existingRoute, updatedRoute))
                .flatMap(mergedRoute -> tomTomOutPort.optimizeWaypoints(mergedRoute.getPickupPoints())
                        .flatMap(optimizedPickupPoints -> tomTomOutPort
                                .calculateRoute(mergedRoute.getOrigin(), mergedRoute.getDestination(), optimizedPickupPoints)
                                .map(routeInfo -> applyRouteInfo(mergedRoute, optimizedPickupPoints, routeInfo))))
                .flatMap(geolocationRepositoryOutPort::save);
    }

    private Route mergeEditableFields(Route existingRoute, Route updatedRoute) {
        existingRoute.setOrigin(updatedRoute.getOrigin());
        existingRoute.setDestination(updatedRoute.getDestination());
        existingRoute.setPickupPoints(updatedRoute.getPickupPoints());
        return existingRoute;
    }

    private Route applyRouteInfo(Route route, List<PickUpPoint> optimizedPickupPoints, RouteInfo routeInfo) {
        route.setPickupPoints(optimizedPickupPoints);
        route.setTotalDistance(routeInfo.getTotalDistance());
        route.setRemainingDistance(routeInfo.getTotalDistance());
        route.setEstimatedArrivalTime(routeInfo.getEstimatedArrivalTime());
        route.setPolyline(routeInfo.getPolyline());
        route.setUpdatedAt(LocalDateTime.now());
        return route;
    }

}
