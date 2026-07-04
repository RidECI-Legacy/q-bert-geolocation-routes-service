package com.rideci.q_bert_geolocation_routes_service.domain.ports.out;

import java.util.List;

import com.rideci.q_bert_geolocation_routes_service.domain.model.Location;
import com.rideci.q_bert_geolocation_routes_service.domain.model.PickUpPoint;
import com.rideci.q_bert_geolocation_routes_service.domain.model.RouteInfo;

import reactor.core.publisher.Mono;

public interface TomTomOutPort {

    Mono<RouteInfo> calculateRoute(Location origin, Location destination, List<PickUpPoint> waypoints);

    Mono<Location> geocodeAddress(String address);

    Mono<String> reverseGeocode(Location location);

    Mono<List<PickUpPoint>> optimizeWaypoints(List<PickUpPoint> waypoints);

    Mono<List<Location>> snapToRoad(List<Location> rawPositions);

    boolean isPointInsideGeofence(Location currentPosition, PickUpPoint pickUpPoint);
}
