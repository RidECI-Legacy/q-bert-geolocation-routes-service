package com.rideci.q_bert_geolocation_routes_service.application.useCases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.rideci.q_bert_geolocation_routes_service.domain.model.Location;
import com.rideci.q_bert_geolocation_routes_service.domain.model.PickUpPoint;
import com.rideci.q_bert_geolocation_routes_service.domain.model.Route;
import com.rideci.q_bert_geolocation_routes_service.domain.model.RouteInfo;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.out.GeolocationRepositoryOutPort;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.out.TomTomOutPort;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class CreateRouteUseCaseImplTest {

    @Mock
    private GeolocationRepositoryOutPort geolocationRepositoryOutPort;

    @Mock
    private TomTomOutPort tomTomOutPort;

    private CreateRouteUseCaseImpl createRouteUseCase;

    private Route inputRoute;
    private List<PickUpPoint> pickupPoints;
    private RouteInfo routeInfo;

    @BeforeEach
    void setUp() {
        createRouteUseCase = new CreateRouteUseCaseImpl(geolocationRepositoryOutPort, tomTomOutPort);

        Location origin = Location.builder().latitude(4.60).longitude(-74.08).build();
        Location destination = Location.builder().latitude(4.65).longitude(-74.05).build();

        pickupPoints = List.of(
                PickUpPoint.builder().PassengerId("p1").location(origin).order(0).build());

        inputRoute = Route.builder()
                .tripId("trip-1")
                .origin(origin)
                .destination(destination)
                .pickupPoints(pickupPoints)
                .build();

        routeInfo = RouteInfo.builder()
                .totalDistance(1000d)
                .totalDuration(600d)
                .estimatedArrivalTime(LocalDateTime.now().plusMinutes(10))
                .polyline("encodedPolyline")
                .build();
    }

    @Test
    void createRoute_callsTomTomThenPersistsCalculatedRoute() {
        when(tomTomOutPort.optimizeWaypoints(pickupPoints)).thenReturn(Mono.just(pickupPoints));
        when(tomTomOutPort.calculateRoute(any(), any(), any())).thenReturn(Mono.just(routeInfo));
        when(geolocationRepositoryOutPort.save(any(Route.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(createRouteUseCase.createRoute(inputRoute))
                .assertNext(savedRoute -> {
                    assertThat(savedRoute.getId()).isNotBlank();
                    assertThat(savedRoute.getTripId()).isEqualTo("trip-1");
                    assertThat(savedRoute.getTotalDistance()).isEqualTo(1000d);
                    assertThat(savedRoute.getRemainingDistance()).isEqualTo(1000d);
                    assertThat(savedRoute.getPolyline()).isEqualTo("encodedPolyline");
                    assertThat(savedRoute.getPickupPoints()).isEqualTo(pickupPoints);
                })
                .verifyComplete();

        verify(tomTomOutPort).optimizeWaypoints(pickupPoints);
        verify(tomTomOutPort).calculateRoute(inputRoute.getOrigin(), inputRoute.getDestination(), pickupPoints);
        verify(geolocationRepositoryOutPort).save(any(Route.class));
    }

    @Test
    void createRoute_propagatesTomTomFailure() {
        RuntimeException tomTomFailure = new RuntimeException("TomTom unavailable");
        when(tomTomOutPort.optimizeWaypoints(pickupPoints)).thenReturn(Mono.error(tomTomFailure));

        StepVerifier.create(createRouteUseCase.createRoute(inputRoute))
                .expectErrorMatches(error -> error == tomTomFailure)
                .verify();
    }

}
