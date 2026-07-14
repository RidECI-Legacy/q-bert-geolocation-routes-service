package com.rideci.q_bert_geolocation_routes_service.infrastructure.config;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.channel.ChannelOption;
import lombok.RequiredArgsConstructor;
import reactor.netty.http.client.HttpClient;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {

    private final TomTomProperties tomTomProperties;
    private final OtpProperties otpProperties;
    private final GtfsRealtimeProperties gtfsRealtimeProperties;

    @Bean
    public WebClient tomTomWebClient() {
        return buildWebClient(tomTomProperties.getBaseUrl());
    }

    @Bean
    public WebClient otpWebClient() {
        return buildWebClient(otpProperties.getBaseUrl());
    }

    @Bean
    public WebClient gtfsRealtimeWebClient() {
        return buildWebClient(gtfsRealtimeProperties.getFeedUrl());
    }

    private WebClient buildWebClient(String baseUrl) {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                .responseTimeout(Duration.ofSeconds(3));

        return WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

}
