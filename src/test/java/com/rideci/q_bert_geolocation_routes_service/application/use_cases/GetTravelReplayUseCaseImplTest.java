package com.rideci.q_bert_geolocation_routes_service.application.use_cases;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.rideci.q_bert_geolocation_routes_service.domain.model.RouteHistory;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.out.GeolocationRepositoryOutPort;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class GetTravelReplayUseCaseImplTest {

    @Mock
    private GeolocationRepositoryOutPort geolocationRepositoryOutPort;

    private GetTravelReplayUseCaseImpl getTravelReplayUseCase;

    @BeforeEach
    void setUp() {
        getTravelReplayUseCase = new GetTravelReplayUseCaseImpl(geolocationRepositoryOutPort);
    }

    @Test
    void getTravelReplay_delegatesToRepositoryOutPort() {
        RouteHistory pointA = RouteHistory.builder().id("h1").tripId("trip-1").participantId("participant-1").build();
        RouteHistory pointB = RouteHistory.builder().id("h2").tripId("trip-1").participantId("participant-1").build();

        when(geolocationRepositoryOutPort.getTravelReplay("trip-1", "participant-1", 2.0))
                .thenReturn(Flux.just(pointA, pointB));

        StepVerifier.create(getTravelReplayUseCase.getTravelReplay("trip-1", "participant-1", 2.0))
                .expectNext(pointA, pointB)
                .verifyComplete();

        verify(geolocationRepositoryOutPort).getTravelReplay("trip-1", "participant-1", 2.0);
    }

    @Test
    void getTravelReplay_propagatesRepositoryFailure() {
        RuntimeException failure = new RuntimeException("mongo unavailable");

        when(geolocationRepositoryOutPort.getTravelReplay("trip-1", "participant-1", 1.0))
                .thenReturn(Flux.error(failure));

        StepVerifier.create(getTravelReplayUseCase.getTravelReplay("trip-1", "participant-1", 1.0))
                .expectErrorMatches(error -> error == failure)
                .verify();
    }

}
