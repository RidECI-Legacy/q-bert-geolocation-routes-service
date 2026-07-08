package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.mapper;

import org.mapstruct.Mapper;

import com.rideci.q_bert_geolocation_routes_service.domain.model.TravelTracking;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.entities.TravelTrackingDocument;

import reactor.core.publisher.Mono;

@Mapper(componentModel = "spring")
public interface TravelTrackingMapper {

    TravelTrackingDocument toDocument(TravelTracking travelTracking);

    TravelTracking toDomain(TravelTrackingDocument trackingDocument);

    default Mono<TravelTrackingDocument> toDocument(Mono<TravelTracking> trackingMono) {
        return trackingMono.map(this::toDocument);
    }

    default Mono<TravelTracking> toDomain(Mono<TravelTrackingDocument> trackingDocMono) {
        return trackingDocMono.map(this::toDomain);
    }

}
