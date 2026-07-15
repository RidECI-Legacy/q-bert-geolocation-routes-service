package com.rideci.q_bert_geolocation_routes_service.application.use_cases;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.rideci.q_bert_geolocation_routes_service.domain.model.BusRealtimeStatus;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.out.GtfsRealtimeOutPort;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class FetchVehiclePositionsUseCaseImplTest {

    @Mock
    private GtfsRealtimeOutPort gtfsRealtimeOutPort;

    private FetchVehiclePositionsUseCaseImpl fetchVehiclePositionsUseCase;

    @BeforeEach
    void setUp() {
        fetchVehiclePositionsUseCase = new FetchVehiclePositionsUseCaseImpl(gtfsRealtimeOutPort);
    }

    @Test
    void fetchVehiclePositions_delegatesToOutPort() {
        List<BusRealtimeStatus> statuses = List.of(BusRealtimeStatus.builder().gtfsTripId("trip-1").build());

        when(gtfsRealtimeOutPort.fetchVehiclePositions()).thenReturn(Mono.just(statuses));

        StepVerifier.create(fetchVehiclePositionsUseCase.fetchVehiclePositions())
                .expectNext(statuses)
                .verifyComplete();

        verify(gtfsRealtimeOutPort).fetchVehiclePositions();
    }

}
