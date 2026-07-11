package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.websocket;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URI;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.socket.HandshakeInfo;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rideci.q_bert_geolocation_routes_service.application.service.GeolocationService;
import com.rideci.q_bert_geolocation_routes_service.domain.model.RouteHistory;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.dto.response.RouteHistoryResponseDto;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.mapper.TravelTrackingControllerMapper;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class TravelReplayWebSocketHandlerTest {

    @Mock
    private GeolocationService geolocationService;

    @Mock
    private TravelTrackingControllerMapper travelTrackingControllerMapper;

    @Mock
    private WebSocketSession session;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private TravelReplayWebSocketHandler handler;

    @BeforeEach
    void setUp() {
        handler = new TravelReplayWebSocketHandler(geolocationService, travelTrackingControllerMapper, objectMapper);
    }

    private void stubHandshake(String pathAndQuery) {
        HandshakeInfo handshakeInfo = new HandshakeInfo(URI.create(pathAndQuery), new HttpHeaders(), Mono.empty(), "");
        when(session.getHandshakeInfo()).thenReturn(handshakeInfo);
    }

    @SuppressWarnings("unchecked")
    private Flux<WebSocketMessage> captureOutbound() {
        ArgumentCaptor<Flux<WebSocketMessage>> captor = ArgumentCaptor.forClass(Flux.class);
        when(session.send(captor.capture())).thenReturn(Mono.empty());
        handler.handle(session);
        return captor.getValue();
    }

    @Test
    void handle_parsesTripAndParticipantFromUri_andStreamsReplayAsJson() {
        stubHandshake("/ws/trips/trip-1/replay/participant-1");
        RouteHistory point = RouteHistory.builder().tripId("trip-1").participantId("participant-1").speed(12d).build();
        RouteHistoryResponseDto responseDto = RouteHistoryResponseDto.builder().participantId("participant-1").speed(12d).build();

        when(geolocationService.getTravelReplay("trip-1", "participant-1", 1.0)).thenReturn(Flux.just(point));
        when(travelTrackingControllerMapper.toResponse(point)).thenReturn(responseDto);
        when(session.textMessage(anyString())).thenReturn(mock(WebSocketMessage.class));

        Flux<WebSocketMessage> outbound = captureOutbound();

        StepVerifier.create(outbound)
                .expectNextCount(1)
                .verifyComplete();

        ArgumentCaptor<String> jsonCaptor = ArgumentCaptor.forClass(String.class);
        verify(session, times(1)).textMessage(jsonCaptor.capture());
        assertThat(jsonCaptor.getValue()).contains("\"participantId\":\"participant-1\"").contains("\"speed\":12.0");
    }

    @Test
    void handle_parsesSpeedQueryParam_andForwardsToService() {
        stubHandshake("/ws/trips/trip-1/replay/participant-1?speed=3.5");

        when(geolocationService.getTravelReplay("trip-1", "participant-1", 3.5)).thenReturn(Flux.empty());

        captureOutbound();

        verify(geolocationService).getTravelReplay(eq("trip-1"), eq("participant-1"), eq(3.5));
    }

    @Test
    void handle_defaultsSpeedToOne_whenQueryParamMissing() {
        stubHandshake("/ws/trips/trip-1/replay/participant-1");

        when(geolocationService.getTravelReplay("trip-1", "participant-1", 1.0)).thenReturn(Flux.empty());

        captureOutbound();

        verify(geolocationService).getTravelReplay("trip-1", "participant-1", 1.0);
    }

    @Test
    void handle_defaultsSpeedToOne_whenQueryParamIsNotPositive() {
        stubHandshake("/ws/trips/trip-1/replay/participant-1?speed=-2");

        when(geolocationService.getTravelReplay("trip-1", "participant-1", 1.0)).thenReturn(Flux.empty());

        captureOutbound();

        verify(geolocationService).getTravelReplay("trip-1", "participant-1", 1.0);
    }

    @Test
    void handle_defaultsSpeedToOne_whenQueryParamIsNotANumber() {
        stubHandshake("/ws/trips/trip-1/replay/participant-1?speed=fast");

        when(geolocationService.getTravelReplay("trip-1", "participant-1", 1.0)).thenReturn(Flux.empty());

        captureOutbound();

        verify(geolocationService).getTravelReplay("trip-1", "participant-1", 1.0);
    }

}
