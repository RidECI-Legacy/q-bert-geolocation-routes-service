package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.controller;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.rideci.q_bert_geolocation_routes_service.application.service.GeolocationService;
import com.rideci.q_bert_geolocation_routes_service.domain.exception.TravelTrackingNotFoundException;
import com.rideci.q_bert_geolocation_routes_service.domain.model.RouteHistory;
import com.rideci.q_bert_geolocation_routes_service.domain.model.TrackingConfiguration;
import com.rideci.q_bert_geolocation_routes_service.domain.model.TravelTracking;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.advice.GlobalExceptionHandler;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.dto.request.UpdateTrackingIntervalRequestDto;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.dto.response.RouteHistoryResponseDto;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.dto.response.TrackingConfigurationResponseDto;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.dto.response.TravelTrackingResponseDto;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.mapper.TravelTrackingControllerMapper;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@WebFluxTest(controllers = TravelTrackingController.class)
@org.springframework.context.annotation.Import(GlobalExceptionHandler.class)
class TravelTrackingControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private GeolocationService geolocationService;

    @MockitoBean
    private TravelTrackingControllerMapper travelTrackingControllerMapper;

    @Test
    void getUserLocation_returns200WhenFound() {
        TravelTracking tracking = TravelTracking.builder().tripId("trip-1").participantId("participant-1").build();
        TravelTrackingResponseDto responseDto = TravelTrackingResponseDto.builder()
                .tripId("trip-1").participantId("participant-1").build();

        when(geolocationService.getUserLocation("trip-1", "participant-1")).thenReturn(Mono.just(tracking));
        when(travelTrackingControllerMapper.toResponse(tracking)).thenReturn(responseDto);

        webTestClient.get().uri("/api/v1/routes/trip-1/tracking/participant-1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.tripId").isEqualTo("trip-1")
                .jsonPath("$.participantId").isEqualTo("participant-1");
    }

    @Test
    void getUserLocation_returns404WhenNotTracked() {
        when(geolocationService.getUserLocation("trip-1", "missing"))
                .thenReturn(Mono.error(new TravelTrackingNotFoundException("trip-1", "missing")));

        webTestClient.get().uri("/api/v1/routes/trip-1/tracking/missing")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404);
    }

    @Test
    void getUsersLocation_returns200WithFluxOfParticipants() {
        TravelTracking trackingA = TravelTracking.builder().tripId("trip-1").participantId("a").build();
        TravelTracking trackingB = TravelTracking.builder().tripId("trip-1").participantId("b").build();

        when(geolocationService.getUsersLocation("trip-1")).thenReturn(Flux.just(trackingA, trackingB));
        when(travelTrackingControllerMapper.toResponse(trackingA))
                .thenReturn(TravelTrackingResponseDto.builder().tripId("trip-1").participantId("a").build());
        when(travelTrackingControllerMapper.toResponse(trackingB))
                .thenReturn(TravelTrackingResponseDto.builder().tripId("trip-1").participantId("b").build());

        webTestClient.get().uri("/api/v1/routes/trip-1/tracking")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Object.class)
                .hasSize(2);
    }

    @Test
    void getTravelReplay_returns200WithRecordedHistory() {
        RouteHistory point = RouteHistory.builder().tripId("trip-1").participantId("participant-1").build();
        RouteHistoryResponseDto responseDto = RouteHistoryResponseDto.builder().participantId("participant-1").build();

        when(geolocationService.getTravelReplay("trip-1", "participant-1", 0)).thenReturn(Flux.just(point));
        when(travelTrackingControllerMapper.toResponse(point)).thenReturn(responseDto);

        webTestClient.get().uri("/api/v1/routes/trip-1/tracking/participant-1/replay")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Object.class)
                .hasSize(1);
    }

    @Test
    void updateConfigurableInterval_returns200WithUpdatedConfiguration() {
        UpdateTrackingIntervalRequestDto request = UpdateTrackingIntervalRequestDto.builder()
                .updateIntervalSeconds(10).build();
        TrackingConfiguration configuration = TrackingConfiguration.builder()
                .tripId("trip-1").participantId("participant-1").updateIntervalSeconds(10).build();
        TrackingConfigurationResponseDto responseDto = TrackingConfigurationResponseDto.builder()
                .tripId("trip-1").participantId("participant-1").updateIntervalSeconds(10).build();

        when(geolocationService.updateConfigurableInterval("trip-1", "participant-1", 10))
                .thenReturn(Mono.just(configuration));
        when(travelTrackingControllerMapper.toResponse(configuration)).thenReturn(responseDto);

        webTestClient.put().uri("/api/v1/routes/trip-1/tracking/participant-1/interval")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.updateIntervalSeconds").isEqualTo(10);
    }

    @Test
    void updateConfigurableInterval_returns400WhenIntervalIsNotPositive() {
        UpdateTrackingIntervalRequestDto request = UpdateTrackingIntervalRequestDto.builder()
                .updateIntervalSeconds(0).build();

        webTestClient.put().uri("/api/v1/routes/trip-1/tracking/participant-1/interval")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400);
    }

}
