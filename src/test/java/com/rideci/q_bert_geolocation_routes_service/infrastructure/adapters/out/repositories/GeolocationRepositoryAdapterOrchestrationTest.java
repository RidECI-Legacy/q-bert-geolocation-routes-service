package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.repositories;

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

import com.rideci.q_bert_geolocation_routes_service.domain.exception.RouteNotFoundException;
import com.rideci.q_bert_geolocation_routes_service.domain.model.Location;
import com.rideci.q_bert_geolocation_routes_service.domain.model.PickUpPoint;
import com.rideci.q_bert_geolocation_routes_service.domain.model.Route;
import com.rideci.q_bert_geolocation_routes_service.domain.model.RouteInfo;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.out.TomTomOutPort;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.entities.RouteDocument;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.mapper.RouteMapper;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class GeolocationRepositoryAdapterOrchestrationTest {

    @Mock
    private GeolocationRepository geolocationRepository;

    @Mock
    private TomTomOutPort tomTomOutPort;

    private final RouteMapper routeMapper = org.mapstruct.factory.Mappers.getMapper(RouteMapper.class);

    private GeolocationRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new GeolocationRepositoryAdapter(geolocationRepository, routeMapper, tomTomOutPort);
    }

    @Test
    void save_optimizesWaypointsCalculatesRouteAndPersists() {
        Location origin = Location.builder().latitude(4.60).longitude(-74.08).build();
        Location destination = Location.builder().latitude(4.65).longitude(-74.05).build();
        List<PickUpPoint> pickUpPoints = List.of(
                PickUpPoint.builder().passengerId("p1").location(origin).order(0).build());

        Route inputRoute = Route.builder()
                .tripId("trip-1")
                .origin(origin)
                .destination(destination)
                .pickUpPoints(pickUpPoints)
                .build();

        RouteInfo routeInfo = RouteInfo.builder()
                .totalDistance(1000d)
                .totalDuration(600d)
                .estimatedArrivalTime(LocalDateTime.now().plusMinutes(10))
                .polyline("encodedPolyline")
                .build();

        when(tomTomOutPort.optimizeWaypoints(pickUpPoints)).thenReturn(Mono.just(pickUpPoints));
        when(tomTomOutPort.calculateRoute(origin, destination, pickUpPoints)).thenReturn(Mono.just(routeInfo));
        when(geolocationRepository.save(any(RouteDocument.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(adapter.save(inputRoute))
                .assertNext(savedRoute -> {
                    assertThat(savedRoute.getId()).isNotBlank();
                    assertThat(savedRoute.getTripId()).isEqualTo("trip-1");
                    assertThat(savedRoute.getTotalDistance()).isEqualTo(1000d);
                    assertThat(savedRoute.getRemainingDistance()).isEqualTo(1000d);
                    assertThat(savedRoute.getPolyline()).isEqualTo("encodedPolyline");
                    assertThat(savedRoute.getPickUpPoints()).hasSize(1);
                })
                .verifyComplete();

        verify(tomTomOutPort).optimizeWaypoints(pickUpPoints);
        verify(tomTomOutPort).calculateRoute(origin, destination, pickUpPoints);
        verify(geolocationRepository).save(any(RouteDocument.class));
    }

    @Test
    void save_propagatesTomTomFailure() {
        Location origin = Location.builder().latitude(4.60).longitude(-74.08).build();
        List<PickUpPoint> pickUpPoints = List.of(
                PickUpPoint.builder().passengerId("p1").location(origin).order(0).build());
        Route inputRoute = Route.builder().tripId("trip-1").origin(origin).pickUpPoints(pickUpPoints).build();

        RuntimeException tomTomFailure = new RuntimeException("TomTom unavailable");
        when(tomTomOutPort.optimizeWaypoints(pickUpPoints)).thenReturn(Mono.error(tomTomFailure));

        StepVerifier.create(adapter.save(inputRoute))
                .expectErrorMatches(error -> error == tomTomFailure)
                .verify();
    }

    @Test
    void update_mergesRecalculatesAndPersists() {
        Location newOrigin = Location.builder().latitude(4.61).longitude(-74.09).build();
        Location newDestination = Location.builder().latitude(4.66).longitude(-74.06).build();
        List<PickUpPoint> newPickUpPoints = List.of(
                PickUpPoint.builder().passengerId("p2").location(newOrigin).order(0).build());

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
                .pickUpPoints(newPickUpPoints)
                .build();

        RouteInfo routeInfo = RouteInfo.builder()
                .totalDistance(2000d)
                .totalDuration(900d)
                .estimatedArrivalTime(LocalDateTime.now().plusMinutes(20))
                .polyline("newEncodedPolyline")
                .build();

        RouteDocument existingDocument = routeMapper.toDocument(existingRoute);

        when(geolocationRepository.findById("route-1")).thenReturn(Mono.just(existingDocument));
        when(tomTomOutPort.optimizeWaypoints(newPickUpPoints)).thenReturn(Mono.just(newPickUpPoints));
        when(tomTomOutPort.calculateRoute(newOrigin, newDestination, newPickUpPoints))
                .thenReturn(Mono.just(routeInfo));
        when(geolocationRepository.save(any(RouteDocument.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(adapter.update("route-1", updatePayload))
                .assertNext(updated -> {
                    assertThat(updated.getId()).isEqualTo("route-1");
                    assertThat(updated.getOrigin()).usingRecursiveComparison().isEqualTo(newOrigin);
                    assertThat(updated.getDestination()).usingRecursiveComparison().isEqualTo(newDestination);
                    assertThat(updated.getTotalDistance()).isEqualTo(2000d);
                    assertThat(updated.getRemainingDistance()).isEqualTo(2000d);
                    assertThat(updated.getPolyline()).isEqualTo("newEncodedPolyline");
                })
                .verifyComplete();
    }

    @Test
    void update_emitsRouteNotFoundWhenMissing() {
        when(geolocationRepository.findById("missing")).thenReturn(Mono.empty());

        StepVerifier.create(adapter.update("missing", Route.builder().build()))
                .expectErrorMatches(error -> error instanceof RouteNotFoundException
                        && ((RouteNotFoundException) error).getRouteId().equals("missing"))
                .verify();
    }

}
