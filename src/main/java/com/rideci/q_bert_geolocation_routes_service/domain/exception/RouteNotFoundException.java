package com.rideci.q_bert_geolocation_routes_service.domain.exception;

import lombok.Getter;

@Getter
public class RouteNotFoundException extends RuntimeException {

    private final String routeId;

    public RouteNotFoundException(String routeId) {
        super("Route with id " + routeId + " not found");
        this.routeId = routeId;
    }

}
