package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request to start sharing a participant's live location")
public class ShareLocationRequestDto {

    @Schema(description = "Id of the registered user to share with; omit for an external, link-only share",
            example = "user-789")
    private String emergencyContactId;

}
