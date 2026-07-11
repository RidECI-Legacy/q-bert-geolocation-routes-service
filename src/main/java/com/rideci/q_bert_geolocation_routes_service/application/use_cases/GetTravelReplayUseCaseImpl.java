package com.rideci.q_bert_geolocation_routes_service.application.use_cases;

import com.rideci.q_bert_geolocation_routes_service.application.annotation.UseCase;
import com.rideci.q_bert_geolocation_routes_service.domain.model.RouteHistory;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.in.GetTravelReplayUseCase;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.out.GeolocationRepositoryOutPort;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@UseCase
@RequiredArgsConstructor
public class GetTravelReplayUseCaseImpl implements GetTravelReplayUseCase {

    private final GeolocationRepositoryOutPort geolocationRepositoryOutPort;

    @Override
    public Flux<RouteHistory> getTravelReplay(String tripId, String participantId, double speedMultiplier) {
        return geolocationRepositoryOutPort.getTravelReplay(tripId, participantId, speedMultiplier);
    }

}
