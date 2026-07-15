package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.websocket;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.rideci.q_bert_geolocation_routes_service.domain.model.BusRealtimeStatus;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

// In-memory fan-out of live bus positions per GTFS trip id, scoped to a single service instance
// (same reasoning as TripLocationBroadcaster). Uses a replay(1) sink so a new subscriber (a WS
// connection or the REST snapshot endpoint) immediately gets the last known position instead of
// waiting for the next poll tick.
@Component
public class BusRealtimeBroadcaster {

    private final Map<String, Sinks.Many<BusRealtimeStatus>> sinksByGtfsTripId = new ConcurrentHashMap<>();

    public void publish(BusRealtimeStatus status) {
        sinkFor(status.getGtfsTripId()).tryEmitNext(status);
    }

    public Flux<BusRealtimeStatus> subscribe(String gtfsTripId) {
        return sinkFor(gtfsTripId).asFlux();
    }

    private Sinks.Many<BusRealtimeStatus> sinkFor(String gtfsTripId) {
        return sinksByGtfsTripId.computeIfAbsent(gtfsTripId, id -> Sinks.many().replay().limit(1));
    }

}
