package com.rideci.q_bert_geolocation_routes_service.domain.exception;

public class TomTomIntegrationException extends RuntimeException {

    public TomTomIntegrationException(String message, Throwable cause) {
        super(message, cause);
    }

    public TomTomIntegrationException(String message) {
        super(message);
    }

}
