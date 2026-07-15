package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.rideci.q_bert_geolocation_routes_service.application.service.CompanionRouteService;
import com.rideci.q_bert_geolocation_routes_service.domain.exception.CompanionRouteNotFoundException;
import com.rideci.q_bert_geolocation_routes_service.domain.model.CompanionRoute;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.advice.GlobalExceptionHandler;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.dto.request.CompanionRouteRequestDto;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.dto.request.LocationRequestDto;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.dto.response.CompanionRouteResponseDto;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.mapper.CompanionRouteControllerMapper;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@WebFluxTest(controllers = CompanionRouteController.class)
@Import(GlobalExceptionHandler.class)
class CompanionRouteControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private CompanionRouteService companionRouteService;

    @MockitoBean
    private CompanionRouteControllerMapper companionRouteControllerMapper;

    private static CompanionRouteRequestDto validCompanionRouteRequest(String tripId) {
        return CompanionRouteRequestDto.builder()
                .tripId(tripId)
                .origin(LocationRequestDto.builder().latitude(4.710989).longitude(-74.072092).build())
                .destination(LocationRequestDto.builder().latitude(4.6975).longitude(-74.0833).build())
                .build();
    }

    @Test
    void createCompanionRoute_returns201WithCreatedRoute() {
        CompanionRouteRequestDto requestDto = validCompanionRouteRequest("trip-1");
        CompanionRoute domainRoute = CompanionRoute.builder().tripId("trip-1").build();
        CompanionRoute saved = CompanionRoute.builder().id("companion-route-1").tripId("trip-1").build();
        CompanionRouteResponseDto responseDto = CompanionRouteResponseDto.builder().id("companion-route-1").tripId("trip-1").build();

        when(companionRouteControllerMapper.toDomain(any(CompanionRouteRequestDto.class))).thenReturn(domainRoute);
        when(companionRouteService.createCompanionRoute(domainRoute)).thenReturn(Mono.just(saved));
        when(companionRouteControllerMapper.toResponse(saved)).thenReturn(responseDto);

        webTestClient.post().uri("/api/v1/companion-routes")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo("companion-route-1");
    }

    @Test
    void getCompanionRoute_returns200WhenFound() {
        CompanionRoute route = CompanionRoute.builder().id("companion-route-1").build();
        CompanionRouteResponseDto responseDto = CompanionRouteResponseDto.builder().id("companion-route-1").build();

        when(companionRouteService.getCompanionRoute("companion-route-1")).thenReturn(Mono.just(route));
        when(companionRouteControllerMapper.toResponse(route)).thenReturn(responseDto);

        webTestClient.get().uri("/api/v1/companion-routes/companion-route-1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("companion-route-1");
    }

    @Test
    void getCompanionRoute_returns404WhenNotFound() {
        when(companionRouteService.getCompanionRoute("missing"))
                .thenReturn(Mono.error(new CompanionRouteNotFoundException("missing")));

        webTestClient.get().uri("/api/v1/companion-routes/missing")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404);
    }

    @Test
    void getAllCompanionRoutes_returns200WithFlux() {
        CompanionRoute routeA = CompanionRoute.builder().id("a").build();
        CompanionRoute routeB = CompanionRoute.builder().id("b").build();

        when(companionRouteService.getAllCompanionRoutes()).thenReturn(Flux.just(routeA, routeB));
        when(companionRouteControllerMapper.toResponse(routeA)).thenReturn(CompanionRouteResponseDto.builder().id("a").build());
        when(companionRouteControllerMapper.toResponse(routeB)).thenReturn(CompanionRouteResponseDto.builder().id("b").build());

        webTestClient.get().uri("/api/v1/companion-routes")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Object.class)
                .hasSize(2);
    }

    @Test
    void updateCompanionRoute_returns200WithUpdatedRoute() {
        CompanionRouteRequestDto payload = validCompanionRouteRequest("trip-1");
        CompanionRoute domainRoute = CompanionRoute.builder().tripId("trip-1").build();
        CompanionRoute updated = CompanionRoute.builder().id("companion-route-1").tripId("trip-1").build();
        CompanionRouteResponseDto responseDto = CompanionRouteResponseDto.builder().id("companion-route-1").tripId("trip-1").build();

        when(companionRouteControllerMapper.toDomain(any(CompanionRouteRequestDto.class))).thenReturn(domainRoute);
        when(companionRouteService.updateCompanionRoute(eq("companion-route-1"), any(CompanionRoute.class)))
                .thenReturn(Mono.just(updated));
        when(companionRouteControllerMapper.toResponse(updated)).thenReturn(responseDto);

        webTestClient.put().uri("/api/v1/companion-routes/companion-route-1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("companion-route-1");
    }

}
