package com.rideci.q_bert_geolocation_routes_service.application.use_cases;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.rideci.q_bert_geolocation_routes_service.domain.model.TrackingConfiguration;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.out.GeolocationRepositoryOutPort;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class UpdateConfigurableIntervalsUseCaseImplTest {

    @Mock
    private GeolocationRepositoryOutPort geolocationRepositoryOutPort;

    private UpdateConfigurableIntervalsUseCaseImpl updateConfigurableIntervalsUseCase;

    @BeforeEach
    void setUp() {
        updateConfigurableIntervalsUseCase = new UpdateConfigurableIntervalsUseCaseImpl(geolocationRepositoryOutPort);
    }

    @Test
    void updateConfigurableInterval_delegatesToRepositoryOutPort() {
        TrackingConfiguration configuration = TrackingConfiguration.builder()
                .tripId("trip-1").participantId("participant-1").updateIntervalSeconds(10).build();

        when(geolocationRepositoryOutPort.updateConfigurableInterval("trip-1", "participant-1", 10))
                .thenReturn(Mono.just(configuration));

        StepVerifier.create(updateConfigurableIntervalsUseCase.updateConfigurableInterval("trip-1", "participant-1", 10))
                .expectNext(configuration)
                .verifyComplete();

        verify(geolocationRepositoryOutPort).updateConfigurableInterval("trip-1", "participant-1", 10);
    }

    @Test
    void updateConfigurableInterval_propagatesRepositoryFailure() {
        RuntimeException failure = new RuntimeException("redis unavailable");

        when(geolocationRepositoryOutPort.updateConfigurableInterval("trip-1", "participant-1", 10))
                .thenReturn(Mono.error(failure));

        StepVerifier.create(updateConfigurableIntervalsUseCase.updateConfigurableInterval("trip-1", "participant-1", 10))
                .expectErrorMatches(error -> error == failure)
                .verify();
    }

}
