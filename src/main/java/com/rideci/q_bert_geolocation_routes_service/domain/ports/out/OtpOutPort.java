package com.rideci.q_bert_geolocation_routes_service.domain.ports.out;

import java.time.LocalDateTime;

import com.rideci.q_bert_geolocation_routes_service.domain.model.Location;
import com.rideci.q_bert_geolocation_routes_service.domain.model.TransitItinerary;

import reactor.core.publisher.Mono;

public interface OtpOutPort {

    Mono<TransitItinerary> planTrip(Location origin, Location destination, LocalDateTime departureTime);

}
