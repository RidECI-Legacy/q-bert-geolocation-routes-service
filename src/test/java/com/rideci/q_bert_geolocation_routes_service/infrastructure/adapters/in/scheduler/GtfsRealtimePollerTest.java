package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.scheduler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.rideci.q_bert_geolocation_routes_service.application.service.CompanionRouteService;
import com.rideci.q_bert_geolocation_routes_service.domain.model.BusRealtimeStatus;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.websocket.BusRealtimeBroadcaster;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.config.GtfsRealtimeProperties;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class GtfsRealtimePollerTest {

    @Mock
    private CompanionRouteService companionRouteService;

    @Mock
    private BusRealtimeBroadcaster broadcaster;

    @Mock
    private GtfsRealtimeProperties gtfsRealtimeProperties;

    private GtfsRealtimePoller poller;

    @BeforeEach
    void setUp() {
        poller = new GtfsRealtimePoller(companionRouteService, broadcaster, gtfsRealtimeProperties);
    }

    @Test
    void poll_publishesEachFetchedStatus() {
        BusRealtimeStatus statusA = BusRealtimeStatus.builder().gtfsTripId("gtfs-trip-a").build();
        BusRealtimeStatus statusB = BusRealtimeStatus.builder().gtfsTripId("gtfs-trip-b").build();
        when(companionRouteService.fetchVehiclePositions()).thenReturn(Mono.just(List.of(statusA, statusB)));

        StepVerifier.create(poller.poll()).verifyComplete();

        verify(broadcaster).publish(statusA);
        verify(broadcaster).publish(statusB);
    }

    @Test
    void poll_resumesWhenFetchFailsWithReactiveError() {
        when(companionRouteService.fetchVehiclePositions())
                .thenReturn(Mono.error(new RuntimeException("feed unavailable")));

        StepVerifier.create(poller.poll()).verifyComplete();

        verify(broadcaster, never()).publish(any());
    }

    @Test
    void poll_resumesWhenFetchThrowsSynchronously() {
        when(companionRouteService.fetchVehiclePositions())
                .thenThrow(new UnsupportedOperationException("GTFS-Realtime integration pending"));

        StepVerifier.create(poller.poll()).verifyComplete();

        verify(broadcaster, never()).publish(any());
    }

}
