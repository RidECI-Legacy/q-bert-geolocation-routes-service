package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.dto;

import java.time.Instant;

public record ErrorResponse(int status, String message, Instant timestamp) {

    public static ErrorResponse of(int status, String message) {
        return new ErrorResponse(status, message, Instant.now());
    }

}
