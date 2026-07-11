package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.mapper;

import org.mapstruct.Mapper;

import com.rideci.q_bert_geolocation_routes_service.domain.model.RouteHistory;
import com.rideci.q_bert_geolocation_routes_service.domain.model.TrackingConfiguration;
import com.rideci.q_bert_geolocation_routes_service.domain.model.TravelTracking;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.dto.response.RouteHistoryResponseDto;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.dto.response.TrackingConfigurationResponseDto;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.dto.response.TravelTrackingResponseDto;

@Mapper(componentModel = "spring")
public interface TravelTrackingControllerMapper {

    TravelTrackingResponseDto toResponse(TravelTracking travelTracking);

    RouteHistoryResponseDto toResponse(RouteHistory routeHistory);

    TrackingConfigurationResponseDto toResponse(TrackingConfiguration trackingConfiguration);

}
