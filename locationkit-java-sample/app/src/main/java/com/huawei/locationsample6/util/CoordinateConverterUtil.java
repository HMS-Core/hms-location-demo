/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.locationsample6.util;

import com.huawei.hms.location.LocationUtils;
import com.huawei.hms.support.api.entity.location.coordinate.LonLat;

/**
 * CoordinateConverterUtil
 *
 * @since 2020-08-22
 */
public class CoordinateConverterUtil {
    private static final int SIXTY = 60;

    private static final int ONE_HUNDRED_EIGHTY = 180;

    private static final double DISTANCE_ONE = 1.1515d;

    private static final double DISTANCE_TWO = 1.60934d;

    private static final int DISTANCE_THREE = 1000;

    /**
     * Coordinate conversion
     *
     * @param latitude latitude
     * @param longitude longitude
     * @param coordType Coordinate Type
     * @return Concatenation of result strings after conversion
     */
    public static String change(double latitude, double longitude, int coordType) {
        StringBuilder buffer = new StringBuilder(0);
        long currentTimeMillisBefore = System.currentTimeMillis();

        LonLat convertLatlon = LocationUtils.convertCoord(latitude, longitude, coordType);
        long currentTimeMillisAfter = System.currentTimeMillis();
        buffer.append("conversion time:")
            .append(System.lineSeparator())
            .append(currentTimeMillisAfter - currentTimeMillisBefore)
            .append(" milliseconds");
        buffer.append(System.lineSeparator());
        if (convertLatlon == null) {
            buffer.append("converted LatLon is null");
        } else {
            buffer.append("we convert the coordinates after 02[Longitude,Latitude]:")
                .append(System.lineSeparator())
                .append(convertLatlon.getLongitude())
                .append(",")
                .append(convertLatlon.getLatitude());
            buffer.append(System.lineSeparator());
            double distance = distance(latitude, longitude, convertLatlon.getLatitude(), convertLatlon.getLongitude());
            buffer.append("difference between the converted 02 and the original gps:")
                .append(System.lineSeparator())
                .append(distance)
                .append("m");
        }
        return buffer.toString();
    }

    static double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2))
            + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        return dist * SIXTY * DISTANCE_ONE * DISTANCE_TWO * DISTANCE_THREE;
    }

    static double deg2rad(double degree) {
        return degree / ONE_HUNDRED_EIGHTY * Math.PI;
    }

    static double rad2deg(double radian) {
        return radian * ONE_HUNDRED_EIGHTY / Math.PI;
    }
}
