package com.rideci.q_bert_geolocation_routes_service.application.use_cases;

import com.rideci.q_bert_geolocation_routes_service.application.annotation.UseCase;
import com.rideci.q_bert_geolocation_routes_service.domain.model.TrackingConfiguration;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.in.UpdateConfigurableIntervalsUseCase;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.out.GeolocationRepositoryOutPort;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@UseCase
@RequiredArgsConstructor
public class UpdateConfigurableIntervalsUseCaseImpl implements UpdateConfigurableIntervalsUseCase {

    private final GeolocationRepositoryOutPort geolocationRepositoryOutPort;

    @Override
    public Mono<TrackingConfiguration> updateConfigurableInterval(String tripId, String participantId, int newUpdateIntervalSeconds) {
        return geolocationRepositoryOutPort.updateConfigurableInterval(tripId, participantId, newUpdateIntervalSeconds);
    }
    
}
