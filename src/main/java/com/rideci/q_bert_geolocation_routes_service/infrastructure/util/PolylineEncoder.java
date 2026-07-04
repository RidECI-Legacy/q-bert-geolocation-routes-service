package com.rideci.q_bert_geolocation_routes_service.infrastructure.util;

import java.util.List;

import com.rideci.q_bert_geolocation_routes_service.domain.model.Location;

/**
 * Google Encoded Polyline Algorithm Format (precision 1e5), used to turn the
 * list of points returned by TomTom's Routing API into the {@code polyline}
 * string expected by the domain model.
 */
public final class PolylineEncoder {

    private static final double PRECISION = 1e5;

    private PolylineEncoder() {
    }

    public static String encode(List<Location> path) {
        StringBuilder result = new StringBuilder();
        long lastLat = 0;
        long lastLng = 0;

        for (Location point : path) {
            long lat = Math.round(point.getLatitude() * PRECISION);
            long lng = Math.round(point.getLongitude() * PRECISION);

            encodeSignedNumber(lat - lastLat, result);
            encodeSignedNumber(lng - lastLng, result);

            lastLat = lat;
            lastLng = lng;
        }

        return result.toString();
    }

    private static void encodeSignedNumber(long num, StringBuilder result) {
        long signedNum = num << 1;
        if (num < 0) {
            signedNum = ~signedNum;
        }
        encodeNumber(signedNum, result);
    }

    private static void encodeNumber(long num, StringBuilder result) {
        while (num >= 0x20) {
            result.append((char) ((0x20 | (num & 0x1f)) + 63));
            num >>= 5;
        }
        result.append((char) (num + 63));
    }

}
