package com.rideci.q_bert_geolocation_routes_service.domain.exception;

import lombok.Getter;

@Getter
public class VehicleStatusNotAvailableException extends RuntimeException {

    private final String companionRouteId;

    private final String legId;

    public VehicleStatusNotAvailableException(String companionRouteId, String legId) {
        super("No live vehicle status available yet for companion route " + companionRouteId + " leg " + legId);
        this.companionRouteId = companionRouteId;
        this.legId = legId;
    }

}
