package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.scheduler;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.rideci.q_bert_geolocation_routes_service.application.service.CompanionRouteService;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.websocket.BusRealtimeBroadcaster;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.config.GtfsRealtimeProperties;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

// Reactive poller (not @Scheduled) to stay consistent with this service being reactive
// end-to-end. Every tick is isolated with onErrorResume so a single failed poll (expected
// while GtfsRealtimeAdapter is stubbed) doesn't terminate the whole Flux.interval sequence.
@Slf4j
@Component
@RequiredArgsConstructor
public class GtfsRealtimePoller {

    private final CompanionRouteService companionRouteService;
    private final BusRealtimeBroadcaster broadcaster;
    private final GtfsRealtimeProperties gtfsRealtimeProperties;

    private Disposable subscription;

    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        subscription = Flux.interval(gtfsRealtimeProperties.getPollInterval())
                .concatMap(tick -> poll())
                .subscribe();
    }

    Mono<Void> poll() {
        return Mono.defer(companionRouteService::fetchVehiclePositions)
                .doOnNext(statuses -> statuses.forEach(broadcaster::publish))
                .onErrorResume(error -> {
                    log.warn("GTFS-Realtime poll failed, will retry next tick", error);
                    return Mono.empty();
                })
                .then();
    }

    @PreDestroy
    public void stop() {
        if (subscription != null) {
            subscription.dispose();
        }
    }

}
