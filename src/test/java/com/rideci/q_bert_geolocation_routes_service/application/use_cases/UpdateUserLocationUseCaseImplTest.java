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

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class UpdateUserLocationUseCaseImplTest {

    @Mock
    private GeolocationRepositoryOutPort geolocationRepositoryOutPort;

    private UpdateUserLocationUseCaseImpl updateUserLocationUseCase;

    @BeforeEach
    void setUp() {
        updateUserLocationUseCase = new UpdateUserLocationUseCaseImpl(geolocationRepositoryOutPort);
    }

    @Test
    void updateUserLocation_delegatesToRepositoryOutPort() {
        TravelTracking incoming = TravelTracking.builder().participantId("participant-1").build();
        TravelTracking updated = TravelTracking.builder().tripId("trip-1").participantId("participant-1").build();

        when(geolocationRepositoryOutPort.updateUserLocation("trip-1", incoming)).thenReturn(Mono.just(updated));

        StepVerifier.create(updateUserLocationUseCase.updateUserLocation("trip-1", incoming))
                .expectNext(updated)
                .verifyComplete();

        verify(geolocationRepositoryOutPort).updateUserLocation("trip-1", incoming);
    }

    @Test
    void updateUserLocation_propagatesRepositoryFailure() {
        TravelTracking incoming = TravelTracking.builder().participantId("participant-1").build();
        RuntimeException failure = new RuntimeException("redis unavailable");

        when(geolocationRepositoryOutPort.updateUserLocation("trip-1", incoming)).thenReturn(Mono.error(failure));

        StepVerifier.create(updateUserLocationUseCase.updateUserLocation("trip-1", incoming))
                .expectErrorMatches(error -> error == failure)
                .verify();
    }

}
