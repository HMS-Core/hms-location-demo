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

package com.hms.locationkit.utils

import com.hms.locationkit.geofence.Data
import com.hms.locationkit.logger.LocationLog
import com.huawei.hms.location.Geofence

object GeoFenceData {
        private var requestCode = 0
        private var geoFences = mutableListOf<Geofence>()

        fun addGeoFence(data: Data) {
            if (!checkStyle(geoFences, data.uniqueId)) {
                LocationLog.d("GeoFenceActivity", "not unique ID!")
                LocationLog.i("GeoFenceActivity", "addGeofence failed!")
                return
            }
            val geoBuild = Geofence.Builder().apply {

                setRoundArea(data.latitude, data.longitude, data.radius)
                setUniqueId(data.uniqueId)
                setConversions(data.conversions)
                setValidContinueTime(data.validContinueTime)
                setDwellDelayTime(data.dwellDelayTime)
                setNotificationInterval(data.notificationInterval)
            }

            geoFences.add(geoBuild.build())
            LocationLog.i("GeoFenceActivity", "addGeofence success!")
        }

        fun createNewList() {
            geoFences = mutableListOf()
        }

        private fun checkStyle(geoFences: MutableList<Geofence>, ID: String?): Boolean {
            for (i in geoFences.indices) {
                if (geoFences[i].uniqueId == ID) return false
            }
            return true
        }

        fun returnList(): MutableList<Geofence> {
            return geoFences
        }

        fun show() {
            if (geoFences.isEmpty()) {
                LocationLog.d("GeoFenceActivity", "no GeoFence Data!")
            }
            for (i in geoFences.indices) {
                LocationLog.d(
                    "GeoFenceActivity", "Unique ID is   ${geoFences[i].uniqueId}"
                )
            }
        }

        fun newRequest() {
            requestCode++
        }

        fun requestCode(): Int {
            return requestCode
        }
}