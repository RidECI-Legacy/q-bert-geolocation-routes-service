package com.rideci.q_bert_geolocation_routes_service.application.use_cases;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

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
class UpdateUsersLocationUseCaseImplTest {

    @Mock
    private GeolocationRepositoryOutPort geolocationRepositoryOutPort;

    private UpdateUsersLocationUseCaseImpl updateUsersLocationUseCase;

    @BeforeEach
    void setUp() {
        updateUsersLocationUseCase = new UpdateUsersLocationUseCaseImpl(geolocationRepositoryOutPort);
    }

    @Test
    void updateUsersLocation_delegatesToRepositoryOutPort() {
        List<TravelTracking> incoming = List.of(
                TravelTracking.builder().participantId("a").build(),
                TravelTracking.builder().participantId("b").build());
        TravelTracking updatedA = TravelTracking.builder().tripId("trip-1").participantId("a").build();
        TravelTracking updatedB = TravelTracking.builder().tripId("trip-1").participantId("b").build();

        when(geolocationRepositoryOutPort.updateUsersLocation("trip-1", incoming))
                .thenReturn(Flux.just(updatedA, updatedB));

        StepVerifier.create(updateUsersLocationUseCase.updateUsersLocation("trip-1", incoming))
                .expectNext(updatedA, updatedB)
                .verifyComplete();

        verify(geolocationRepositoryOutPort).updateUsersLocation("trip-1", incoming);
    }

    @Test
    void updateUsersLocation_propagatesRepositoryFailure() {
        List<TravelTracking> incoming = List.of(TravelTracking.builder().participantId("a").build());
        RuntimeException failure = new RuntimeException("redis unavailable");

        when(geolocationRepositoryOutPort.updateUsersLocation("trip-1", incoming)).thenReturn(Flux.error(failure));

        StepVerifier.create(updateUsersLocationUseCase.updateUsersLocation("trip-1", incoming))
                .expectErrorMatches(error -> error == failure)
                .verify();
    }

}
