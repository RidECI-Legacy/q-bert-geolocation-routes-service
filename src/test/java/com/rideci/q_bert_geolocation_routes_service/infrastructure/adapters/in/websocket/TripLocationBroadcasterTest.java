package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.websocket;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.rideci.q_bert_geolocation_routes_service.domain.model.TravelTracking;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

class TripLocationBroadcasterTest {

    private TripLocationBroadcaster broadcaster;

    @BeforeEach
    void setUp() {
        broadcaster = new TripLocationBroadcaster();
    }

    @Test
    void subscribe_receivesTrackingPublishedAfterSubscription() {
        TravelTracking tracking = TravelTracking.builder().tripId("trip-1").participantId("participant-1").build();

        StepVerifier.create(broadcaster.subscribe("trip-1"))
                .then(() -> broadcaster.publish("trip-1", tracking))
                .expectNext(tracking)
                .thenCancel()
                .verify();
    }

    @Test
    void subscribe_multipleSubscribersToSameTrip_allReceivePublishedValue() {
        TravelTracking tracking = TravelTracking.builder().tripId("trip-1").participantId("participant-1").build();
        Flux<TravelTracking> merged = Flux.merge(broadcaster.subscribe("trip-1"), broadcaster.subscribe("trip-1"));

        StepVerifier.create(merged)
                .then(() -> broadcaster.publish("trip-1", tracking))
                .expectNext(tracking, tracking)
                .thenCancel()
                .verify();
    }

    @Test
    void publish_isIsolatedPerTrip() {
        TravelTracking trackingForTripA = TravelTracking.builder().tripId("trip-a").participantId("participant-a").build();
        TravelTracking trackingForTripB = TravelTracking.builder().tripId("trip-b").participantId("participant-b").build();

        StepVerifier.create(broadcaster.subscribe("trip-b"))
                .then(() -> broadcaster.publish("trip-a", trackingForTripA))
                .then(() -> broadcaster.publish("trip-b", trackingForTripB))
                .expectNext(trackingForTripB)
                .thenCancel()
                .verify();
    }

}
