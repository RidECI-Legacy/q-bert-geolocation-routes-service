package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.repositories;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.rideci.q_bert_geolocation_routes_service.domain.exception.TomTomIntegrationException;
import com.rideci.q_bert_geolocation_routes_service.domain.model.Location;
import com.rideci.q_bert_geolocation_routes_service.domain.model.PickUpPoint;
import com.rideci.q_bert_geolocation_routes_service.domain.model.RouteInfo;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.out.TomTomOutPort;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.dto.tomtom.TomTomGeocodeResponse;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.dto.tomtom.TomTomReverseGeocodeResponse;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.dto.tomtom.TomTomRouteResponse;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.config.TomTomProperties;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.util.PolylineEncoder;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class TomTomAdapter implements TomTomOutPort {

    private static final double EARTH_RADIUS_METERS = 6_371_000;
    private static final String RESILIENCE_INSTANCE = "tomtom";

    private final WebClient tomTomWebClient;
    private final TomTomProperties tomTomProperties;

    @Override
    @CircuitBreaker(name = RESILIENCE_INSTANCE, fallbackMethod = "calculateRouteFallback")
    @Retry(name = RESILIENCE_INSTANCE)
    @TimeLimiter(name = RESILIENCE_INSTANCE)
    public Mono<RouteInfo> calculateRoute(Location origin, Location destination, List<PickUpPoint> waypoints) {
        List<Location> locations = new ArrayList<>();
        locations.add(origin);
        waypoints.forEach(waypoint -> locations.add(waypoint.getLocation()));
        locations.add(destination);

        return callRoutingApi(locations, false).map(this::toRouteInfo);
    }

    @SuppressWarnings("unused")
    private Mono<RouteInfo> calculateRouteFallback(Location origin, Location destination,
            List<PickUpPoint> waypoints, Throwable cause) {
        return Mono.error(new TomTomIntegrationException("TomTom routing API unavailable", cause));
    }

    @Override
    @CircuitBreaker(name = RESILIENCE_INSTANCE, fallbackMethod = "optimizeWaypointsFallback")
    @Retry(name = RESILIENCE_INSTANCE)
    @TimeLimiter(name = RESILIENCE_INSTANCE)
    public Mono<List<PickUpPoint>> optimizeWaypoints(List<PickUpPoint> waypoints) {
        if (waypoints.size() < 3) {
            return Mono.just(waypoints);
        }

        List<Location> locations = waypoints.stream().map(PickUpPoint::getLocation).toList();

        return callRoutingApi(locations, true).map(response -> reorder(waypoints, response));
    }

    @SuppressWarnings("unused")
    private Mono<List<PickUpPoint>> optimizeWaypointsFallback(List<PickUpPoint> waypoints, Throwable cause) {
        return Mono.error(new TomTomIntegrationException("TomTom waypoint optimization API unavailable", cause));
    }

    @Override
    @CircuitBreaker(name = RESILIENCE_INSTANCE, fallbackMethod = "snapToRoadFallback")
    @Retry(name = RESILIENCE_INSTANCE)
    @TimeLimiter(name = RESILIENCE_INSTANCE)
    public Mono<List<Location>> snapToRoad(List<Location> rawPositions) {
        if (rawPositions.size() < 2) {
            return Mono.just(rawPositions);
        }

        return callRoutingApi(rawPositions, false).map(this::toPath);
    }

    @SuppressWarnings("unused")
    private Mono<List<Location>> snapToRoadFallback(List<Location> rawPositions, Throwable cause) {
        return Mono.error(new TomTomIntegrationException("TomTom snap-to-road API unavailable", cause));
    }

    @Override
    @CircuitBreaker(name = RESILIENCE_INSTANCE, fallbackMethod = "geocodeAddressFallback")
    @Retry(name = RESILIENCE_INSTANCE)
    @TimeLimiter(name = RESILIENCE_INSTANCE)
    public Mono<Location> geocodeAddress(String address) {
        URI uri = URI.create(tomTomProperties.getBaseUrl() + "/search/2/geocode/"
                + URLEncoder.encode(address, StandardCharsets.UTF_8) + ".json?key=" + encodedApiKey());

        return tomTomWebClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(TomTomGeocodeResponse.class)
                .flatMap(response -> {
                    if (response.results() == null || response.results().isEmpty()) {
                        return Mono
                                .error(new TomTomIntegrationException("No geocoding result for address: " + address));
                    }
                    TomTomGeocodeResponse.Position position = response.results().get(0).position();
                    return Mono.just(Location.builder()
                            .latitude(position.lat())
                            .longitude(position.lon())
                            .timestamp(LocalDateTime.now())
                            .build());
                });
    }

    @SuppressWarnings("unused")
    private Mono<Location> geocodeAddressFallback(String address, Throwable cause) {
        return Mono.error(new TomTomIntegrationException("TomTom geocoding API unavailable", cause));
    }

    @Override
    @CircuitBreaker(name = RESILIENCE_INSTANCE, fallbackMethod = "reverseGeocodeFallback")
    @Retry(name = RESILIENCE_INSTANCE)
    @TimeLimiter(name = RESILIENCE_INSTANCE)
    public Mono<String> reverseGeocode(Location location) {
        String position = location.getLatitude() + "," + location.getLongitude();
        URI uri = URI.create(tomTomProperties.getBaseUrl() + "/search/2/reverseGeocode/" + position
                + ".json?key=" + encodedApiKey());

        return tomTomWebClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(TomTomReverseGeocodeResponse.class)
                .flatMap(response -> {
                    if (response.addresses() == null || response.addresses().isEmpty()) {
                        return Mono.error(new TomTomIntegrationException("No reverse geocoding result for location"));
                    }
                    return Mono.just(response.addresses().get(0).address().freeformAddress());
                });
    }

    @SuppressWarnings("unused")
    private Mono<String> reverseGeocodeFallback(Location location, Throwable cause) {
        return Mono.error(new TomTomIntegrationException("TomTom reverse geocoding API unavailable", cause));
    }

    @Override
    public boolean isPointInsideGeofence(Location currentPosition, PickUpPoint pickUpPoint) {
        Location center = pickUpPoint.getLocation();
        double distanceMeters = haversineDistanceMeters(currentPosition, center);
        return distanceMeters <= pickUpPoint.getGeofenceConfig().getRadiusMeters();
    }

    private double haversineDistanceMeters(Location a, Location b) {
        double lat1 = Math.toRadians(a.getLatitude());
        double lat2 = Math.toRadians(b.getLatitude());
        double deltaLat = Math.toRadians(b.getLatitude() - a.getLatitude());
        double deltaLon = Math.toRadians(b.getLongitude() - a.getLongitude());

        double h = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                + Math.cos(lat1) * Math.cos(lat2) * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(h), Math.sqrt(1 - h));

        return EARTH_RADIUS_METERS * c;
    }

    private Mono<TomTomRouteResponse> callRoutingApi(List<Location> locations, boolean computeBestOrder) {
        URI uri = URI.create(tomTomProperties.getBaseUrl() + "/routing/1/calculateRoute/"
                + toLocationsPath(locations) + "/json?key=" + encodedApiKey()
                + "&traffic=true&computeBestOrder=" + computeBestOrder);

        return tomTomWebClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(TomTomRouteResponse.class);
    }

    private String encodedApiKey() {
        return URLEncoder.encode(tomTomProperties.getApiKey(), StandardCharsets.UTF_8);
    }

    private String toLocationsPath(List<Location> locations) {
        return locations.stream()
                .map(location -> location.getLatitude() + "," + location.getLongitude())
                .collect(Collectors.joining(":"));
    }

    private RouteInfo toRouteInfo(TomTomRouteResponse response) {
        TomTomRouteResponse.TomTomRoute route = response.routes().get(0);

        return RouteInfo.builder()
                .totalDistance(route.summary().lengthInMeters())
                .totalDuration(route.summary().travelTimeInSeconds())
                .estimatedArrivalTime(route.summary().arrivalTime().toLocalDateTime())
                .polyline(PolylineEncoder.encode(toPath(response)))
                .build();
    }

    private List<Location> toPath(TomTomRouteResponse response) {
        return response.routes().get(0).legs().stream()
                .flatMap(leg -> leg.points().stream())
                .map(point -> Location.builder().latitude(point.latitude()).longitude(point.longitude()).build())
                .toList();
    }

    private List<PickUpPoint> reorder(List<PickUpPoint> waypoints, TomTomRouteResponse response) {
        PickUpPoint[] reordered = new PickUpPoint[waypoints.size()];
        for (TomTomRouteResponse.OptimizedWaypoint optimizedWaypoint : response.routes().get(0).optimizedWaypoints()) {
            reordered[optimizedWaypoint.optimizedIndex()] = waypoints.get(optimizedWaypoint.providedIndex());
        }
        return Arrays.asList(reordered);
    }

}
