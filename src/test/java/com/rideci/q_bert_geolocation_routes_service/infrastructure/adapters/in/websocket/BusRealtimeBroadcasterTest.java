package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.websocket;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.rideci.q_bert_geolocation_routes_service.domain.model.BusRealtimeStatus;

import reactor.test.StepVerifier;

class BusRealtimeBroadcasterTest {

    private BusRealtimeBroadcaster broadcaster;

    @BeforeEach
    void setUp() {
        broadcaster = new BusRealtimeBroadcaster();
    }

    @Test
    void subscribe_receivesStatusPublishedAfterSubscription() {
        BusRealtimeStatus status = BusRealtimeStatus.builder().gtfsTripId("gtfs-trip-1").build();

        StepVerifier.create(broadcaster.subscribe("gtfs-trip-1"))
                .then(() -> broadcaster.publish(status))
                .expectNext(status)
                .thenCancel()
                .verify();
    }

    @Test
    void subscribe_newSubscriberReceivesLastKnownValueImmediately() {
        BusRealtimeStatus status = BusRealtimeStatus.builder().gtfsTripId("gtfs-trip-1").build();
        broadcaster.publish(status);

        StepVerifier.create(broadcaster.subscribe("gtfs-trip-1"))
                .expectNext(status)
                .thenCancel()
                .verify();
    }

    @Test
    void publish_isIsolatedPerGtfsTripId() {
        BusRealtimeStatus statusForTripA = BusRealtimeStatus.builder().gtfsTripId("gtfs-trip-a").build();
        BusRealtimeStatus statusForTripB = BusRealtimeStatus.builder().gtfsTripId("gtfs-trip-b").build();

        StepVerifier.create(broadcaster.subscribe("gtfs-trip-b"))
                .then(() -> broadcaster.publish(statusForTripA))
                .then(() -> broadcaster.publish(statusForTripB))
                .expectNext(statusForTripB)
                .thenCancel()
                .verify();
    }

}
