package com.rideci.q_bert_geolocation_routes_service.domain.exception;

import lombok.Getter;

@Getter
public class CompanionRouteNotFoundException extends RuntimeException {

    private final String companionRouteId;

    public CompanionRouteNotFoundException(String companionRouteId) {
        super("Companion route with id " + companionRouteId + " not found");
        this.companionRouteId = companionRouteId;
    }

}
