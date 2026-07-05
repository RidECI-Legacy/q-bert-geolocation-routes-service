package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.controller.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.rideci.q_bert_geolocation_routes_service.domain.model.Location;
import com.rideci.q_bert_geolocation_routes_service.domain.model.PickUpPoint;
import com.rideci.q_bert_geolocation_routes_service.domain.model.Route;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.controller.dto.request.RouteRequestDto;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.controller.dto.response.LocationResponseDto;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.controller.dto.response.PickUpPointResponseDto;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.controller.dto.response.RouteResponseDto;

@Mapper(componentModel = "spring")
public interface RouteControllerMapper {

    Route toDomain(RouteRequestDto routeRequest);

    RouteResponseDto toResponse(Route route);

    List<RouteResponseDto> toListResponse(List<Route> routes);

    List<Route> toListDomain(List<RouteRequestDto> routeRequests);

    LocationResponseDto toLocationResponse(Location location);

    PickUpPointResponseDto toPickUpPointResponse(PickUpPoint pickUpPoint);
}
