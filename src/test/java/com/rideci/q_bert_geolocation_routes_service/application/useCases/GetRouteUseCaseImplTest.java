package com.rideci.q_bert_geolocation_routes_service.application.useCases;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.rideci.q_bert_geolocation_routes_service.domain.exception.RouteNotFoundException;
import com.rideci.q_bert_geolocation_routes_service.domain.model.Route;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.out.GeolocationRepositoryOutPort;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class GetRouteUseCaseImplTest {

    @Mock
    private GeolocationRepositoryOutPort geolocationRepositoryOutPort;

    @Test
    void getRoute_returnsRouteWhenFound() {
        GetRouteUseCaseImpl getRouteUseCase = new GetRouteUseCaseImpl(geolocationRepositoryOutPort);
        Route route = Route.builder().id("route-1").build();
        when(geolocationRepositoryOutPort.findRouteById("route-1")).thenReturn(Mono.just(route));

        StepVerifier.create(getRouteUseCase.getRoute("route-1"))
                .expectNext(route)
                .verifyComplete();
    }

    @Test
    void getRoute_emitsRouteNotFoundWhenMissing() {
        GetRouteUseCaseImpl getRouteUseCase = new GetRouteUseCaseImpl(geolocationRepositoryOutPort);
        when(geolocationRepositoryOutPort.findRouteById("missing")).thenReturn(Mono.empty());

        StepVerifier.create(getRouteUseCase.getRoute("missing"))
                .expectErrorMatches(error -> error instanceof RouteNotFoundException)
                .verify();
    }

}
