package com.rideci.q_bert_geolocation_routes_service.domain.ports.out;

import java.util.List;

import com.rideci.q_bert_geolocation_routes_service.domain.model.Route;
import com.rideci.q_bert_geolocation_routes_service.domain.model.TravelTracking;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GeolocationRepositoryOutPort {

    Mono<Route> save(Route route);

    Mono<Route> update(String id, Route newRoute);

    Mono<Route> findRouteById(String id);

    Flux<Route> findAllRoutes();

    Mono<TravelTracking> getUserLocation(String tripId, TravelTracking tracking);

    Flux<TravelTracking> getUsersLocation(String tripId, List<TravelTracking> usersTracking);

    Mono<TravelTracking> updateUserLocation(String tripId, TravelTracking newUsertracking);

    Flux<TravelTracking> updateUsersLocation(String tripId, List<TravelTracking> newUserstracking);

}
