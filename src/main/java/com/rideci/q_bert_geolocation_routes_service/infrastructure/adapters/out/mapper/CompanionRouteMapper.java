package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.rideci.q_bert_geolocation_routes_service.domain.model.CompanionRoute;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.entities.CompanionRouteDocument;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Mapper(componentModel = "spring")
public interface CompanionRouteMapper {

    CompanionRouteDocument toDocument(CompanionRoute companionRoute);

    CompanionRoute toDomain(CompanionRouteDocument companionRouteDocument);

    List<CompanionRoute> listToDomain(List<CompanionRouteDocument> companionRouteDocuments);

    default Mono<CompanionRouteDocument> toDocument(Mono<CompanionRoute> companionRouteMono) {
        return companionRouteMono.map(this::toDocument);
    }

    default Mono<CompanionRoute> toDomain(Mono<CompanionRouteDocument> companionRouteDocumentMono) {
        return companionRouteDocumentMono.map(this::toDomain);
    }

    default Flux<CompanionRoute> listToDomain(Flux<CompanionRouteDocument> companionRouteDocumentsFlux) {
        return companionRouteDocumentsFlux.map(this::toDomain);
    }

}
