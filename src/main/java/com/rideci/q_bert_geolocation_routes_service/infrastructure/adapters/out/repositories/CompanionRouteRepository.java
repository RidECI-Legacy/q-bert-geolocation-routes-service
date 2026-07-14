package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.entities.CompanionRouteDocument;

import reactor.core.publisher.Mono;

public interface CompanionRouteRepository extends ReactiveMongoRepository<CompanionRouteDocument, String> {

    Mono<CompanionRouteDocument> findById(String id);

}
