package com.rideci.q_bert_geolocation_routes_service.domain.ports.in;

import com.rideci.q_bert_geolocation_routes_service.domain.model.RouteHistory;

import reactor.core.publisher.Flux;

public interface GetTravelReplayUseCase {

    Flux<RouteHistory> getTravelReplay(String tripId, String participantId, double speedMultiplier);

}
