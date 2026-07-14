package com.rideci.q_bert_geolocation_routes_service.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "otp")
public class OtpProperties {

    private String baseUrl;

}
