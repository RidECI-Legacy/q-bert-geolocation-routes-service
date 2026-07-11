package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.entities.LocationShareDocument;

public interface LocationShareRepository extends ReactiveMongoRepository<LocationShareDocument, String> {

}
