package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.repositories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;

import com.rideci.q_bert_geolocation_routes_service.domain.exception.TravelTrackingNotFoundException;
import com.rideci.q_bert_geolocation_routes_service.domain.model.Location;
import com.rideci.q_bert_geolocation_routes_service.domain.model.TravelTracking;
import com.rideci.q_bert_geolocation_routes_service.domain.model.enums.ParticipantRole;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.out.TomTomOutPort;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.entities.LocationDocument;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.entities.RouteHistoryDocument;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.entities.TrackingConfigurationDocument;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.entities.TravelTrackingDocument;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.mapper.RouteMapper;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.mapper.TravelTrackingMapper;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class GeolocationRepositoryAdapterTravelTrackingTest {

    @Mock
    private GeolocationRepository geolocationRepository;

    @Mock
    private TomTomOutPort tomTomOutPort;

    @Mock
    private RouteHistoryRepository routeHistoryRepository;

    @Mock
    private ReactiveRedisTemplate<String, TravelTrackingDocument> travelTrackingRedisTemplate;

    @Mock
    private ReactiveRedisTemplate<String, TrackingConfigurationDocument> trackingConfigurationRedisTemplate;

    @Mock
    private ReactiveValueOperations<String, TravelTrackingDocument> travelTrackingValueOps;

    @Mock
    private ReactiveValueOperations<String, TrackingConfigurationDocument> trackingConfigurationValueOps;

    @Mock
    private LocationShareRepository locationShareRepository;

    private final RouteMapper routeMapper = Mappers.getMapper(RouteMapper.class);
    private final TravelTrackingMapper travelTrackingMapper = Mappers.getMapper(TravelTrackingMapper.class);

    private GeolocationRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new GeolocationRepositoryAdapter(geolocationRepository, routeMapper, tomTomOutPort,
                routeHistoryRepository, locationShareRepository, travelTrackingMapper, travelTrackingRedisTemplate,
                trackingConfigurationRedisTemplate);
    }

    private static TravelTrackingDocument sampleDocument(String tripId, String participantId) {
        return TravelTrackingDocument.builder()
                .tripId(tripId)
                .participantId(participantId)
                .speed(30d)
                .heading(90d)
                .currentLocation(LocationDocument.builder().latitude(4.6).longitude(-74.08).build())
                .participantRole(ParticipantRole.PASSENGER)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void getUserLocation_returnsMappedTracking_whenPresentInRedis() {
        TravelTrackingDocument document = sampleDocument("trip-1", "participant-1");

        when(travelTrackingRedisTemplate.opsForValue()).thenReturn(travelTrackingValueOps);
        when(travelTrackingValueOps.get("travel-tracking:trip-1:participant-1")).thenReturn(Mono.just(document));

        StepVerifier.create(adapter.getUserLocation("trip-1", "participant-1"))
                .assertNext(tracking -> {
                    assertThat(tracking.getTripId()).isEqualTo("trip-1");
                    assertThat(tracking.getParticipantId()).isEqualTo("participant-1");
                    assertThat(tracking.getSpeed()).isEqualTo(30d);
                })
                .verifyComplete();
    }

    @Test
    void getUserLocation_emitsTravelTrackingNotFound_whenAbsentInRedis() {
        when(travelTrackingRedisTemplate.opsForValue()).thenReturn(travelTrackingValueOps);
        when(travelTrackingValueOps.get("travel-tracking:trip-1:missing")).thenReturn(Mono.empty());

        StepVerifier.create(adapter.getUserLocation("trip-1", "missing"))
                .expectErrorMatches(error -> error instanceof TravelTrackingNotFoundException
                        && ((TravelTrackingNotFoundException) error).getTripId().equals("trip-1")
                        && ((TravelTrackingNotFoundException) error).getParticipantId().equals("missing"))
                .verify();
    }

    @Test
    void getUsersLocation_returnsEveryParticipantTrackingForTrip() {
        TravelTrackingDocument documentA = sampleDocument("trip-1", "participant-a");
        TravelTrackingDocument documentB = sampleDocument("trip-1", "participant-b");

        when(travelTrackingRedisTemplate.keys("travel-tracking:trip-1:*"))
                .thenReturn(Flux.just("travel-tracking:trip-1:participant-a", "travel-tracking:trip-1:participant-b"));
        when(travelTrackingRedisTemplate.opsForValue()).thenReturn(travelTrackingValueOps);
        when(travelTrackingValueOps.get("travel-tracking:trip-1:participant-a")).thenReturn(Mono.just(documentA));
        when(travelTrackingValueOps.get("travel-tracking:trip-1:participant-b")).thenReturn(Mono.just(documentB));

        StepVerifier.create(adapter.getUsersLocation("trip-1"))
                .recordWith(java.util.ArrayList::new)
                .thenConsumeWhile(tracking -> true)
                .consumeRecordedWith(trackings -> assertThat(trackings)
                        .extracting(TravelTracking::getParticipantId)
                        .containsExactlyInAnyOrder("participant-a", "participant-b"))
                .verifyComplete();
    }

    @Test
    void updateUserLocation_mergesStoredConfigurationAndPersistsToRedisAndHistory() {
        TravelTracking incoming = TravelTracking.builder()
                .participantId("participant-1")
                .speed(45d)
                .heading(120d)
                .currentLocation(Location.builder().latitude(4.6).longitude(-74.08).build())
                .participantRole(ParticipantRole.DRIVER)
                .build();

        TrackingConfigurationDocument storedConfig = TrackingConfigurationDocument.builder()
                .tripId("trip-1")
                .participantId("participant-1")
                .updateIntervalSeconds(5)
                .updatedAt(LocalDateTime.now())
                .build();

        when(trackingConfigurationRedisTemplate.opsForValue()).thenReturn(trackingConfigurationValueOps);
        when(trackingConfigurationValueOps.get("tracking-configuration:trip-1:participant-1"))
                .thenReturn(Mono.just(storedConfig));
        when(travelTrackingRedisTemplate.opsForValue()).thenReturn(travelTrackingValueOps);
        when(travelTrackingValueOps.set(eq("travel-tracking:trip-1:participant-1"), any(TravelTrackingDocument.class)))
                .thenReturn(Mono.just(true));
        when(routeHistoryRepository.save(any(RouteHistoryDocument.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(adapter.updateUserLocation("trip-1", incoming))
                .assertNext(tracking -> {
                    assertThat(tracking.getTripId()).isEqualTo("trip-1");
                    assertThat(tracking.getTrackingConfiguration().getUpdateIntervalSeconds()).isEqualTo(5);
                    assertThat(tracking.getUpdatedAt()).isNotNull();
                })
                .verifyComplete();

        ArgumentCaptor<RouteHistoryDocument> historyCaptor = ArgumentCaptor.forClass(RouteHistoryDocument.class);
        verify(routeHistoryRepository).save(historyCaptor.capture());
        RouteHistoryDocument savedHistory = historyCaptor.getValue();
        assertThat(savedHistory.getTripId()).isEqualTo("trip-1");
        assertThat(savedHistory.getParticipantId()).isEqualTo("participant-1");
        assertThat(savedHistory.getSpeed()).isEqualTo(45d);
        assertThat(savedHistory.getRecordedAt()).isNotNull();

        verify(travelTrackingValueOps).set(eq("travel-tracking:trip-1:participant-1"), any(TravelTrackingDocument.class));
    }

    @Test
    void updateUserLocation_leavesConfigurationUnset_whenNoneStoredInRedis() {
        TravelTracking incoming = TravelTracking.builder()
                .participantId("participant-1")
                .speed(10d)
                .heading(0d)
                .currentLocation(Location.builder().latitude(1).longitude(1).build())
                .participantRole(ParticipantRole.PASSENGER)
                .build();

        when(trackingConfigurationRedisTemplate.opsForValue()).thenReturn(trackingConfigurationValueOps);
        when(trackingConfigurationValueOps.get("tracking-configuration:trip-1:participant-1")).thenReturn(Mono.empty());
        when(travelTrackingRedisTemplate.opsForValue()).thenReturn(travelTrackingValueOps);
        when(travelTrackingValueOps.set(anyString(), any(TravelTrackingDocument.class))).thenReturn(Mono.just(true));
        when(routeHistoryRepository.save(any(RouteHistoryDocument.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(adapter.updateUserLocation("trip-1", incoming))
                .assertNext(tracking -> assertThat(tracking.getTrackingConfiguration()).isNull())
                .verifyComplete();
    }

    @Test
    void updateUsersLocation_updatesEachParticipant() {
        TravelTracking trackingA = TravelTracking.builder().participantId("participant-a")
                .currentLocation(Location.builder().latitude(1).longitude(1).build())
                .participantRole(ParticipantRole.PASSENGER).build();
        TravelTracking trackingB = TravelTracking.builder().participantId("participant-b")
                .currentLocation(Location.builder().latitude(2).longitude(2).build())
                .participantRole(ParticipantRole.DRIVER).build();

        when(trackingConfigurationRedisTemplate.opsForValue()).thenReturn(trackingConfigurationValueOps);
        when(trackingConfigurationValueOps.get(anyString())).thenReturn(Mono.empty());
        when(travelTrackingRedisTemplate.opsForValue()).thenReturn(travelTrackingValueOps);
        when(travelTrackingValueOps.set(anyString(), any(TravelTrackingDocument.class))).thenReturn(Mono.just(true));
        when(routeHistoryRepository.save(any(RouteHistoryDocument.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(adapter.updateUsersLocation("trip-1", List.of(trackingA, trackingB)))
                .recordWith(java.util.ArrayList::new)
                .thenConsumeWhile(tracking -> true)
                .consumeRecordedWith(trackings -> assertThat(trackings)
                        .extracting(TravelTracking::getParticipantId)
                        .containsExactlyInAnyOrder("participant-a", "participant-b"))
                .verifyComplete();

        verify(routeHistoryRepository, org.mockito.Mockito.times(2)).save(any(RouteHistoryDocument.class));
    }

    @Test
    void updateConfigurableInterval_persistsConfigurationToRedis() {
        when(trackingConfigurationRedisTemplate.opsForValue()).thenReturn(trackingConfigurationValueOps);
        when(trackingConfigurationValueOps.set(eq("tracking-configuration:trip-1:participant-1"),
                any(TrackingConfigurationDocument.class))).thenReturn(Mono.just(true));

        StepVerifier.create(adapter.updateConfigurableInterval("trip-1", "participant-1", 10))
                .assertNext(configuration -> {
                    assertThat(configuration.getTripId()).isEqualTo("trip-1");
                    assertThat(configuration.getParticipantId()).isEqualTo("participant-1");
                    assertThat(configuration.getUpdateIntervalSeconds()).isEqualTo(10);
                })
                .verifyComplete();

        ArgumentCaptor<TrackingConfigurationDocument> captor = ArgumentCaptor.forClass(TrackingConfigurationDocument.class);
        verify(trackingConfigurationValueOps).set(eq("tracking-configuration:trip-1:participant-1"), captor.capture());
        assertThat(captor.getValue().getUpdateIntervalSeconds()).isEqualTo(10);
    }

    private static RouteHistoryDocument historyPoint(String id, LocalDateTime recordedAt) {
        return RouteHistoryDocument.builder()
                .id(id)
                .tripId("trip-1")
                .participantId("participant-1")
                .speed(20d)
                .heading(0d)
                .location(LocationDocument.builder().latitude(1).longitude(1).build())
                .participantRole(ParticipantRole.PASSENGER)
                .recordedAt(recordedAt)
                .build();
    }

    @Test
    void getTravelReplay_emitsFirstPointImmediatelyThenScalesGapBySpeedMultiplier() {
        LocalDateTime t0 = LocalDateTime.of(2026, 1, 1, 10, 0, 0);
        RouteHistoryDocument first = historyPoint("h1", t0);
        RouteHistoryDocument second = historyPoint("h2", t0.plusSeconds(10));

        when(routeHistoryRepository.findByTripIdAndParticipantIdOrderByRecordedAtAsc("trip-1", "participant-1"))
                .thenReturn(Flux.just(first, second));

        StepVerifier.withVirtualTime(() -> adapter.getTravelReplay("trip-1", "participant-1", 2.0))
                .expectSubscription()
                .assertNext(point -> assertThat(point.getId()).isEqualTo("h1"))
                .expectNoEvent(Duration.ofSeconds(4))
                .thenAwait(Duration.ofSeconds(1))
                .assertNext(point -> assertThat(point.getId()).isEqualTo("h2"))
                .verifyComplete();
    }

    @Test
    void getTravelReplay_defaultsToNormalSpeed_whenMultiplierIsZeroOrNegative() {
        LocalDateTime t0 = LocalDateTime.of(2026, 1, 1, 10, 0, 0);
        RouteHistoryDocument first = historyPoint("h1", t0);
        RouteHistoryDocument second = historyPoint("h2", t0.plusSeconds(10));

        when(routeHistoryRepository.findByTripIdAndParticipantIdOrderByRecordedAtAsc("trip-1", "participant-1"))
                .thenReturn(Flux.just(first, second));

        StepVerifier.withVirtualTime(() -> adapter.getTravelReplay("trip-1", "participant-1", -5))
                .expectSubscription()
                .assertNext(point -> assertThat(point.getId()).isEqualTo("h1"))
                .expectNoEvent(Duration.ofSeconds(9))
                .thenAwait(Duration.ofSeconds(1))
                .assertNext(point -> assertThat(point.getId()).isEqualTo("h2"))
                .verifyComplete();
    }

    @Test
    void getTravelReplay_completesEmpty_whenNoHistoryRecorded() {
        when(routeHistoryRepository.findByTripIdAndParticipantIdOrderByRecordedAtAsc("trip-1", "participant-1"))
                .thenReturn(Flux.empty());

        StepVerifier.create(adapter.getTravelReplay("trip-1", "participant-1", 1.0))
                .verifyComplete();

        verify(travelTrackingRedisTemplate, never()).opsForValue();
    }

}
