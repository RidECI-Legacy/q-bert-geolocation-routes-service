package com.rideci.q_bert_geolocation_routes_service.domain.ports.in;

import com.rideci.q_bert_geolocation_routes_service.domain.model.TrackingConfiguration;

import reactor.core.publisher.Mono;

public interface UpdateConfigurableIntervalsUseCase {

    Mono<TrackingConfiguration> updateConfigurableInterval(String tripId, String participantId, int newUpdateIntervalSeconds);

}
