package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.dto.request;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@Schema(description = "Data to create or update a companion (public transit) route")
public class CompanionRouteRequestDto {

    @NotNull(message = "tripId cannot be null")
    @NotBlank(message = "tripId cannot be blank")
    @Schema(description = "Id of the trip associated with the companion route", example = "trip-123")
    private String tripId;

    @NotNull(message = "origin cannot be null")
    @Valid
    @Schema(description = "Origin point of the companion route")
    private LocationRequestDto origin;

    @NotNull(message = "destination cannot be null")
    @Valid
    @Schema(description = "Destination point of the companion route")
    private LocationRequestDto destination;

    @Schema(description = "Desired departure time; omit to plan leaving now", example = "2026-07-13T08:30:00")
    private LocalDateTime departureTime;

}
