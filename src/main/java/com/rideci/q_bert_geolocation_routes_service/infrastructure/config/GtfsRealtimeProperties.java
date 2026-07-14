package com.rideci.q_bert_geolocation_routes_service.infrastructure.config;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "gtfs-realtime")
public class GtfsRealtimeProperties {

    private String feedUrl;

    private Duration pollInterval;

}
