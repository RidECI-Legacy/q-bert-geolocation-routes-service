package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.entities.RouteHistoryDocument;

import reactor.core.publisher.Flux;

public interface RouteHistoryRepository extends ReactiveMongoRepository<RouteHistoryDocument, String> {

    Flux<RouteHistoryDocument> findByTripIdAndParticipantIdOrderByRecordedAtAsc(String tripId, String participantId);

}
