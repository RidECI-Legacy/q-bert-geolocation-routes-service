package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.repositories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.rideci.q_bert_geolocation_routes_service.domain.model.Geofence;
import com.rideci.q_bert_geolocation_routes_service.domain.model.Location;
import com.rideci.q_bert_geolocation_routes_service.domain.model.PickUpPoint;
import com.rideci.q_bert_geolocation_routes_service.domain.model.Route;
import com.rideci.q_bert_geolocation_routes_service.domain.model.RouteInfo;
import com.rideci.q_bert_geolocation_routes_service.domain.model.enums.GeofenceStatus;
import com.rideci.q_bert_geolocation_routes_service.domain.model.enums.PickUpStatus;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.out.TomTomOutPort;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.mapper.RouteMapper;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * Regression coverage for the bug where {@code PickUpPointDocument} had no
 * geofence field and every {@link Geofence} was silently dropped on save.
 * Requires Docker to run (Testcontainers spins up a real MongoDB instance).
 */
@Testcontainers
@DataMongoTest
@ExtendWith(MockitoExtension.class)
class GeolocationRepositoryAdapterTest {

    @Container
    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7");

    @Autowired
    private GeolocationRepository geolocationRepository;

    @Mock
    private TomTomOutPort tomTomOutPort;

    private final RouteMapper routeMapper = org.mapstruct.factory.Mappers.getMapper(RouteMapper.class);

    @org.springframework.test.context.DynamicPropertySource
    static void mongoProperties(org.springframework.test.context.DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Test
    void saveThenFindById_roundTripsGeofenceConfig() {
        GeolocationRepositoryAdapter adapter = new GeolocationRepositoryAdapter(geolocationRepository, routeMapper,
                tomTomOutPort);

        Geofence geofence = Geofence.builder()
                .radiusMeters(150)
                .geofenceStatus(GeofenceStatus.PENDING)
                .build();

        PickUpPoint pickUpPoint = PickUpPoint.builder()
                .passengerId("passenger-1")
                .location(Location.builder().latitude(4.6).longitude(-74.08).timestamp(LocalDateTime.now()).build())
                .geofenceConfig(geofence)
                .pickUpStatus(PickUpStatus.PENDING)
                .order(0)
                .build();

        Route route = Route.builder()
                .id("route-geofence-test")
                .tripId("trip-1")
                .origin(Location.builder().latitude(4.6).longitude(-74.08).build())
                .destination(Location.builder().latitude(4.65).longitude(-74.05).build())
                .pickUpPoints(List.of(pickUpPoint))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(tomTomOutPort.optimizeWaypoints(List.of(pickUpPoint))).thenReturn(Mono.just(List.of(pickUpPoint)));
        when(tomTomOutPort.calculateRoute(any(), any(), any())).thenReturn(Mono.just(RouteInfo.builder()
                .totalDistance(1000d)
                .totalDuration(600d)
                .estimatedArrivalTime(LocalDateTime.now().plusMinutes(10))
                .polyline("encodedPolyline")
                .build()));

        StepVerifier.create(adapter.save(route)
                        .flatMap(saved -> adapter.findRouteById(saved.getId())))
                .assertNext(found -> {
                    assertThat(found.getPickUpPoints()).hasSize(1);
                    Geofence roundTripped = found.getPickUpPoints().get(0).getGeofenceConfig();
                    assertThat(roundTripped).isNotNull();
                    assertThat(roundTripped.getRadiusMeters()).isEqualTo(150);
                    assertThat(roundTripped.getGeofenceStatus()).isEqualTo(GeofenceStatus.PENDING);
                })
                .verifyComplete();
    }

}
