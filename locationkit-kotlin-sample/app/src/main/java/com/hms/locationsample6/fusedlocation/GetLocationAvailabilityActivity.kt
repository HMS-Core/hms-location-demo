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

import android.os.Bundle
import android.util.Log
import android.view.View
import com.hms.locationsample6.R
import com.hms.locationsample6.activity.BaseActivity
import com.hms.locationsample6.logger.LocationLog
import com.huawei.hms.location.FusedLocationProviderClient
import com.huawei.hms.location.LocationServices
import kotlinx.android.synthetic.main.activity_location_get_location_availability.*

class GetLocationAvailabilityActivity : BaseActivity(), View.OnClickListener {
    companion object {
        private const val TAG = "GetLocationAvailability"
    }

    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_get_location_availability)
        // Button click listeners
        location_getLocationAvailability.setOnClickListener(this@GetLocationAvailabilityActivity)
        addLogFragment()
        mFusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this@GetLocationAvailabilityActivity)
    }

    /**
     * Obtaining Location Availability
     */
    private fun getLocationAvailability() {
        try {
            val locationAvailability = mFusedLocationProviderClient.locationAvailability
            locationAvailability?.addOnSuccessListener { locationAvail ->
                locationAvailability.let {
                    LocationLog.i(
                        TAG,
                        "getLocationAvailability onSuccess:$locationAvail"
                    )
                }

            }
                ?.addOnFailureListener { e ->
                    LocationLog.e(TAG, "getLocationAvailability onFailure:${e.message}")
                }
        } catch (e: Exception) {
            LocationLog.e(TAG, "getLocationAvailability exception:${e.message}")
        }
    }

    override fun onClick(v: View) {
        try {
            when (v.id) {
                R.id.location_getLocationAvailability -> getLocationAvailability()
            }
        } catch (e: Exception) {
            Log.e(TAG, "getLocationAvailability Exception:$e")
        }
    }
}