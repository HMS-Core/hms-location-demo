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

package com.hms.locationsample6.fusedlocation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.hms.locationsample6.logger.LocationLog
import com.hms.locationsample6.useractivity.ActivityIdentificationActivity
import com.hms.locationsample6.utils.Utils.ACTION_PROCESS_LOCATION
import com.hms.locationsample6.utils.Utils.TAG
import com.hms.locationsample6.utils.Utils.isListenActivityConversion
import com.hms.locationsample6.utils.Utils.isListenActivityIdentification
import com.huawei.hms.location.ActivityConversionResponse
import com.huawei.hms.location.ActivityIdentificationResponse
import com.huawei.hms.location.LocationAvailability
import com.huawei.hms.location.LocationResult

class LocationBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null) {
            val action = intent.action
            val sb = StringBuilder()
            if (ACTION_PROCESS_LOCATION == action) {
                // Processing LocationResult information
                Log.i(TAG, "null != intent")
                var messageBack = ""
                val activityTransitionResult =
                    ActivityConversionResponse.getDataFromIntent(intent)
                if (activityTransitionResult != null && isListenActivityConversion) {
                    val list =
                        activityTransitionResult.activityConversionDatas
                    for (i in list.indices) {
                        Log.i(TAG, "activityTransitionEvent[  $i ] $list[i]")
                        messageBack += """${list[i]}""".trimIndent()
                    }
                    LocationLog.d("TAG", messageBack)
                }
                val activityRecognitionResult =
                    ActivityIdentificationResponse.getDataFromIntent(intent)
                if (activityRecognitionResult != null && isListenActivityIdentification) {
                    LocationLog.i(TAG, "activityRecognitionResult:$activityRecognitionResult")
                    val list =
                        activityRecognitionResult.activityIdentificationDatas
                    ActivityIdentificationActivity.setData(list)
                }
                if (LocationResult.hasResult(intent)) {
                    val result = LocationResult.extractResult(intent)
                    result?.let {
                        val locations =
                            result.locations
                        if (locations.isNotEmpty()) {
                            sb.append("requestLocationUpdatesWithIntent[Longitude,Latitude,Accuracy]:\r\n")
                            for (location in locations) {
                                sb.apply {
                                    append(location.longitude)
                                    append(",")
                                    append(location.latitude)
                                    append(",")
                                    append(location.accuracy)
                                    append("\r\n")
                                }
                            }
                        }
                    }

                    // Processing LocationAvailability information
                    if (LocationAvailability.hasLocationAvailability(intent)) {
                        val locationAvailability =
                            LocationAvailability.extractLocationAvailability(intent)

                        locationAvailability?.let {
                            sb.append(
                                """
                                [locationAvailability]:${locationAvailability.isLocationAvailable}
                                
                                """.trimIndent()
                            )
                        }
                    }
                }
                if (sb.isNotEmpty()) {
                    LocationLog.i(TAG, sb.toString())
                }
            }
        }
    }

}
