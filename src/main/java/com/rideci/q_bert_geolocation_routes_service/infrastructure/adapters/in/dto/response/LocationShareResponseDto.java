package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.dto.response;

import java.time.LocalDateTime;

import com.rideci.q_bert_geolocation_routes_service.domain.model.enums.ShareStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "A live-location sharing session")
public class LocationShareResponseDto {

    @Schema(description = "Id of the share; also acts as the access token for the /ws/shares/{shareId} WebSocket",
            example = "3f9a1c2e-...")
    private String id;

    @Schema(description = "Id of the trip being shared", example = "trip-123")
    private String tripId;

    @Schema(description = "Id of the participant whose location is being shared", example = "user-456")
    private String participantId;

    @Schema(description = "Id of the registered user this was shared with, if any", example = "user-789")
    private String emergencyContactId;

    @Schema(description = "Last known location of the participant, if available")
    private LocationResponseDto currentLocation;

    @Schema(description = "Status of the share")
    private ShareStatus shareStatus;

    @Schema(description = "Moment the share was created")
    private LocalDateTime createdAt;

    @Schema(description = "Moment of the last update")
    private LocalDateTime updatedAt;

}
