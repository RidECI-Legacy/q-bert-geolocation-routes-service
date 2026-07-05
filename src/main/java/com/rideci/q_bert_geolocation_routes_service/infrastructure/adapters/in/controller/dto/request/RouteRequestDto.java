package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.controller.dto.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class RouteRequestDto {

    private String tripId;

    private LocationRequestDto origin;

    private LocationRequestDto destination;

    private List<PickUpPointRequestDto> pickUpPoints;

}
