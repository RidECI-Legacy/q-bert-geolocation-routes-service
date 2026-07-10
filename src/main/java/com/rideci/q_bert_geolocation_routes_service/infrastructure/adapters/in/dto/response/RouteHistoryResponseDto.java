package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.dto.response;

import java.time.LocalDateTime;

import com.rideci.q_bert_geolocation_routes_service.domain.model.enums.ParticipantRole;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "A recorded point of a participant's route, used to replay a trip")
public class RouteHistoryResponseDto {

    @Schema(description = "Id of the participant this point belongs to", example = "user-456")
    private String participantId;

    @Schema(description = "Speed recorded at this point in km/h")
    private double speed;

    @Schema(description = "Heading recorded at this point in degrees")
    private double heading;

    @Schema(description = "Location recorded at this point")
    private LocationResponseDto location;

    @Schema(description = "Role of the participant in the trip")
    private ParticipantRole participantRole;

    @Schema(description = "Moment at which this point was recorded")
    private LocalDateTime recordedAt;

}
