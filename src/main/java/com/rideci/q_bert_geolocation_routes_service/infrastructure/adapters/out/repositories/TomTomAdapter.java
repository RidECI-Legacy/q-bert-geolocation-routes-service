package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.repositories;

import java.util.List;

import com.rideci.q_bert_geolocation_routes_service.domain.model.Geofence;
import com.rideci.q_bert_geolocation_routes_service.domain.model.Location;
import com.rideci.q_bert_geolocation_routes_service.domain.model.PickUpPoint;
import com.rideci.q_bert_geolocation_routes_service.domain.model.RouteInfo;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.out.TomTomOutPort;

public class TomTomAdapter implements TomTomOutPort{

    @Override
    public RouteInfo calculateRoute(Location origin, Location destination, List<PickUpPoint> waypoints) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'calculateRoute'");
    }

    @Override
    public Location geocodeAddress(String address) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'geocodeAddress'");
    }

    @Override
    public String reverseGeocode(Location location) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'reverseGeocode'");
    }

    @Override
    public List<PickUpPoint> optimizeWaypoints(List<PickUpPoint> waypoints) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'optimizeWaypoints'");
    }

    @Override
    public List<Location> snapToRoad(List<Location> rawPositions) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'snapToRoad'");
    }

    @Override
    public boolean isPointInsideGeofence(Location point, Geofence geofence) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isPointInsideGeofence'");
    }
    
}
