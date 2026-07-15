package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.mapper;

import org.mapstruct.Mapper;

import com.rideci.q_bert_geolocation_routes_service.domain.model.BusRealtimeStatus;
import com.rideci.q_bert_geolocation_routes_service.domain.model.CompanionRoute;
import com.rideci.q_bert_geolocation_routes_service.domain.model.TransitLeg;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.dto.request.CompanionRouteRequestDto;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.dto.response.CompanionRouteResponseDto;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.dto.response.TransitLegResponseDto;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.dto.response.VehicleStatusResponseDto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Mapper(componentModel = "spring")
public interface CompanionRouteControllerMapper {

    CompanionRoute toDomain(CompanionRouteRequestDto companionRouteRequest);

    CompanionRouteResponseDto toResponse(CompanionRoute companionRoute);

    TransitLegResponseDto toLegResponse(TransitLeg transitLeg);

    VehicleStatusResponseDto toResponse(BusRealtimeStatus busRealtimeStatus);

    default Mono<CompanionRoute> toDomain(Mono<CompanionRouteRequestDto> companionRouteRequestMono) {
        return companionRouteRequestMono.map(this::toDomain);
    }

    default Mono<CompanionRouteResponseDto> toResponse(Mono<CompanionRoute> companionRouteMono) {
        return companionRouteMono.map(this::toResponse);
    }

    default Flux<CompanionRouteResponseDto> toListResponse(Flux<CompanionRoute> companionRoutesFlux) {
        return companionRoutesFlux.map(this::toResponse);
    }

}
