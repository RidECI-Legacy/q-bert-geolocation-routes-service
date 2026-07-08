package com.rideci.q_bert_geolocation_routes_service.application.use_cases;

import java.util.List;

import com.rideci.q_bert_geolocation_routes_service.application.annotation.UseCase;
import com.rideci.q_bert_geolocation_routes_service.domain.model.TravelTracking;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.in.GetUsersLocationUseCase;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.out.GeolocationRepositoryOutPort;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@UseCase
@RequiredArgsConstructor
public class GetUsersLocationUseCaseImpl implements GetUsersLocationUseCase {

    private final GeolocationRepositoryOutPort geolocationRepositoryOutPort;

    @Override
    public Flux<TravelTracking> getUsersLocation(String tripId, List<TravelTracking> usersTracking) {
        return geolocationRepositoryOutPort.getUsersLocation(tripId, usersTracking);
    }

}
