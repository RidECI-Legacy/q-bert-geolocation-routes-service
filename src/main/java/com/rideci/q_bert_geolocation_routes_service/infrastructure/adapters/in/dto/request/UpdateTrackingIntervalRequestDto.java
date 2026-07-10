package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Desired interval between location updates of a trip participant")
public class UpdateTrackingIntervalRequestDto {

    @Positive(message = "updateIntervalSeconds must be greater than zero")
    @Schema(description = "Interval between location updates, in seconds", example = "5")
    private int updateIntervalSeconds;

}
