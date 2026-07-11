package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.dto.response;

import java.time.LocalDateTime;

import com.rideci.q_bert_geolocation_routes_service.domain.model.enums.ParticipantRole;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Current real-time tracking state of a trip participant")
public class TravelTrackingResponseDto {

    @Schema(description = "Id of the trip being tracked", example = "trip-123")
    private String tripId;

    @Schema(description = "Id of the participant being tracked", example = "user-456")
    private String participantId;

    @Schema(description = "Current speed in km/h")
    private double speed;

    @Schema(description = "Current heading in degrees")
    private double heading;

    @Schema(description = "Current location of the participant")
    private LocationResponseDto currentLocation;

    @Schema(description = "Role of the participant in the trip")
    private ParticipantRole participantRole;

    @Schema(description = "Moment of the last update")
    private LocalDateTime updatedAt;

}
