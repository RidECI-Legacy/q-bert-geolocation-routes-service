package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.mapper;

import org.mapstruct.Mapper;

import com.rideci.q_bert_geolocation_routes_service.domain.model.RouteHistory;
import com.rideci.q_bert_geolocation_routes_service.domain.model.TrackingConfiguration;
import com.rideci.q_bert_geolocation_routes_service.domain.model.TravelTracking;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.entities.RouteHistoryDocument;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.entities.TrackingConfigurationDocument;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.entities.TravelTrackingDocument;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Mapper(componentModel = "spring")
public interface TravelTrackingMapper {

    TravelTrackingDocument toDocument(TravelTracking travelTracking);

    TravelTracking toDomain(TravelTrackingDocument trackingDocument);

    RouteHistoryDocument toDocument(RouteHistory routeHistory);

    RouteHistory toDomain(RouteHistoryDocument routeHistoryDocument);

    TrackingConfigurationDocument toDocument(TrackingConfiguration trackingConfiguration);

    TrackingConfiguration toDomain(TrackingConfigurationDocument trackingConfigurationDocument);

    default Mono<TravelTrackingDocument> toDocument(Mono<TravelTracking> trackingMono) {
        return trackingMono.map(this::toDocument);
    }

    default Mono<TravelTracking> toDomain(Mono<TravelTrackingDocument> trackingDocMono) {
        return trackingDocMono.map(this::toDomain);
    }

    default Flux<RouteHistory> toDomainHistory(Flux<RouteHistoryDocument> routeHistoryDocumentFlux) {
        return routeHistoryDocumentFlux.map(this::toDomain);
    }

}
