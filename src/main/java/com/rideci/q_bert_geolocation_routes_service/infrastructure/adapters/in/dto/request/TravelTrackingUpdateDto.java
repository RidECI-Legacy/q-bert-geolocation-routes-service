package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.dto.request;

import com.rideci.q_bert_geolocation_routes_service.domain.model.enums.ParticipantRole;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Real-time location update sent by a trip participant")
public class TravelTrackingUpdateDto {

    @DecimalMin(value = "-90.0")
    @DecimalMax(value = "90.0")
    @Schema(description = "Latitude", example = "4.710989")
    private double latitude;

    @DecimalMin(value = "-180.0")
    @DecimalMax(value = "180.0")
    @Schema(description = "Longitude", example = "-74.072092")
    private double longitude;

    @PositiveOrZero
    @Schema(description = "Current speed in km/h", example = "42.5")
    private double speed;

    @Schema(description = "Current heading in degrees", example = "180.0")
    private double heading;

    @NotNull(message = "participantRole cannot be null")
    @Schema(description = "Role of the participant sending the update")
    private ParticipantRole participantRole;

}
