package com.rideci.q_bert_geolocation_routes_service.domain.ports.in;

import java.util.List;

import com.rideci.q_bert_geolocation_routes_service.domain.model.BusRealtimeStatus;

import reactor.core.publisher.Mono;

public interface FetchVehiclePositionsUseCase {

    Mono<List<BusRealtimeStatus>> fetchVehiclePositions();

}
