package com.rideci.q_bert_geolocation_routes_service.application.useCases;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
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
class UpdateRouteUseCaseImplTest {

    @Mock
    private GeolocationRepositoryOutPort geolocationRepositoryOutPort;

    private UpdateRouteUseCaseImpl updateRouteUseCase;

    @BeforeEach
    void setUp() {
        updateRouteUseCase = new UpdateRouteUseCaseImpl(geolocationRepositoryOutPort);
    }

    @Test
    void updateRoute_delegatesToRepositoryOutPort() {
        Route updatePayload = Route.builder().tripId("trip-1").build();
        Route updatedRoute = Route.builder().id("route-1").tripId("trip-1").build();

        when(geolocationRepositoryOutPort.update("route-1", updatePayload)).thenReturn(Mono.just(updatedRoute));

        StepVerifier.create(updateRouteUseCase.updateRoute("route-1", updatePayload))
                .expectNext(updatedRoute)
                .verifyComplete();

        verify(geolocationRepositoryOutPort).update("route-1", updatePayload);
    }

    @Test
    void updateRoute_propagatesRouteNotFound() {
        Route updatePayload = Route.builder().build();
        RouteNotFoundException notFound = new RouteNotFoundException("missing");

        when(geolocationRepositoryOutPort.update("missing", updatePayload)).thenReturn(Mono.error(notFound));

        StepVerifier.create(updateRouteUseCase.updateRoute("missing", updatePayload))
                .expectErrorMatches(error -> error instanceof RouteNotFoundException
                        && ((RouteNotFoundException) error).getRouteId().equals("missing"))
                .verify();
    }

}
