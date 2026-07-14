package com.rideci.q_bert_geolocation_routes_service.domain.model;

import java.time.LocalDateTime;

import com.rideci.q_bert_geolocation_routes_service.domain.model.enums.VehicleStopStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusRealtimeStatus {

    private String gtfsTripId;

    private String gtfsRouteId;

    private double latitude;

    private double longitude;

    private Double bearing;

    private VehicleStopStatus vehicleStopStatus;

    private LocalDateTime positionTimestamp;

    private LocalDateTime estimatedArrivalAtStop;

    private Integer delaySeconds;

}
