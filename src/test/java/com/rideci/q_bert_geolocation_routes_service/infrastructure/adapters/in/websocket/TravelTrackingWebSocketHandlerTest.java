package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.websocket;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
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
import com.rideci.q_bert_geolocation_routes_service.domain.model.TravelTracking;
import com.rideci.q_bert_geolocation_routes_service.domain.model.enums.ParticipantRole;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.dto.request.TravelTrackingUpdateDto;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.dto.response.TravelTrackingResponseDto;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.mapper.TravelTrackingControllerMapper;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class TravelTrackingWebSocketHandlerTest {

    @Mock
    private GeolocationService geolocationService;

    @Mock
    private TripLocationBroadcaster broadcaster;

    @Mock
    private TravelTrackingControllerMapper travelTrackingControllerMapper;

    @Mock
    private WebSocketSession session;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private TravelTrackingWebSocketHandler handler;

    @BeforeEach
    void setUp() {
        handler = new TravelTrackingWebSocketHandler(geolocationService, broadcaster, travelTrackingControllerMapper,
                objectMapper);
    }

    private void stubHandshake(String pathAndQuery) {
        HandshakeInfo handshakeInfo = new HandshakeInfo(URI.create(pathAndQuery), new HttpHeaders(), Mono.empty(), "");
        when(session.getHandshakeInfo()).thenReturn(handshakeInfo);
    }

    private WebSocketMessage inboundMessage(String payload) {
        WebSocketMessage message = mock(WebSocketMessage.class);
        when(message.getPayloadAsText()).thenReturn(payload);
        return message;
    }

    @SuppressWarnings("unchecked")
    private Flux<WebSocketMessage> captureOutbound() {
        ArgumentCaptor<Flux<WebSocketMessage>> captor = ArgumentCaptor.forClass(Flux.class);
        when(session.send(captor.capture())).thenReturn(Mono.empty());
        handler.handle(session).block();
        return captor.getValue();
    }

    @Test
    void handle_subscribesToBroadcasterForTripAndStreamsUpdatesAsJson() {
        stubHandshake("/ws/trips/trip-1/tracking/participant-1");
        when(session.receive()).thenReturn(Flux.empty());

        TravelTracking tracking = TravelTracking.builder().tripId("trip-1").participantId("participant-2").build();
        TravelTrackingResponseDto responseDto = TravelTrackingResponseDto.builder()
                .tripId("trip-1").participantId("participant-2").build();

        when(broadcaster.subscribe("trip-1")).thenReturn(Flux.just(tracking));
        when(travelTrackingControllerMapper.toResponse(tracking)).thenReturn(responseDto);
        when(session.textMessage(anyString())).thenReturn(mock(WebSocketMessage.class));

        Flux<WebSocketMessage> outbound = captureOutbound();

        StepVerifier.create(outbound)
                .expectNextCount(1)
                .verifyComplete();

        ArgumentCaptor<String> jsonCaptor = ArgumentCaptor.forClass(String.class);
        verify(session, times(1)).textMessage(jsonCaptor.capture());
        assertThat(jsonCaptor.getValue()).contains("\"participantId\":\"participant-2\"");
    }

    @Test
    void handle_processesInboundUpdate_persistsAndPublishesToBroadcaster() throws Exception {
        stubHandshake("/ws/trips/trip-1/tracking/participant-1");
        when(broadcaster.subscribe("trip-1")).thenReturn(Flux.empty());
        when(session.send(any())).thenReturn(Mono.empty());

        TravelTrackingUpdateDto update = TravelTrackingUpdateDto.builder()
                .latitude(4.6).longitude(-74.08).speed(30d).heading(90d)
                .participantRole(ParticipantRole.DRIVER)
                .build();
        String payload = objectMapper.writeValueAsString(update);
        WebSocketMessage message = inboundMessage(payload);

        when(session.receive()).thenReturn(Flux.just(message));

        TravelTracking persisted = TravelTracking.builder().tripId("trip-1").participantId("participant-1").build();
        when(geolocationService.updateUserLocation(eq("trip-1"), any(TravelTracking.class)))
                .thenReturn(Mono.just(persisted));

        handler.handle(session).block();

        ArgumentCaptor<TravelTracking> trackingCaptor = ArgumentCaptor.forClass(TravelTracking.class);
        verify(geolocationService).updateUserLocation(eq("trip-1"), trackingCaptor.capture());
        TravelTracking sent = trackingCaptor.getValue();
        assertThat(sent.getTripId()).isEqualTo("trip-1");
        assertThat(sent.getParticipantId()).isEqualTo("participant-1");
        assertThat(sent.getSpeed()).isEqualTo(30d);
        assertThat(sent.getHeading()).isEqualTo(90d);
        assertThat(sent.getParticipantRole()).isEqualTo(ParticipantRole.DRIVER);
        assertThat(sent.getCurrentLocation().getLatitude()).isEqualTo(4.6);
        assertThat(sent.getCurrentLocation().getLongitude()).isEqualTo(-74.08);

        verify(broadcaster).publish("trip-1", persisted);
    }

    @Test
    void handle_discardsInvalidInboundPayload_andKeepsProcessingSubsequentUpdates() throws Exception {
        stubHandshake("/ws/trips/trip-1/tracking/participant-1");
        when(broadcaster.subscribe("trip-1")).thenReturn(Flux.empty());
        when(session.send(any())).thenReturn(Mono.empty());

        TravelTrackingUpdateDto validUpdate = TravelTrackingUpdateDto.builder()
                .latitude(1).longitude(1).speed(5d).heading(0d)
                .participantRole(ParticipantRole.PASSENGER)
                .build();
        String validPayload = objectMapper.writeValueAsString(validUpdate);
        WebSocketMessage invalidMessage = inboundMessage("{not-valid-json");
        WebSocketMessage validMessage = inboundMessage(validPayload);

        when(session.receive()).thenReturn(Flux.just(invalidMessage, validMessage));
        when(geolocationService.updateUserLocation(eq("trip-1"), any(TravelTracking.class)))
                .thenReturn(Mono.just(TravelTracking.builder().tripId("trip-1").participantId("participant-1").build()));

        StepVerifier.create(handler.handle(session)).verifyComplete();

        verify(geolocationService, times(1)).updateUserLocation(eq("trip-1"), any(TravelTracking.class));
        verify(broadcaster, times(1)).publish(eq("trip-1"), any(TravelTracking.class));
    }

    @Test
    void handle_doesNotPublish_whenUpdateUserLocationFails() {
        stubHandshake("/ws/trips/trip-1/tracking/participant-1");
        when(broadcaster.subscribe("trip-1")).thenReturn(Flux.empty());
        when(session.send(any())).thenReturn(Mono.empty());

        TravelTrackingUpdateDto update = TravelTrackingUpdateDto.builder()
                .latitude(1).longitude(1).speed(5d).heading(0d)
                .participantRole(ParticipantRole.PASSENGER)
                .build();

        when(session.receive()).thenReturn(Flux.defer(() -> {
            try {
                return Flux.just(inboundMessage(objectMapper.writeValueAsString(update)));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }));
        when(geolocationService.updateUserLocation(eq("trip-1"), any(TravelTracking.class)))
                .thenReturn(Mono.error(new RuntimeException("persistence unavailable")));

        StepVerifier.create(handler.handle(session)).verifyComplete();

        verify(broadcaster, never()).publish(anyString(), any(TravelTracking.class));
    }

}
