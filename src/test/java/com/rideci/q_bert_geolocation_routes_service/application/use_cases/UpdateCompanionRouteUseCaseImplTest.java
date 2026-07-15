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
class UpdateCompanionRouteUseCaseImplTest {

    @Mock
    private CompanionRouteRepositoryOutPort companionRouteRepositoryOutPort;

    private UpdateCompanionRouteUseCaseImpl updateCompanionRouteUseCase;

    @BeforeEach
    void setUp() {
        updateCompanionRouteUseCase = new UpdateCompanionRouteUseCaseImpl(companionRouteRepositoryOutPort);
    }

    @Test
    void updateCompanionRoute_delegatesToRepositoryOutPort() {
        CompanionRoute payload = CompanionRoute.builder().tripId("trip-1").build();
        CompanionRoute updated = CompanionRoute.builder().id("companion-route-1").tripId("trip-1").build();

        when(companionRouteRepositoryOutPort.update("companion-route-1", payload)).thenReturn(Mono.just(updated));

        StepVerifier.create(updateCompanionRouteUseCase.updateCompanionRoute("companion-route-1", payload))
                .expectNext(updated)
                .verifyComplete();

        verify(companionRouteRepositoryOutPort).update("companion-route-1", payload);
    }

}
