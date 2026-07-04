package com.rideci.q_bert_geolocation_routes_service.application.useCases;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.rideci.q_bert_geolocation_routes_service.domain.model.Route;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.out.GeolocationRepositoryOutPort;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class GetAllUseCaseImplTest {

    @Mock
    private GeolocationRepositoryOutPort geolocationRepositoryOutPort;

    @Test
    void getAllRoutes_returnsEveryPersistedRoute() {
        GetAllUseCaseImpl getAllRoutesUseCase = new GetAllUseCaseImpl(geolocationRepositoryOutPort);
        Route routeA = Route.builder().id("a").build();
        Route routeB = Route.builder().id("b").build();
        when(geolocationRepositoryOutPort.findAllRoutes()).thenReturn(Flux.just(routeA, routeB));

        StepVerifier.create(getAllRoutesUseCase.getAllRoutes())
                .expectNext(routeA, routeB)
                .verifyComplete();
    }

    @Test
    void getAllRoutes_returnsEmptyFluxWhenNoRoutesExist() {
        GetAllUseCaseImpl getAllRoutesUseCase = new GetAllUseCaseImpl(geolocationRepositoryOutPort);
        when(geolocationRepositoryOutPort.findAllRoutes()).thenReturn(Flux.empty());

        StepVerifier.create(getAllRoutesUseCase.getAllRoutes())
                .verifyComplete();
    }

}
