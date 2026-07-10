package com.rideci.q_bert_geolocation_routes_service.infrastructure.config;

import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;

import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.websocket.TravelReplayWebSocketHandler;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.websocket.TravelTrackingWebSocketHandler;

@Configuration
public class WebSocketConfig {

    @Bean
    public HandlerMapping webSocketHandlerMapping(TravelTrackingWebSocketHandler trackingHandler,
            TravelReplayWebSocketHandler replayHandler) {
        Map<String, WebSocketHandler> urlMap = Map.of(
                "/ws/trips/*/tracking/*", trackingHandler,
                "/ws/trips/*/replay/*", replayHandler);

        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setUrlMap(urlMap);
        mapping.setOrder(-1);
        return mapping;
    }

    @Bean
    public WebSocketHandlerAdapter webSocketHandlerAdapter() {
        return new WebSocketHandlerAdapter();
    }

}
