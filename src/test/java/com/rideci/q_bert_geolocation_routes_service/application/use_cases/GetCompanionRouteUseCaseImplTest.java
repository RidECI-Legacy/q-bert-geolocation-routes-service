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
class GetCompanionRouteUseCaseImplTest {

    @Mock
    private CompanionRouteRepositoryOutPort companionRouteRepositoryOutPort;

    private GetCompanionRouteUseCaseImpl getCompanionRouteUseCase;

    @BeforeEach
    void setUp() {
        getCompanionRouteUseCase = new GetCompanionRouteUseCaseImpl(companionRouteRepositoryOutPort);
    }

    @Test
    void getCompanionRoute_delegatesToRepositoryOutPort() {
        CompanionRoute companionRoute = CompanionRoute.builder().id("companion-route-1").build();

        when(companionRouteRepositoryOutPort.findCompanionRouteById("companion-route-1"))
                .thenReturn(Mono.just(companionRoute));

        StepVerifier.create(getCompanionRouteUseCase.getCompanionRoute("companion-route-1"))
                .expectNext(companionRoute)
                .verifyComplete();

        verify(companionRouteRepositoryOutPort).findCompanionRouteById("companion-route-1");
    }

}
