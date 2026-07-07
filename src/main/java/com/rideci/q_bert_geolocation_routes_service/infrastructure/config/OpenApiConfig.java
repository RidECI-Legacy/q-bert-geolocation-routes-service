package com.rideci.q_bert_geolocation_routes_service.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI geolocationRoutesOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Q-Bert Geolocation Routes Service")
                        .description("""
                                RidECI's reactive service for managing geolocation routes: \
                                route creation and updates, distance/ETA/polyline calculation via TomTom, \
                                and management of pickup points with their geofences.""")
                        .version("v1")
                        .contact(new Contact().name("RidECI")));
    }

}
