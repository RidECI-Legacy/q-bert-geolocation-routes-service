package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.dto.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@Schema(description = "Data to create or update a geolocation route")
public class RouteRequestDto {

    @NotNull(message = "tripId cannot be null")
    @NotBlank(message = "tripId cannot be blank")
    @Schema(description = "Id of the trip associated with the route", example = "trip-123")
    private String tripId;

    @NotEmpty(message = "origin cannot be empty")
    @NotNull(message = "origin cannot be null")
    @Schema(description = "Origin point of the route")
    private LocationRequestDto origin;

    @NotEmpty(message = "destination cannot be empty")
    @NotNull(message = "origin cannot be null")
    @Schema(description = "Destination point of the route")
    private LocationRequestDto destination;

    @NotNull(message = "pickup points cannot be null")
    @Schema(description = "Pickup points to include in the route")
    private List<PickUpPointRequestDto> pickUpPoints;

}
