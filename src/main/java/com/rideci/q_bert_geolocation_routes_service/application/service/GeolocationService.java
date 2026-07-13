package com.rideci.q_bert_geolocation_routes_service.application.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.rideci.q_bert_geolocation_routes_service.domain.model.LocationShare;
import com.rideci.q_bert_geolocation_routes_service.domain.model.Route;
import com.rideci.q_bert_geolocation_routes_service.domain.model.RouteHistory;
import com.rideci.q_bert_geolocation_routes_service.domain.model.TrackingConfiguration;
import com.rideci.q_bert_geolocation_routes_service.domain.model.TravelTracking;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.in.CreateRouteUseCase;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.in.GetAllRoutesUseCase;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.in.GetLocationShareUseCase;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.in.GetRouteUseCase;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.in.GetTravelReplayUseCase;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.in.GetUserLocationUseCase;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.in.GetUsersLocationUseCase;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.in.ShareLocationUseCase;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.in.UpdateConfigurableIntervalsUseCase;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.in.UpdateRouteUseCase;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.in.UpdateUserLocationUseCase;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.in.UpdateUsersLocationUseCase;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class GeolocationService
        implements CreateRouteUseCase, UpdateRouteUseCase, GetRouteUseCase, GetAllRoutesUseCase, GetUserLocationUseCase,
        GetUsersLocationUseCase, UpdateUserLocationUseCase, UpdateUsersLocationUseCase, UpdateConfigurableIntervalsUseCase,
        GetTravelReplayUseCase, ShareLocationUseCase, GetLocationShareUseCase {

    private final CreateRouteUseCase createRouteUseCase;
    private final UpdateRouteUseCase updateRouteUseCase;
    private final GetRouteUseCase getRouteUseCase;
    private final GetAllRoutesUseCase getAllRoutesUseCase;
    private final GetUserLocationUseCase getUserLocationUseCase;
    private final GetUsersLocationUseCase getUsersLocationUseCase;
    private final UpdateUserLocationUseCase updateUserLocationUseCase;
    private final UpdateUsersLocationUseCase updateUsersLocationUseCase;
    private final UpdateConfigurableIntervalsUseCase updateConfigurableIntervalsUseCase;
    private final GetTravelReplayUseCase getTravelReplayUseCase;
    private final ShareLocationUseCase shareLocationUseCase;
    private final GetLocationShareUseCase getLocationShareUseCase;

    @Override
    public Mono<Route> createRoute(Route route) {
        return createRouteUseCase.createRoute(route);
    }

    @Override
    public Mono<Route> updateRoute(String routeId, Route updatedRoute) {
        return updateRouteUseCase.updateRoute(routeId, updatedRoute);
    }

    @Override
    public Mono<Route> getRoute(String routeId) {
        return getRouteUseCase.getRoute(routeId);
    }

    @Override
    public Flux<Route> getAllRoutes() {
        return getAllRoutesUseCase.getAllRoutes();
    }

    @Override
    public Flux<TravelTracking> updateUsersLocation(String tripId, List<TravelTracking> newUserstracking) {
        return updateUsersLocationUseCase.updateUsersLocation(tripId, newUserstracking);
    }

    @Override
    public Mono<TravelTracking> updateUserLocation(String tripId, TravelTracking newUsertracking) {
        return updateUserLocationUseCase.updateUserLocation(tripId, newUsertracking);
    }

    @Override
    public Flux<TravelTracking> getUsersLocation(String tripId) {
        return getUsersLocationUseCase.getUsersLocation(tripId);
    }

    @Override
    public Mono<TravelTracking> getUserLocation(String tripId, String participantId) {
        return getUserLocationUseCase.getUserLocation(tripId, participantId);
    }

    @Override
    public Mono<TrackingConfiguration> updateConfigurableInterval(String tripId, String participantId, int newUpdateIntervalSeconds) {
        return updateConfigurableIntervalsUseCase.updateConfigurableInterval(tripId, participantId, newUpdateIntervalSeconds);
    }

    @Override
    public Flux<RouteHistory> getTravelReplay(String tripId, String participantId, double speedMultiplier) {
        return getTravelReplayUseCase.getTravelReplay(tripId, participantId, speedMultiplier);
    }

    @Override
    public Mono<LocationShare> shareLocation(String tripId, String passengerId, String emergencyContactId) {
        return shareLocationUseCase.shareLocation(tripId, passengerId, emergencyContactId);
    }

    @Override
    public Mono<LocationShare> getLocationShare(String shareId) {
        return getLocationShareUseCase.getLocationShare(shareId);
    }

}
