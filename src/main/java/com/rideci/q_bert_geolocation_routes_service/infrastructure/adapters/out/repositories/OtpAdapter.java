package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.repositories;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.rideci.q_bert_geolocation_routes_service.domain.model.Location;
import com.rideci.q_bert_geolocation_routes_service.domain.model.TransitItinerary;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.out.OtpOutPort;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.config.OtpProperties;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class OtpAdapter implements OtpOutPort {

    private final WebClient otpWebClient;
    private final OtpProperties otpProperties;

    @Override
    public Mono<TransitItinerary> planTrip(Location origin, Location destination, LocalDateTime departureTime) {
        // Pending OTP deployment: base URL and REST-vs-GraphQL API shape not yet confirmed.
        throw new UnsupportedOperationException("OTP integration pending: endpoint/API details not yet defined");
    }

}
