/**
 * Copyright 2020 Huawei Technologies co, Ltd All
 * Rights reserved
 * Licenced under the Apache License,Version 2.0(the "License");
 * you may not use this file except in compliance with license
 * you may obtain a copy of the license at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by application law or agreed to in writing software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permission and
 * limitations under the License
 */

package com.hms.locationkit.geofence

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.hms.locationkit.logger.LocationLog
import com.huawei.hms.location.Geofence
import com.huawei.hms.location.GeofenceData

class GeoFenceBroadcastReceiver : BroadcastReceiver() {
    companion object {
        const val ACTION_PROCESS_LOCATION =
            "com.hms.locationkit.geofence.GeoFenceBroadcastReceiver.ACTION_PROCESS_LOCATION"
        private const val TAG = "GeoFenceReceiver"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let {

            val action = intent.action
            val sb = StringBuilder()
            val next = "\n"

            if (ACTION_PROCESS_LOCATION == action) {
                val geoFenceData = GeofenceData.getDataFromIntent(intent)
                geoFenceData.let {

                    val errorCode = geoFenceData.errorCode
                    val conversion = geoFenceData.conversion
                    val list =
                        geoFenceData.convertingGeofenceList as MutableList<Geofence>
                    val myLocation =
                        geoFenceData.convertingLocation
                    val status = geoFenceData.isSuccess
                    sb.append("errorCode: $errorCode$next")
                    sb.append("conversion: $conversion$next")
                    for (i in list.indices) {
                        sb.append("geoFence id : ${list[i].uniqueId} $next")
                    }
                    if (myLocation != null) {
                        sb.append("location is : ${myLocation.longitude}  ${myLocation.latitude}  $next")
                    }
                    sb.append("is successful :$status")
                    LocationLog.i(TAG, sb.toString())
                }
            }
        }
    }
}