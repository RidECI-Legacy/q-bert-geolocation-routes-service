package com.rideci.q_bert_geolocation_routes_service.application.use_cases;

import com.rideci.q_bert_geolocation_routes_service.application.annotation.UseCase;
import com.rideci.q_bert_geolocation_routes_service.domain.model.LocationShare;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.in.GetLocationShareUseCase;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.out.GeolocationRepositoryOutPort;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@UseCase
@RequiredArgsConstructor
public class GetLocationShareUseCaseImpl implements GetLocationShareUseCase {

    private final GeolocationRepositoryOutPort geolocationRepositoryOutPort;

    @Override
    public Mono<LocationShare> getLocationShare(String shareId) {
        return geolocationRepositoryOutPort.getLocationShare(shareId);
    }

}
