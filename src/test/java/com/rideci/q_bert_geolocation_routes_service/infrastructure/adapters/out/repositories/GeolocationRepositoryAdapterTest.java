package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.rideci.q_bert_geolocation_routes_service.domain.model.Geofence;
import com.rideci.q_bert_geolocation_routes_service.domain.model.Location;
import com.rideci.q_bert_geolocation_routes_service.domain.model.PickUpPoint;
import com.rideci.q_bert_geolocation_routes_service.domain.model.Route;
import com.rideci.q_bert_geolocation_routes_service.domain.model.enums.GeofenceStatus;
import com.rideci.q_bert_geolocation_routes_service.domain.model.enums.PickUpStatus;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.mapper.RouteMapper;

import reactor.test.StepVerifier;

/**
 * Regression coverage for the bug where {@code PickUpPointDocument} had no
 * geofence field and every {@link Geofence} was silently dropped on save.
 * Requires Docker to run (Testcontainers spins up a real MongoDB instance).
 */
@Testcontainers
@DataMongoTest
class GeolocationRepositoryAdapterTest {

    @Container
    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7");

    @Autowired
    private GeolocationRepository geolocationRepository;

    private final RouteMapper routeMapper = org.mapstruct.factory.Mappers.getMapper(RouteMapper.class);

    @DynamicPropertySource
    static void mongoProperties(org.springframework.test.context.DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Test
    void saveThenFindById_roundTripsGeofenceConfig() {
        GeolocationRepositoryAdapter adapter = new GeolocationRepositoryAdapter(geolocationRepository, routeMapper);

        Geofence geofence = Geofence.builder()
                .radiusMeters(150)
                .geofenceStatus(GeofenceStatus.PENDING)
                .build();

        PickUpPoint pickUpPoint = PickUpPoint.builder()
                .PassengerId("passenger-1")
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
                .pickupPoints(List.of(pickUpPoint))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        StepVerifier.create(adapter.save(route)
                        .then(adapter.findRouteById("route-geofence-test")))
                .assertNext(found -> {
                    assertThat(found.getPickupPoints()).hasSize(1);
                    Geofence roundTripped = found.getPickupPoints().get(0).getGeofenceConfig();
                    assertThat(roundTripped).isNotNull();
                    assertThat(roundTripped.getRadiusMeters()).isEqualTo(150);
                    assertThat(roundTripped.getGeofenceStatus()).isEqualTo(GeofenceStatus.PENDING);
                })
                .verifyComplete();
    }

}
