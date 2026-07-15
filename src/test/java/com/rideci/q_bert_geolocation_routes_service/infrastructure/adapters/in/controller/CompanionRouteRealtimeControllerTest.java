package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.controller;

import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.rideci.q_bert_geolocation_routes_service.application.service.CompanionRouteService;
import com.rideci.q_bert_geolocation_routes_service.domain.model.BusRealtimeStatus;
import com.rideci.q_bert_geolocation_routes_service.domain.model.CompanionRoute;
import com.rideci.q_bert_geolocation_routes_service.domain.model.TransitLeg;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.advice.GlobalExceptionHandler;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.dto.response.VehicleStatusResponseDto;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.mapper.CompanionRouteControllerMapper;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.websocket.BusRealtimeBroadcaster;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@WebFluxTest(controllers = CompanionRouteRealtimeController.class)
@Import(GlobalExceptionHandler.class)
class CompanionRouteRealtimeControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private CompanionRouteService companionRouteService;

    @MockitoBean
    private BusRealtimeBroadcaster busRealtimeBroadcaster;

    @MockitoBean
    private CompanionRouteControllerMapper companionRouteControllerMapper;

    @Test
    void getVehicleStatus_returns200WithLatestStatus() {
        TransitLeg leg = TransitLeg.builder().id("leg-1").gtfsTripId("gtfs-trip-1").build();
        CompanionRoute route = CompanionRoute.builder().id("companion-route-1").legs(List.of(leg)).build();
        BusRealtimeStatus status = BusRealtimeStatus.builder().gtfsTripId("gtfs-trip-1").latitude(4.6).longitude(-74.08).build();
        VehicleStatusResponseDto responseDto = VehicleStatusResponseDto.builder().gtfsTripId("gtfs-trip-1").build();

        when(companionRouteService.getCompanionRoute("companion-route-1")).thenReturn(Mono.just(route));
        when(busRealtimeBroadcaster.subscribe("gtfs-trip-1")).thenReturn(Flux.just(status));
        when(companionRouteControllerMapper.toResponse(status)).thenReturn(responseDto);

        webTestClient.get().uri("/api/v1/companion-routes/companion-route-1/legs/leg-1/vehicle-status")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.gtfsTripId").isEqualTo("gtfs-trip-1");
    }

    @Test
    void getVehicleStatus_returns404WhenLegNotFound() {
        CompanionRoute route = CompanionRoute.builder().id("companion-route-1").legs(List.of()).build();

        when(companionRouteService.getCompanionRoute("companion-route-1")).thenReturn(Mono.just(route));

        webTestClient.get().uri("/api/v1/companion-routes/companion-route-1/legs/missing-leg/vehicle-status")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404);
    }

}
