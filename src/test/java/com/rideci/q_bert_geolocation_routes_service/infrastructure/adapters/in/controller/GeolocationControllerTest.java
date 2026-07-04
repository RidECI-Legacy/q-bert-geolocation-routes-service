package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.rideci.q_bert_geolocation_routes_service.domain.exception.RouteNotFoundException;
import com.rideci.q_bert_geolocation_routes_service.domain.model.Route;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.in.CreateRouteUseCase;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.in.GetAllRoutesUseCase;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.in.GetRouteUseCase;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.in.UpdateRouteUseCase;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.advice.GlobalExceptionHandler;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@WebFluxTest(controllers = GeolocationController.class)
@org.springframework.context.annotation.Import(GlobalExceptionHandler.class)
class GeolocationControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private CreateRouteUseCase createRouteUseCase;

    @MockitoBean
    private UpdateRouteUseCase updateRouteUseCase;

    @MockitoBean
    private GetRouteUseCase getRouteUseCase;

    @MockitoBean
    private GetAllRoutesUseCase getAllRoutesUseCase;

    @Test
    void createRoute_returns201WithCreatedRoute() {
        Route route = Route.builder().tripId("trip-1").build();
        Route saved = Route.builder().id("route-1").tripId("trip-1").build();
        when(createRouteUseCase.createRoute(any(Route.class))).thenReturn(Mono.just(saved));

        webTestClient.post().uri("/api/v1/routes")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(route)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo("route-1");
    }

    @Test
    void getRoute_returns200WhenFound() {
        Route route = Route.builder().id("route-1").build();
        when(getRouteUseCase.getRoute("route-1")).thenReturn(Mono.just(route));

        webTestClient.get().uri("/api/v1/routes/route-1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("route-1");
    }

    @Test
    void getRoute_returns404WhenNotFound() {
        when(getRouteUseCase.getRoute("missing")).thenReturn(Mono.error(new RouteNotFoundException("missing")));

        webTestClient.get().uri("/api/v1/routes/missing")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404);
    }

    @Test
    void getAllRoutes_returns200WithFlux() {
        when(getAllRoutesUseCase.getAllRoutes())
                .thenReturn(Flux.just(Route.builder().id("a").build(), Route.builder().id("b").build()));

        webTestClient.get().uri("/api/v1/routes")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Route.class)
                .hasSize(2);
    }

    @Test
    void updateRoute_returns200WithUpdatedRoute() {
        Route payload = Route.builder().tripId("trip-1").build();
        Route updated = Route.builder().id("route-1").tripId("trip-1").build();
        when(updateRouteUseCase.updateRoute(eq("route-1"), any(Route.class))).thenReturn(Mono.just(updated));

        webTestClient.put().uri("/api/v1/routes/route-1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("route-1");
    }

}
