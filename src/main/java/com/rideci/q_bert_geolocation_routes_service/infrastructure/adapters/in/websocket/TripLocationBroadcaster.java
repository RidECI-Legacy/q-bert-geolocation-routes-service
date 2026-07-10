package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.websocket;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.rideci.q_bert_geolocation_routes_service.domain.model.TravelTracking;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

// In-memory fan-out of live location updates per trip, scoped to a single service instance.
@Component
public class TripLocationBroadcaster {

    private final Map<String, Sinks.Many<TravelTracking>> sinksByTrip = new ConcurrentHashMap<>();

    public void publish(String tripId, TravelTracking tracking) {
        sinkFor(tripId).tryEmitNext(tracking);
    }

    public Flux<TravelTracking> subscribe(String tripId) {
        return sinkFor(tripId).asFlux();
    }

    private Sinks.Many<TravelTracking> sinkFor(String tripId) {
        return sinksByTrip.computeIfAbsent(tripId, id -> Sinks.many().multicast().onBackpressureBuffer());
    }

}
