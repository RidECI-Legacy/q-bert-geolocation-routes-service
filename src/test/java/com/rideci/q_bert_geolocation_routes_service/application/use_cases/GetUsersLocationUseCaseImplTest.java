package com.rideci.q_bert_geolocation_routes_service.application.use_cases;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.rideci.q_bert_geolocation_routes_service.domain.model.TravelTracking;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.out.GeolocationRepositoryOutPort;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class GetUsersLocationUseCaseImplTest {

    @Mock
    private GeolocationRepositoryOutPort geolocationRepositoryOutPort;

    private GetUsersLocationUseCaseImpl getUsersLocationUseCase;

    @BeforeEach
    void setUp() {
        getUsersLocationUseCase = new GetUsersLocationUseCaseImpl(geolocationRepositoryOutPort);
    }

    @Test
    void getUsersLocation_delegatesToRepositoryOutPort() {
        TravelTracking trackingA = TravelTracking.builder().tripId("trip-1").participantId("a").build();
        TravelTracking trackingB = TravelTracking.builder().tripId("trip-1").participantId("b").build();

        when(geolocationRepositoryOutPort.getUsersLocation("trip-1")).thenReturn(Flux.just(trackingA, trackingB));

        StepVerifier.create(getUsersLocationUseCase.getUsersLocation("trip-1"))
                .expectNext(trackingA, trackingB)
                .verifyComplete();

        verify(geolocationRepositoryOutPort).getUsersLocation("trip-1");
    }

    @Test
    void getUsersLocation_propagatesRepositoryFailure() {
        RuntimeException failure = new RuntimeException("redis unavailable");

        when(geolocationRepositoryOutPort.getUsersLocation("trip-1")).thenReturn(Flux.error(failure));

        StepVerifier.create(getUsersLocationUseCase.getUsersLocation("trip-1"))
                .expectErrorMatches(error -> error == failure)
                .verify();
    }

}
