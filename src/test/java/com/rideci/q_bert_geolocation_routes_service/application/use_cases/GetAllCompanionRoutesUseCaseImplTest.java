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

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class GetAllCompanionRoutesUseCaseImplTest {

    @Mock
    private CompanionRouteRepositoryOutPort companionRouteRepositoryOutPort;

    private GetAllCompanionRoutesUseCaseImpl getAllCompanionRoutesUseCase;

    @BeforeEach
    void setUp() {
        getAllCompanionRoutesUseCase = new GetAllCompanionRoutesUseCaseImpl(companionRouteRepositoryOutPort);
    }

    @Test
    void getAllCompanionRoutes_delegatesToRepositoryOutPort() {
        CompanionRoute routeA = CompanionRoute.builder().id("a").build();

        when(companionRouteRepositoryOutPort.findAllCompanionRoutes()).thenReturn(Flux.just(routeA));

        StepVerifier.create(getAllCompanionRoutesUseCase.getAllCompanionRoutes())
                .expectNext(routeA)
                .verifyComplete();

        verify(companionRouteRepositoryOutPort).findAllCompanionRoutes();
    }

}
