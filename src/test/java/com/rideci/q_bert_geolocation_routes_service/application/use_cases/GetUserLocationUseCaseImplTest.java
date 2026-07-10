package com.rideci.q_bert_geolocation_routes_service.application.use_cases;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.rideci.q_bert_geolocation_routes_service.domain.exception.TravelTrackingNotFoundException;
import com.rideci.q_bert_geolocation_routes_service.domain.model.TravelTracking;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.out.GeolocationRepositoryOutPort;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class GetUserLocationUseCaseImplTest {

    @Mock
    private GeolocationRepositoryOutPort geolocationRepositoryOutPort;

    private GetUserLocationUseCaseImpl getUserLocationUseCase;

    @BeforeEach
    void setUp() {
        getUserLocationUseCase = new GetUserLocationUseCaseImpl(geolocationRepositoryOutPort);
    }

    @Test
    void getUserLocation_delegatesToRepositoryOutPort() {
        TravelTracking tracking = TravelTracking.builder().tripId("trip-1").participantId("participant-1").build();

        when(geolocationRepositoryOutPort.getUserLocation("trip-1", "participant-1")).thenReturn(Mono.just(tracking));

        StepVerifier.create(getUserLocationUseCase.getUserLocation("trip-1", "participant-1"))
                .expectNext(tracking)
                .verifyComplete();

        verify(geolocationRepositoryOutPort).getUserLocation("trip-1", "participant-1");
    }

    @Test
    void getUserLocation_propagatesTravelTrackingNotFound() {
        TravelTrackingNotFoundException notFound = new TravelTrackingNotFoundException("trip-1", "missing");

        when(geolocationRepositoryOutPort.getUserLocation("trip-1", "missing")).thenReturn(Mono.error(notFound));

        StepVerifier.create(getUserLocationUseCase.getUserLocation("trip-1", "missing"))
                .expectErrorMatches(error -> error instanceof TravelTrackingNotFoundException
                        && ((TravelTrackingNotFoundException) error).getParticipantId().equals("missing"))
                .verify();
    }

}
