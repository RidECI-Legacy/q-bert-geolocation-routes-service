package com.rideci.q_bert_geolocation_routes_service.application.useCases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.rideci.q_bert_geolocation_routes_service.domain.exception.RouteNotFoundException;
import com.rideci.q_bert_geolocation_routes_service.domain.model.Location;
import com.rideci.q_bert_geolocation_routes_service.domain.model.PickUpPoint;
import com.rideci.q_bert_geolocation_routes_service.domain.model.Route;
import com.rideci.q_bert_geolocation_routes_service.domain.model.RouteInfo;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.out.GeolocationRepositoryOutPort;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.out.TomTomOutPort;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class UpdateRouteUseCaseImplTest {

    @Mock
    private GeolocationRepositoryOutPort geolocationRepositoryOutPort;

    @Mock
    private TomTomOutPort tomTomOutPort;

    private UpdateRouteUseCaseImpl updateRouteUseCase;

    @BeforeEach
    void setUp() {
        updateRouteUseCase = new UpdateRouteUseCaseImpl(geolocationRepositoryOutPort, tomTomOutPort);
    }

    @Test
    void updateRoute_mergesRecalculatesAndPersists() {
        Location newOrigin = Location.builder().latitude(4.61).longitude(-74.09).build();
        Location newDestination = Location.builder().latitude(4.66).longitude(-74.06).build();
        List<PickUpPoint> newPickupPoints = List.of(
                PickUpPoint.builder().PassengerId("p2").location(newOrigin).order(0).build());

        Route existingRoute = Route.builder()
                .id("route-1")
                .tripId("trip-1")
                .origin(Location.builder().latitude(0).longitude(0).build())
                .destination(Location.builder().latitude(1).longitude(1).build())
                .totalDistance(500d)
                .createdAt(LocalDateTime.now().minusHours(1))
                .build();

        Route updatePayload = Route.builder()
                .origin(newOrigin)
                .destination(newDestination)
                .pickupPoints(newPickupPoints)
                .build();

        RouteInfo routeInfo = RouteInfo.builder()
                .totalDistance(2000d)
                .totalDuration(900d)
                .estimatedArrivalTime(LocalDateTime.now().plusMinutes(20))
                .polyline("newEncodedPolyline")
                .build();

        when(geolocationRepositoryOutPort.findRouteById("route-1")).thenReturn(Mono.just(existingRoute));
        when(tomTomOutPort.optimizeWaypoints(newPickupPoints)).thenReturn(Mono.just(newPickupPoints));
        when(tomTomOutPort.calculateRoute(newOrigin, newDestination, newPickupPoints)).thenReturn(Mono.just(routeInfo));
        when(geolocationRepositoryOutPort.save(any(Route.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(updateRouteUseCase.updateRoute("route-1", updatePayload))
                .assertNext(updated -> {
                    assertThat(updated.getId()).isEqualTo("route-1");
                    assertThat(updated.getOrigin()).isEqualTo(newOrigin);
                    assertThat(updated.getDestination()).isEqualTo(newDestination);
                    assertThat(updated.getTotalDistance()).isEqualTo(2000d);
                    assertThat(updated.getRemainingDistance()).isEqualTo(2000d);
                    assertThat(updated.getPolyline()).isEqualTo("newEncodedPolyline");
                })
                .verifyComplete();
    }

    @Test
    void updateRoute_emitsRouteNotFoundWhenMissing() {
        when(geolocationRepositoryOutPort.findRouteById("missing")).thenReturn(Mono.empty());

        StepVerifier.create(updateRouteUseCase.updateRoute("missing", Route.builder().build()))
                .expectErrorMatches(error -> error instanceof RouteNotFoundException
                        && ((RouteNotFoundException) error).getRouteId().equals("missing"))
                .verify();
    }

}
