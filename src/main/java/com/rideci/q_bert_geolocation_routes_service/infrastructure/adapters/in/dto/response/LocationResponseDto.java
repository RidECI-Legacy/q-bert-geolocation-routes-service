package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.dto.response;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Geographic coordinates of a point")
public class LocationResponseDto {

    @Schema(description = "Latitude", example = "4.710989")
    private double latitude;

    @Schema(description = "Longitude", example = "-74.072092")
    private double longitude;

    @Schema(description = "Moment at which the location was recorded")
    private LocalDateTime timestamp;
}
