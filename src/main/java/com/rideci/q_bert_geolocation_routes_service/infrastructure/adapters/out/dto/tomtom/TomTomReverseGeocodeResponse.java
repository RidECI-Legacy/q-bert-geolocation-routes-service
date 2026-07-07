package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.dto.tomtom;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TomTomReverseGeocodeResponse(List<AddressResult> addresses) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record AddressResult(Address address) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Address(String freeformAddress) {
    }

}
