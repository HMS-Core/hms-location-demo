/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */
package com.hms.locationsample6.utils

import com.huawei.hms.location.LocationUtils

/**
 * 功能描述 Coordinate conversion
 *
 * @author lwx1017831
 * @since 2020-08-22
 */
object CoordinateConverterUtil {
    private const val SIXTY = 60
    private const val ONE_HUNDRED_EIGHTY = 180
    private const val DISTANCE_ONE = 1.1515
    private const val DISTANCE_TWO = 1.60934
    private const val DISTANCE_THREE = 1000

    /**
     * Coordinate conversion
     *
     * @param latitude latitude
     * @param longitude longitude
     * @param coordType Coordinate Type
     * @return Concatenation of result strings after conversion
     */
    fun change(latitude: Double, longitude: Double, coordType: Int): String {
        val buffer = StringBuilder(0)
        val currentTimeMillisBefore = System.currentTimeMillis()
        val convertLonlat = LocationUtils.convertCoord(latitude, longitude, coordType)

        val currentTimeMillisAfter = System.currentTimeMillis()
        buffer.append("conversion time:")
            .append(System.lineSeparator())
            .append(currentTimeMillisAfter - currentTimeMillisBefore)
            .append(" milliseconds")
        buffer.append(System.lineSeparator())
        if (convertLonlat == null) {
            buffer.append("converted LatLon is null")
        } else {
            buffer.append("we convert the coordinates after 02[Longitude,Latitude]:")
                .append(System.lineSeparator())
                .append(convertLonlat.longitude)
                .append(",")
                .append(convertLonlat.latitude)
            buffer.append(System.lineSeparator())
            val distance = distance(
                latitude,
                longitude,
                convertLonlat.latitude,
                convertLonlat.longitude
            )
            buffer.append("difference between the converted 02 and the original gps:")
                .append(System.lineSeparator())
                .append(distance)
                .append("m")
        }
        return buffer.toString()
    }

    fun distance(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Double {
        val theta = lon1 - lon2
        var dist =
            (Math.sin(deg2rad(lat1)) * Math.sin(
                deg2rad(lat2)
            )
                    + Math.cos(deg2rad(lat1)) * Math.cos(
                deg2rad(lat2)
            ) * Math.cos(deg2rad(theta)))
        dist = Math.acos(dist)
        dist = rad2deg(dist)
        return dist * SIXTY * DISTANCE_ONE * DISTANCE_TWO * DISTANCE_THREE
    }

    fun deg2rad(degree: Double): Double {
        return degree / ONE_HUNDRED_EIGHTY * Math.PI
    }

    fun rad2deg(radian: Double): Double {
        return radian * ONE_HUNDRED_EIGHTY / Math.PI
    }
}