package com.rideci.q_bert_geolocation_routes_service.domain.ports.out;

import java.util.List;

import com.rideci.q_bert_geolocation_routes_service.domain.model.Geofence;
import com.rideci.q_bert_geolocation_routes_service.domain.model.Location;
import com.rideci.q_bert_geolocation_routes_service.domain.model.PickUpPoint;

public interface TomTomOutPort {
    
    RouteInfo calculateRoute(Location origin, Location destination, List<PickUpPoint> waypoints);

    Location geocodeAddress(String address);

    String reverseGeocode(Location location);

    List<PickUpPoint> optimizeWaypoints(List<PickUpPoint> waypoints);

    List<Location> snapToRoad(List<Location> rawPositions);

    boolean isPointInsideGeofence(Location point, Geofence geofence);
}
