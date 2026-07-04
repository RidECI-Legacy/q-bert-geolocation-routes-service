package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.repositories;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;

import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.rideci.q_bert_geolocation_routes_service.domain.exception.TomTomIntegrationException;
import com.rideci.q_bert_geolocation_routes_service.domain.model.Location;

import reactor.test.StepVerifier;

/**
 * Exercises the Resilience4j Spring AOP proxy (retry -> circuit breaker -> fallback)
 * that plain unit tests calling {@link TomTomAdapter} directly cannot reach.
 */
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class TomTomAdapterResilienceIntegrationTest {

    private static WireMockServer wireMockServer;

    @Autowired
    private TomTomAdapter tomTomAdapter;

    @BeforeEach
    void resetStubs() {
        wireMockServer.resetAll();
    }

    @DynamicPropertySource
    static void tomTomProperties(DynamicPropertyRegistry registry) {
        wireMockServer = new WireMockServer(WireMockConfiguration.options().dynamicPort());
        wireMockServer.start();
        registry.add("tomtom.base-url", wireMockServer::baseUrl);
        registry.add("tomtom.api-key", () -> "test-key");
    }

    @AfterAll
    static void tearDown() {
        wireMockServer.stop();
    }

    @Test
    void calculateRoute_exhaustsRetriesThenFallsBackToTomTomIntegrationException() {
        wireMockServer.stubFor(get(urlPathEqualTo("/routing/1/calculateRoute/4.6,-74.08:4.65,-74.05/json"))
                .willReturn(aResponse().withStatus(500)));

        Location origin = Location.builder().latitude(4.6).longitude(-74.08).build();
        Location destination = Location.builder().latitude(4.65).longitude(-74.05).build();

        StepVerifier.create(tomTomAdapter.calculateRoute(origin, destination, List.of()))
                .expectErrorMatches(error -> error instanceof TomTomIntegrationException)
                .verify(Duration.ofSeconds(10));

        wireMockServer.verify(3, com.github.tomakehurst.wiremock.client.WireMock
                .getRequestedFor(urlPathEqualTo("/routing/1/calculateRoute/4.6,-74.08:4.65,-74.05/json")));
    }

}
