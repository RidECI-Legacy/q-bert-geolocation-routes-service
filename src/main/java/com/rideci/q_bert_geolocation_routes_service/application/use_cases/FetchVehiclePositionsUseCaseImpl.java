package com.rideci.q_bert_geolocation_routes_service.application.use_cases;

import java.util.List;

import com.rideci.q_bert_geolocation_routes_service.application.annotation.UseCase;
import com.rideci.q_bert_geolocation_routes_service.domain.model.BusRealtimeStatus;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.in.FetchVehiclePositionsUseCase;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.out.GtfsRealtimeOutPort;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@UseCase
@RequiredArgsConstructor
public class FetchVehiclePositionsUseCaseImpl implements FetchVehiclePositionsUseCase {

    private final GtfsRealtimeOutPort gtfsRealtimeOutPort;

    @Override
    public Mono<List<BusRealtimeStatus>> fetchVehiclePositions() {
        return gtfsRealtimeOutPort.fetchVehiclePositions();
    }

}
