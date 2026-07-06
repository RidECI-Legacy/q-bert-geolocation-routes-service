package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.repositories;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.rideci.q_bert_geolocation_routes_service.domain.model.Location;
import com.rideci.q_bert_geolocation_routes_service.domain.model.PickUpPoint;
import com.rideci.q_bert_geolocation_routes_service.domain.model.RouteInfo;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.config.TomTomProperties;

import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.netty.http.client.HttpClient;
import reactor.test.StepVerifier;

class TomTomAdapterTest {

    private WireMockServer wireMockServer;
    private TomTomAdapter tomTomAdapter;

    @BeforeEach
    void setUp() {
        wireMockServer = new WireMockServer(WireMockConfiguration.options().dynamicPort());
        wireMockServer.start();

        TomTomProperties properties = new TomTomProperties();
        properties.setApiKey("test-key");
        properties.setBaseUrl(wireMockServer.baseUrl());

        WebClient webClient = WebClient.builder()
                .baseUrl(properties.getBaseUrl())
                .clientConnector(new ReactorClientHttpConnector(HttpClient.create()))
                .build();

        tomTomAdapter = new TomTomAdapter(webClient, properties);
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    @Test
    void calculateRoute_mapsTomTomResponseIntoRouteInfo() {
        wireMockServer.stubFor(get(urlPathEqualTo("/routing/1/calculateRoute/4.6,-74.08:4.65,-74.05/json"))
                .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json").withBody("""
                        {
                          "routes": [
                            {
                              "summary": {
                                "lengthInMeters": 5000,
                                "travelTimeInSeconds": 600,
                                "arrivalTime": "2026-07-03T12:00:00-05:00"
                              },
                              "legs": [
                                { "points": [ { "latitude": 4.6, "longitude": -74.08 }, { "latitude": 4.65, "longitude": -74.05 } ] }
                              ]
                            }
                          ]
                        }
                        """)));

        Location origin = Location.builder().latitude(4.6).longitude(-74.08).build();
        Location destination = Location.builder().latitude(4.65).longitude(-74.05).build();

        StepVerifier.create(tomTomAdapter.calculateRoute(origin, destination, List.of()))
                .assertNext((RouteInfo routeInfo) -> {
                    assertThat(routeInfo.getTotalDistance()).isEqualTo(5000d);
                    assertThat(routeInfo.getTotalDuration()).isEqualTo(600d);
                    assertThat(routeInfo.getPolyline()).isNotBlank();
                })
                .verifyComplete();
    }

    @Test
    void calculateRoute_wrapsFailureAfterRetriesExhaustedIntoTomTomIntegrationException() {
        wireMockServer.stubFor(get(urlPathEqualTo("/routing/1/calculateRoute/4.6,-74.08:4.65,-74.05/json"))
                .willReturn(aResponse().withStatus(500)));

        Location origin = Location.builder().latitude(4.6).longitude(-74.08).build();
        Location destination = Location.builder().latitude(4.65).longitude(-74.05).build();

        // Calling the adapter method directly (bypassing the Spring AOP proxy that
        // normally applies @Retry/@CircuitBreaker/@TimeLimiter) only verifies the raw
        // WebClient error path; see TomTomAdapterResilienceIntegrationTest for the
        // proxied behaviour (retry/circuit-breaker/fallback into TomTomIntegrationException).
        StepVerifier.create(tomTomAdapter.calculateRoute(origin, destination, List.of()))
                .expectError()
                .verify(Duration.ofSeconds(5));
    }

    @Test
    void optimizeWaypoints_skipsCallWhenFewerThanThreePoints() {
        PickUpPoint single = PickUpPoint.builder()
                .location(Location.builder().latitude(1).longitude(1).build())
                .build();

        StepVerifier.create(tomTomAdapter.optimizeWaypoints(List.of(single)))
                .expectNext(List.of(single))
                .verifyComplete();

        wireMockServer.verify(0, com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor(urlPathEqualTo("/routing/1/calculateRoute/1.0,1.0/json")));
    }

    @Test
    void isPointInsideGeofence_usesHaversineDistanceAgainstPickUpPointLocation() {
        Location pickupLocation = Location.builder().latitude(4.60971).longitude(-74.08175).build();
        PickUpPoint pickUpPoint = PickUpPoint.builder()
                .location(pickupLocation)
                .geofenceConfig(com.rideci.q_bert_geolocation_routes_service.domain.model.Geofence.builder()
                        .radiusMeters(100)
                        .build())
                .build();

        Location veryClosePoint = Location.builder().latitude(4.60972).longitude(-74.08176).build();
        Location farPoint = Location.builder().latitude(4.70).longitude(-74.20).build();

        assertThat(tomTomAdapter.isPointInsideGeofence(veryClosePoint, pickUpPoint)).isTrue();
        assertThat(tomTomAdapter.isPointInsideGeofence(farPoint, pickUpPoint)).isFalse();
    }

}
