package com.rideci.q_bert_geolocation_routes_service.application.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.rideci.q_bert_geolocation_routes_service.domain.model.BusRealtimeStatus;
import com.rideci.q_bert_geolocation_routes_service.domain.model.CompanionRoute;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.in.CreateCompanionRouteUseCase;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.in.FetchVehiclePositionsUseCase;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.in.GetAllCompanionRoutesUseCase;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.in.GetCompanionRouteUseCase;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.in.UpdateCompanionRouteUseCase;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CompanionRouteService implements CreateCompanionRouteUseCase, UpdateCompanionRouteUseCase,
        GetCompanionRouteUseCase, GetAllCompanionRoutesUseCase, FetchVehiclePositionsUseCase {

    private final CreateCompanionRouteUseCase createCompanionRouteUseCase;
    private final UpdateCompanionRouteUseCase updateCompanionRouteUseCase;
    private final GetCompanionRouteUseCase getCompanionRouteUseCase;
    private final GetAllCompanionRoutesUseCase getAllCompanionRoutesUseCase;
    private final FetchVehiclePositionsUseCase fetchVehiclePositionsUseCase;

    @Override
    public Mono<CompanionRoute> createCompanionRoute(CompanionRoute companionRoute) {
        return createCompanionRouteUseCase.createCompanionRoute(companionRoute);
    }

    @Override
    public Mono<CompanionRoute> updateCompanionRoute(String id, CompanionRoute companionRoute) {
        return updateCompanionRouteUseCase.updateCompanionRoute(id, companionRoute);
    }

    @Override
    public Mono<CompanionRoute> getCompanionRoute(String id) {
        return getCompanionRouteUseCase.getCompanionRoute(id);
    }

    @Override
    public Flux<CompanionRoute> getAllCompanionRoutes() {
        return getAllCompanionRoutesUseCase.getAllCompanionRoutes();
    }

    @Override
    public Mono<List<BusRealtimeStatus>> fetchVehiclePositions() {
        return fetchVehiclePositionsUseCase.fetchVehiclePositions();
    }

}
