package com.rideci.q_bert_geolocation_routes_service.application.use_cases;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.rideci.q_bert_geolocation_routes_service.domain.model.Route;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.out.GeolocationRepositoryOutPort;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class CreateRouteUseCaseImplTest {

    @Mock
    private GeolocationRepositoryOutPort geolocationRepositoryOutPort;

    private CreateRouteUseCaseImpl createRouteUseCase;

    @BeforeEach
    void setUp() {
        createRouteUseCase = new CreateRouteUseCaseImpl(geolocationRepositoryOutPort);
    }

    @Test
    void createRoute_delegatesToRepositoryOutPort() {
        Route inputRoute = Route.builder().tripId("trip-1").build();
        Route savedRoute = Route.builder().id("route-1").tripId("trip-1").build();

        when(geolocationRepositoryOutPort.save(inputRoute)).thenReturn(Mono.just(savedRoute));

        StepVerifier.create(createRouteUseCase.createRoute(inputRoute))
                .expectNext(savedRoute)
                .verifyComplete();

        verify(geolocationRepositoryOutPort).save(inputRoute);
    }

    @Test
    void createRoute_propagatesRepositoryFailure() {
        Route inputRoute = Route.builder().tripId("trip-1").build();
        RuntimeException failure = new RuntimeException("persistence unavailable");

        when(geolocationRepositoryOutPort.save(inputRoute)).thenReturn(Mono.error(failure));

        StepVerifier.create(createRouteUseCase.createRoute(inputRoute))
                .expectErrorMatches(error -> error == failure)
                .verify();
    }

}
