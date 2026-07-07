package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.dto.tomtom;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TomTomGeocodeResponse(List<Result> results) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Result(Position position) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Position(double lat, double lon) {
    }

}
