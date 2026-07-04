package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.mapper;

import org.mapstruct.Mapper;

import com.rideci.q_bert_geolocation_routes_service.domain.model.Route;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.entities.RouteDocument;

@Mapper(componentModel = "spring")
public interface RouteMapper {

    RouteDocument toDocument(Route route);

    Route toDomain(RouteDocument routeDocument);

}
