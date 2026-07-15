package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.repositories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.rideci.q_bert_geolocation_routes_service.domain.exception.CompanionRouteNotFoundException;
import com.rideci.q_bert_geolocation_routes_service.domain.model.CompanionRoute;
import com.rideci.q_bert_geolocation_routes_service.domain.model.Location;
import com.rideci.q_bert_geolocation_routes_service.domain.model.TransitItinerary;
import com.rideci.q_bert_geolocation_routes_service.domain.model.TransitLeg;
import com.rideci.q_bert_geolocation_routes_service.domain.model.enums.LegMode;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.out.OtpOutPort;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.entities.CompanionRouteDocument;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.mapper.CompanionRouteMapper;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class CompanionRouteRepositoryAdapterOrchestrationTest {

    @Mock
    private CompanionRouteRepository companionRouteRepository;

    @Mock
    private OtpOutPort otpOutPort;

    private final CompanionRouteMapper companionRouteMapper = Mappers.getMapper(CompanionRouteMapper.class);

    private CompanionRouteRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new CompanionRouteRepositoryAdapter(companionRouteRepository, companionRouteMapper, otpOutPort);
    }

    private static TransitItinerary sampleItinerary() {
        TransitLeg leg = TransitLeg.builder()
                .id("leg-1")
                .sequence(1)
                .mode(LegMode.BUS)
                .routeShortName("T50")
                .gtfsTripId("gtfs-trip-1")
                .distance(1200d)
                .build();

        return TransitItinerary.builder()
                .legs(List.of(leg))
                .totalDistance(1500d)
                .totalTransfers(1)
                .estimatedArrivalTime(LocalDateTime.now().plusMinutes(25))
                .build();
    }

    @Test
    void save_plansTripAndPersists() {
        Location origin = Location.builder().latitude(4.60).longitude(-74.08).build();
        Location destination = Location.builder().latitude(4.65).longitude(-74.05).build();
        CompanionRoute inputRoute = CompanionRoute.builder().tripId("trip-1").origin(origin).destination(destination).build();
        TransitItinerary itinerary = sampleItinerary();

        when(otpOutPort.planTrip(origin, destination, null)).thenReturn(Mono.just(itinerary));
        when(companionRouteRepository.save(any(CompanionRouteDocument.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(adapter.save(inputRoute))
                .assertNext(savedRoute -> {
                    assertThat(savedRoute.getId()).isNotBlank();
                    assertThat(savedRoute.getTripId()).isEqualTo("trip-1");
                    assertThat(savedRoute.getTotalDistance()).isEqualTo(1500d);
                    assertThat(savedRoute.getTotalTransfers()).isEqualTo(1);
                    assertThat(savedRoute.getLegs()).hasSize(1);
                })
                .verifyComplete();
    }

    @Test
    void save_propagatesOtpFailure() {
        Location origin = Location.builder().latitude(4.60).longitude(-74.08).build();
        CompanionRoute inputRoute = CompanionRoute.builder().tripId("trip-1").origin(origin).build();
        RuntimeException otpFailure = new RuntimeException("OTP unavailable");

        when(otpOutPort.planTrip(origin, null, null)).thenReturn(Mono.error(otpFailure));

        StepVerifier.create(adapter.save(inputRoute))
                .expectErrorMatches(error -> error == otpFailure)
                .verify();
    }

    @Test
    void update_replansAndPersists() {
        Location newOrigin = Location.builder().latitude(4.61).longitude(-74.09).build();
        Location newDestination = Location.builder().latitude(4.66).longitude(-74.06).build();

        CompanionRoute existingRoute = CompanionRoute.builder()
                .id("companion-route-1")
                .tripId("trip-1")
                .origin(Location.builder().latitude(0).longitude(0).build())
                .destination(Location.builder().latitude(1).longitude(1).build())
                .totalDistance(500d)
                .createdAt(LocalDateTime.now().minusHours(1))
                .build();

        CompanionRoute updatePayload = CompanionRoute.builder().origin(newOrigin).destination(newDestination).build();
        TransitItinerary itinerary = sampleItinerary();
        CompanionRouteDocument existingDocument = companionRouteMapper.toDocument(existingRoute);

        when(companionRouteRepository.findById("companion-route-1")).thenReturn(Mono.just(existingDocument));
        when(otpOutPort.planTrip(newOrigin, newDestination, null)).thenReturn(Mono.just(itinerary));
        when(companionRouteRepository.save(any(CompanionRouteDocument.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(adapter.update("companion-route-1", updatePayload))
                .assertNext(updated -> {
                    assertThat(updated.getId()).isEqualTo("companion-route-1");
                    assertThat(updated.getOrigin()).usingRecursiveComparison().isEqualTo(newOrigin);
                    assertThat(updated.getDestination()).usingRecursiveComparison().isEqualTo(newDestination);
                    assertThat(updated.getTotalDistance()).isEqualTo(1500d);
                })
                .verifyComplete();
    }

    @Test
    void update_emitsCompanionRouteNotFoundWhenMissing() {
        when(companionRouteRepository.findById("missing")).thenReturn(Mono.empty());

        StepVerifier.create(adapter.update("missing", CompanionRoute.builder().build()))
                .expectErrorMatches(error -> error instanceof CompanionRouteNotFoundException
                        && ((CompanionRouteNotFoundException) error).getCompanionRouteId().equals("missing"))
                .verify();
    }

    @Test
    void findCompanionRouteById_emitsCompanionRouteNotFoundWhenMissing() {
        when(companionRouteRepository.findById("missing")).thenReturn(Mono.empty());

        StepVerifier.create(adapter.findCompanionRouteById("missing"))
                .expectErrorMatches(error -> error instanceof CompanionRouteNotFoundException
                        && ((CompanionRouteNotFoundException) error).getCompanionRouteId().equals("missing"))
                .verify();
    }

}
