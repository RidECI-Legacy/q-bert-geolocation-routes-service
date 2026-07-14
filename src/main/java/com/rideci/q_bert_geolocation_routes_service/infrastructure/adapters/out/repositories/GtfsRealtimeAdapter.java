package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.repositories;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.rideci.q_bert_geolocation_routes_service.domain.model.BusRealtimeStatus;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.out.GtfsRealtimeOutPort;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.config.GtfsRealtimeProperties;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class GtfsRealtimeAdapter implements GtfsRealtimeOutPort {

    private final WebClient gtfsRealtimeWebClient;
    private final GtfsRealtimeProperties gtfsRealtimeProperties;

    @Override
    public Mono<List<BusRealtimeStatus>> fetchVehiclePositions() {
        // Pending TM/SITP feed confirmation: feed URL and exact GTFS-Realtime entities served not yet defined.
        throw new UnsupportedOperationException("GTFS-Realtime integration pending: feed URL not yet defined");
    }

}
