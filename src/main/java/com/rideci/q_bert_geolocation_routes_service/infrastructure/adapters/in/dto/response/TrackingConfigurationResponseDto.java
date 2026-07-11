package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.dto.response;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Tracking configuration of a trip participant")
public class TrackingConfigurationResponseDto {

    @Schema(description = "Id of the trip associated with the configuration", example = "trip-123")
    private String tripId;

    @Schema(description = "Id of the participant associated with the configuration", example = "user-456")
    private String participantId;

    @Schema(description = "Interval between location updates, in seconds", example = "5")
    private int updateIntervalSeconds;

    @Schema(description = "Moment of the last update")
    private LocalDateTime updatedAt;

}
