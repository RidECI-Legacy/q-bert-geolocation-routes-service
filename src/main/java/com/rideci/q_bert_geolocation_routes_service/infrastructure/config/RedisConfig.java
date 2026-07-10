package com.rideci.q_bert_geolocation_routes_service.infrastructure.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.entities.TrackingConfigurationDocument;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.entities.TravelTrackingDocument;

@Configuration
public class RedisConfig {

    @Bean
    public ObjectMapper redisObjectMapper() {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Bean
    public ReactiveRedisTemplate<String, TravelTrackingDocument> travelTrackingRedisTemplate(
            ReactiveRedisConnectionFactory connectionFactory, @Qualifier("redisObjectMapper") ObjectMapper redisObjectMapper) {
        return buildTemplate(connectionFactory, redisObjectMapper, TravelTrackingDocument.class);
    }

    @Bean
    public ReactiveRedisTemplate<String, TrackingConfigurationDocument> trackingConfigurationRedisTemplate(
            ReactiveRedisConnectionFactory connectionFactory, @Qualifier("redisObjectMapper") ObjectMapper redisObjectMapper) {
        return buildTemplate(connectionFactory, redisObjectMapper, TrackingConfigurationDocument.class);
    }

    private <T> ReactiveRedisTemplate<String, T> buildTemplate(ReactiveRedisConnectionFactory connectionFactory,
            ObjectMapper redisObjectMapper, Class<T> type) {
        Jackson2JsonRedisSerializer<T> serializer = new Jackson2JsonRedisSerializer<>(redisObjectMapper, type);

        RedisSerializationContext<String, T> context = RedisSerializationContext.<String, T>newSerializationContext(new StringRedisSerializer())
                .value(serializer)
                .build();

        return new ReactiveRedisTemplate<>(connectionFactory, context);
    }

}
