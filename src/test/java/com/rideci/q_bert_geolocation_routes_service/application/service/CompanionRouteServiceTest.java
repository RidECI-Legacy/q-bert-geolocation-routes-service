package com.rideci.q_bert_geolocation_routes_service.application.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.rideci.q_bert_geolocation_routes_service.domain.model.BusRealtimeStatus;
import com.rideci.q_bert_geolocation_routes_service.domain.model.CompanionRoute;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.in.CreateCompanionRouteUseCase;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.in.FetchVehiclePositionsUseCase;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.in.GetAllCompanionRoutesUseCase;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.in.GetCompanionRouteUseCase;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.in.UpdateCompanionRouteUseCase;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class CompanionRouteServiceTest {

    @Mock
    private CreateCompanionRouteUseCase createCompanionRouteUseCase;
    @Mock
    private UpdateCompanionRouteUseCase updateCompanionRouteUseCase;
    @Mock
    private GetCompanionRouteUseCase getCompanionRouteUseCase;
    @Mock
    private GetAllCompanionRoutesUseCase getAllCompanionRoutesUseCase;
    @Mock
    private FetchVehiclePositionsUseCase fetchVehiclePositionsUseCase;

    private CompanionRouteService companionRouteService;

    @BeforeEach
    void setUp() {
        companionRouteService = new CompanionRouteService(createCompanionRouteUseCase, updateCompanionRouteUseCase,
                getCompanionRouteUseCase, getAllCompanionRoutesUseCase, fetchVehiclePositionsUseCase);
    }

    @Test
    void createCompanionRoute_delegatesToCreateCompanionRouteUseCase() {
        CompanionRoute route = CompanionRoute.builder().tripId("trip-1").build();
        CompanionRoute saved = CompanionRoute.builder().id("companion-route-1").build();
        when(createCompanionRouteUseCase.createCompanionRoute(route)).thenReturn(Mono.just(saved));

        StepVerifier.create(companionRouteService.createCompanionRoute(route)).expectNext(saved).verifyComplete();

        verify(createCompanionRouteUseCase).createCompanionRoute(route);
    }

    @Test
    void updateCompanionRoute_delegatesToUpdateCompanionRouteUseCase() {
        CompanionRoute payload = CompanionRoute.builder().build();
        CompanionRoute updated = CompanionRoute.builder().id("companion-route-1").build();
        when(updateCompanionRouteUseCase.updateCompanionRoute("companion-route-1", payload))
                .thenReturn(Mono.just(updated));

        StepVerifier.create(companionRouteService.updateCompanionRoute("companion-route-1", payload))
                .expectNext(updated)
                .verifyComplete();

        verify(updateCompanionRouteUseCase).updateCompanionRoute("companion-route-1", payload);
    }

    @Test
    void getCompanionRoute_delegatesToGetCompanionRouteUseCase() {
        CompanionRoute route = CompanionRoute.builder().id("companion-route-1").build();
        when(getCompanionRouteUseCase.getCompanionRoute("companion-route-1")).thenReturn(Mono.just(route));

        StepVerifier.create(companionRouteService.getCompanionRoute("companion-route-1"))
                .expectNext(route)
                .verifyComplete();

        verify(getCompanionRouteUseCase).getCompanionRoute("companion-route-1");
    }

    @Test
    void getAllCompanionRoutes_delegatesToGetAllCompanionRoutesUseCase() {
        CompanionRoute routeA = CompanionRoute.builder().id("a").build();
        when(getAllCompanionRoutesUseCase.getAllCompanionRoutes()).thenReturn(Flux.just(routeA));

        StepVerifier.create(companionRouteService.getAllCompanionRoutes()).expectNext(routeA).verifyComplete();

        verify(getAllCompanionRoutesUseCase).getAllCompanionRoutes();
    }

    @Test
    void fetchVehiclePositions_delegatesToFetchVehiclePositionsUseCase() {
        List<BusRealtimeStatus> statuses = List.of(BusRealtimeStatus.builder().gtfsTripId("trip-1").build());
        when(fetchVehiclePositionsUseCase.fetchVehiclePositions()).thenReturn(Mono.just(statuses));

        StepVerifier.create(companionRouteService.fetchVehiclePositions()).expectNext(statuses).verifyComplete();

        verify(fetchVehiclePositionsUseCase).fetchVehiclePositions();
    }

}
