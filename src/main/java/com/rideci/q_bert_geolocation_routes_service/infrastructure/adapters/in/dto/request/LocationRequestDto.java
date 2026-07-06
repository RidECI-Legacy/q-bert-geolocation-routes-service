package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@Schema(description = "Geographic coordinates of a point")
public class LocationRequestDto {

    @NotNull(message = "latitude cannot be null")
    @NotBlank(message = "latitude cannot be blank")
    @DecimalMin(value = "-90.0")
    @DecimalMax(value = "90.0")
    @Schema(description = "Latitude", example = "4.710989")
    private double latitude;

    @NotNull(message = "longitude cannot be null")
    @NotBlank(message = "longitude cannot be blank")
    @DecimalMin(value = "-180.0")
    @DecimalMax(value = "180.0")
    @Schema(description = "Longitude", example = "-74.072092")
    private double longitude;

}
