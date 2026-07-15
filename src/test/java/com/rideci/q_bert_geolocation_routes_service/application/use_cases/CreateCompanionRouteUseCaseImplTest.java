package com.rideci.q_bert_geolocation_routes_service.application.use_cases;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.rideci.q_bert_geolocation_routes_service.domain.model.CompanionRoute;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.out.CompanionRouteRepositoryOutPort;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class CreateCompanionRouteUseCaseImplTest {

    @Mock
    private CompanionRouteRepositoryOutPort companionRouteRepositoryOutPort;

    private CreateCompanionRouteUseCaseImpl createCompanionRouteUseCase;

    @BeforeEach
    void setUp() {
        createCompanionRouteUseCase = new CreateCompanionRouteUseCaseImpl(companionRouteRepositoryOutPort);
    }

    @Test
    void createCompanionRoute_delegatesToRepositoryOutPort() {
        CompanionRoute inputRoute = CompanionRoute.builder().tripId("trip-1").build();
        CompanionRoute savedRoute = CompanionRoute.builder().id("companion-route-1").tripId("trip-1").build();

        when(companionRouteRepositoryOutPort.save(inputRoute)).thenReturn(Mono.just(savedRoute));

        StepVerifier.create(createCompanionRouteUseCase.createCompanionRoute(inputRoute))
                .expectNext(savedRoute)
                .verifyComplete();

        verify(companionRouteRepositoryOutPort).save(inputRoute);
    }

    @Test
    void createCompanionRoute_propagatesRepositoryFailure() {
        CompanionRoute inputRoute = CompanionRoute.builder().tripId("trip-1").build();
        RuntimeException failure = new RuntimeException("persistence unavailable");

        when(companionRouteRepositoryOutPort.save(inputRoute)).thenReturn(Mono.error(failure));

        StepVerifier.create(createCompanionRouteUseCase.createCompanionRoute(inputRoute))
                .expectErrorMatches(error -> error == failure)
                .verify();
    }

}
