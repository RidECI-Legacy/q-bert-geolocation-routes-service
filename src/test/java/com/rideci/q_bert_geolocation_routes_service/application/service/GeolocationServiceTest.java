package com.rideci.q_bert_geolocation_routes_service.application.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.rideci.q_bert_geolocation_routes_service.domain.model.Route;
import com.rideci.q_bert_geolocation_routes_service.domain.model.RouteHistory;
import com.rideci.q_bert_geolocation_routes_service.domain.model.TrackingConfiguration;
import com.rideci.q_bert_geolocation_routes_service.domain.model.TravelTracking;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.in.CreateRouteUseCase;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.in.GetAllRoutesUseCase;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.in.GetRouteUseCase;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.in.GetTravelReplayUseCase;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.in.GetUserLocationUseCase;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.in.GetUsersLocationUseCase;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.in.UpdateConfigurableIntervalsUseCase;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.in.UpdateRouteUseCase;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.in.UpdateUserLocationUseCase;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.in.UpdateUsersLocationUseCase;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class GeolocationServiceTest {

    @Mock
    private CreateRouteUseCase createRouteUseCase;
    @Mock
    private UpdateRouteUseCase updateRouteUseCase;
    @Mock
    private GetRouteUseCase getRouteUseCase;
    @Mock
    private GetAllRoutesUseCase getAllRoutesUseCase;
    @Mock
    private GetUserLocationUseCase getUserLocationUseCase;
    @Mock
    private GetUsersLocationUseCase getUsersLocationUseCase;
    @Mock
    private UpdateUserLocationUseCase updateUserLocationUseCase;
    @Mock
    private UpdateUsersLocationUseCase updateUsersLocationUseCase;
    @Mock
    private UpdateConfigurableIntervalsUseCase updateConfigurableIntervalsUseCase;
    @Mock
    private GetTravelReplayUseCase getTravelReplayUseCase;

    private GeolocationService geolocationService;

    @BeforeEach
    void setUp() {
        geolocationService = new GeolocationService(createRouteUseCase, updateRouteUseCase, getRouteUseCase,
                getAllRoutesUseCase, getUserLocationUseCase, getUsersLocationUseCase, updateUserLocationUseCase,
                updateUsersLocationUseCase, updateConfigurableIntervalsUseCase, getTravelReplayUseCase);
    }

    @Test
    void createRoute_delegatesToCreateRouteUseCase() {
        Route route = Route.builder().tripId("trip-1").build();
        Route saved = Route.builder().id("route-1").build();
        when(createRouteUseCase.createRoute(route)).thenReturn(Mono.just(saved));

        StepVerifier.create(geolocationService.createRoute(route)).expectNext(saved).verifyComplete();

        verify(createRouteUseCase).createRoute(route);
    }

    @Test
    void updateRoute_delegatesToUpdateRouteUseCase() {
        Route payload = Route.builder().build();
        Route updated = Route.builder().id("route-1").build();
        when(updateRouteUseCase.updateRoute("route-1", payload)).thenReturn(Mono.just(updated));

        StepVerifier.create(geolocationService.updateRoute("route-1", payload)).expectNext(updated).verifyComplete();

        verify(updateRouteUseCase).updateRoute("route-1", payload);
    }

    @Test
    void getRoute_delegatesToGetRouteUseCase() {
        Route route = Route.builder().id("route-1").build();
        when(getRouteUseCase.getRoute("route-1")).thenReturn(Mono.just(route));

        StepVerifier.create(geolocationService.getRoute("route-1")).expectNext(route).verifyComplete();

        verify(getRouteUseCase).getRoute("route-1");
    }

    @Test
    void getAllRoutes_delegatesToGetAllRoutesUseCase() {
        Route routeA = Route.builder().id("a").build();
        when(getAllRoutesUseCase.getAllRoutes()).thenReturn(Flux.just(routeA));

        StepVerifier.create(geolocationService.getAllRoutes()).expectNext(routeA).verifyComplete();

        verify(getAllRoutesUseCase).getAllRoutes();
    }

    @Test
    void getUserLocation_delegatesToGetUserLocationUseCase() {
        TravelTracking tracking = TravelTracking.builder().tripId("trip-1").participantId("p1").build();
        when(getUserLocationUseCase.getUserLocation("trip-1", "p1")).thenReturn(Mono.just(tracking));

        StepVerifier.create(geolocationService.getUserLocation("trip-1", "p1")).expectNext(tracking).verifyComplete();

        verify(getUserLocationUseCase).getUserLocation("trip-1", "p1");
    }

    @Test
    void getUsersLocation_delegatesToGetUsersLocationUseCase() {
        TravelTracking tracking = TravelTracking.builder().tripId("trip-1").participantId("p1").build();
        when(getUsersLocationUseCase.getUsersLocation("trip-1")).thenReturn(Flux.just(tracking));

        StepVerifier.create(geolocationService.getUsersLocation("trip-1")).expectNext(tracking).verifyComplete();

        verify(getUsersLocationUseCase).getUsersLocation("trip-1");
    }

    @Test
    void updateUserLocation_delegatesToUpdateUserLocationUseCase() {
        TravelTracking incoming = TravelTracking.builder().participantId("p1").build();
        TravelTracking updated = TravelTracking.builder().tripId("trip-1").participantId("p1").build();
        when(updateUserLocationUseCase.updateUserLocation("trip-1", incoming)).thenReturn(Mono.just(updated));

        StepVerifier.create(geolocationService.updateUserLocation("trip-1", incoming))
                .expectNext(updated)
                .verifyComplete();

        verify(updateUserLocationUseCase).updateUserLocation("trip-1", incoming);
    }

    @Test
    void updateUsersLocation_delegatesToUpdateUsersLocationUseCase() {
        List<TravelTracking> incoming = List.of(TravelTracking.builder().participantId("p1").build());
        TravelTracking updated = TravelTracking.builder().tripId("trip-1").participantId("p1").build();
        when(updateUsersLocationUseCase.updateUsersLocation("trip-1", incoming)).thenReturn(Flux.just(updated));

        StepVerifier.create(geolocationService.updateUsersLocation("trip-1", incoming))
                .expectNext(updated)
                .verifyComplete();

        verify(updateUsersLocationUseCase).updateUsersLocation("trip-1", incoming);
    }

    @Test
    void updateConfigurableInterval_delegatesToUpdateConfigurableIntervalsUseCase() {
        TrackingConfiguration configuration = TrackingConfiguration.builder()
                .tripId("trip-1").participantId("p1").updateIntervalSeconds(5).build();
        when(updateConfigurableIntervalsUseCase.updateConfigurableInterval("trip-1", "p1", 5))
                .thenReturn(Mono.just(configuration));

        StepVerifier.create(geolocationService.updateConfigurableInterval("trip-1", "p1", 5))
                .expectNext(configuration)
                .verifyComplete();

        verify(updateConfigurableIntervalsUseCase).updateConfigurableInterval("trip-1", "p1", 5);
    }

    @Test
    void getTravelReplay_delegatesToGetTravelReplayUseCase() {
        RouteHistory point = RouteHistory.builder().tripId("trip-1").participantId("p1").build();
        when(getTravelReplayUseCase.getTravelReplay("trip-1", "p1", 2.0)).thenReturn(Flux.just(point));

        StepVerifier.create(geolocationService.getTravelReplay("trip-1", "p1", 2.0))
                .expectNext(point)
                .verifyComplete();

        verify(getTravelReplayUseCase).getTravelReplay("trip-1", "p1", 2.0);
    }

}
