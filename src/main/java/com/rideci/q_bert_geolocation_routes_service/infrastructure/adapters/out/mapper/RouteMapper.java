package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.rideci.q_bert_geolocation_routes_service.domain.model.Route;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.entities.RouteDocument;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Mapper(componentModel = "spring")
public interface RouteMapper {

    RouteDocument toDocument(Route route);

    Route toDomain(RouteDocument routeDocument);

    List<Route> listToDomain(List<RouteDocument> routesDocuments);

    default Mono<RouteDocument> toDocument(Mono<Route> routeMono) {
        return routeMono.map(this::toDocument);
    }

    default Mono<Route> toDomain(Mono<RouteDocument> routeMonoDocument) {
        return routeMonoDocument.map(this::toDomain);
    }

    default Flux<Route> listToDomain(Flux<RouteDocument> routesDocumentsFlux) {
        return routesDocumentsFlux.map(this:: toDomain);
    }

}
