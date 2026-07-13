package com.rideci.q_bert_geolocation_routes_service.application.use_cases;

import com.rideci.q_bert_geolocation_routes_service.application.annotation.UseCase;
import com.rideci.q_bert_geolocation_routes_service.domain.model.LocationShare;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.in.ShareLocationUseCase;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.out.GeolocationRepositoryOutPort;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@UseCase
@RequiredArgsConstructor
public class ShareLocationUseCaseImpl implements ShareLocationUseCase {
    
    private final GeolocationRepositoryOutPort geolocationRepositoryOutPort;
    
    @Override
    public Mono<LocationShare> shareLocation(String tripId, String passengerId, String emergencyContactId) {
        return geolocationRepositoryOutPort.shareLocation(tripId, passengerId, emergencyContactId);
    }
    

}
