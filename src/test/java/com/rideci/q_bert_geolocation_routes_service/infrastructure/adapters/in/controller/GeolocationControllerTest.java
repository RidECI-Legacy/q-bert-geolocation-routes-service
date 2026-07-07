package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.rideci.q_bert_geolocation_routes_service.application.service.GeolocationService;
import com.rideci.q_bert_geolocation_routes_service.domain.exception.RouteNotFoundException;
import com.rideci.q_bert_geolocation_routes_service.domain.model.Route;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.advice.GlobalExceptionHandler;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.dto.request.GeofenceRequestDto;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.dto.request.LocationRequestDto;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.dto.request.PickUpPointRequestDto;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.dto.request.RouteRequestDto;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.dto.response.RouteResponseDto;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.mapper.RouteControllerMapper;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@WebFluxTest(controllers = GeolocationController.class)
@org.springframework.context.annotation.Import(GlobalExceptionHandler.class)
class GeolocationControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private GeolocationService geolocationService;

    @MockitoBean
    private RouteControllerMapper routeControllerMapper;

    private static RouteRequestDto validRouteRequest(String tripId) {
        return RouteRequestDto.builder()
                .tripId(tripId)
                .origin(LocationRequestDto.builder().latitude(4.710989).longitude(-74.072092).build())
                .destination(LocationRequestDto.builder().latitude(4.6975).longitude(-74.0833).build())
                .pickUpPoints(List.of(PickUpPointRequestDto.builder()
                        .passengerId("passenger-1")
                        .location(LocationRequestDto.builder().latitude(4.65).longitude(-74.05).build())
                        .geofenceConfig(GeofenceRequestDto.builder().radiusMeters(50).build())
                        .build()))
                .build();
    }

    @Test
    void createRoute_returns201WithCreatedRoute() {
        RouteRequestDto requestDto = validRouteRequest("trip-1");
        Route domainRoute = Route.builder().tripId("trip-1").build();
        Route saved = Route.builder().id("route-1").tripId("trip-1").build();
        RouteResponseDto responseDto = RouteResponseDto.builder().id("route-1").tripId("trip-1").build();

        when(routeControllerMapper.toDomain(any(RouteRequestDto.class))).thenReturn(domainRoute);
        when(geolocationService.createRoute(domainRoute)).thenReturn(Mono.just(saved));
        when(routeControllerMapper.toResponse(saved)).thenReturn(responseDto);

        webTestClient.post().uri("/api/v1/routes")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo("route-1");
    }

    @Test
    void getRoute_returns200WhenFound() {
        Route route = Route.builder().id("route-1").build();
        RouteResponseDto responseDto = RouteResponseDto.builder().id("route-1").build();

        when(geolocationService.getRoute("route-1")).thenReturn(Mono.just(route));
        when(routeControllerMapper.toResponse(route)).thenReturn(responseDto);

        webTestClient.get().uri("/api/v1/routes/route-1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("route-1");
    }

    @Test
    void getRoute_returns404WhenNotFound() {
        when(geolocationService.getRoute("missing")).thenReturn(Mono.error(new RouteNotFoundException("missing")));

        webTestClient.get().uri("/api/v1/routes/missing")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404);
    }

    @Test
    void getAllRoutes_returns200WithFlux() {
        Route routeA = Route.builder().id("a").build();
        Route routeB = Route.builder().id("b").build();

        when(geolocationService.getAllRoutes()).thenReturn(Flux.just(routeA, routeB));
        when(routeControllerMapper.toResponse(routeA)).thenReturn(RouteResponseDto.builder().id("a").build());
        when(routeControllerMapper.toResponse(routeB)).thenReturn(RouteResponseDto.builder().id("b").build());

        webTestClient.get().uri("/api/v1/routes")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Object.class)
                .hasSize(2);
    }

    @Test
    void updateRoute_returns200WithUpdatedRoute() {
        RouteRequestDto payload = validRouteRequest("trip-1");
        Route domainRoute = Route.builder().tripId("trip-1").build();
        Route updated = Route.builder().id("route-1").tripId("trip-1").build();
        RouteResponseDto responseDto = RouteResponseDto.builder().id("route-1").tripId("trip-1").build();

        when(routeControllerMapper.toDomain(any(RouteRequestDto.class))).thenReturn(domainRoute);
        when(geolocationService.updateRoute(eq("route-1"), any(Route.class))).thenReturn(Mono.just(updated));
        when(routeControllerMapper.toResponse(updated)).thenReturn(responseDto);

        webTestClient.put().uri("/api/v1/routes/route-1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("route-1");
    }

}
