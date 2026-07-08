package com.rideci.q_bert_geolocation_routes_service.domain.exception;

import lombok.Getter;

@Getter
public class TravelTrackingNotFoundException extends RuntimeException {

    private final String tripId;

    private final String participantId;

    public TravelTrackingNotFoundException(String tripId, String participantId) {
        super("Travel tracking for trip " + tripId + " and participant " + participantId + " not found");
        this.tripId = tripId;
        this.participantId = participantId;
    }

}
