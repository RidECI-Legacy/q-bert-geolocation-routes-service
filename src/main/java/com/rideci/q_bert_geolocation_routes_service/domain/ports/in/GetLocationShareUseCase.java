package com.rideci.q_bert_geolocation_routes_service.domain.ports.in;

import com.rideci.q_bert_geolocation_routes_service.domain.model.LocationShare;

import reactor.core.publisher.Mono;

public interface GetLocationShareUseCase {

    Mono<LocationShare> getLocationShare(String shareId);

}
