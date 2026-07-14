package com.rideci.q_bert_geolocation_routes_service.domain.ports.out;

import java.util.List;

import com.rideci.q_bert_geolocation_routes_service.domain.model.BusRealtimeStatus;

import reactor.core.publisher.Mono;

public interface GtfsRealtimeOutPort {

    Mono<List<BusRealtimeStatus>> fetchVehiclePositions();

}
