package com.rideci.q_bert_geolocation_routes_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class QBertGeolocationRoutesServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(QBertGeolocationRoutesServiceApplication.class, args);
	}

}
