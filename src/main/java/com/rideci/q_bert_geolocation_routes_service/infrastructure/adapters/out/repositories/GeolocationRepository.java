package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.entities.RouteDocument;

import reactor.core.publisher.Mono;

public interface GeolocationRepository extends ReactiveMongoRepository<RouteDocument, String> {

    Mono<RouteDocument> findById(String id);

}
