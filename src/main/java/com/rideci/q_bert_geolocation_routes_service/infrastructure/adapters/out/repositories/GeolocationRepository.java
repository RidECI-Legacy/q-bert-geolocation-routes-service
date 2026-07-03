package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.entities.RouteDocument;

public interface GeolocationRepository extends MongoRepository<RouteDocument, String> {

    List<RouteDocument> findAll();

    RouteDocument findByRouteId(String routeId);

}
