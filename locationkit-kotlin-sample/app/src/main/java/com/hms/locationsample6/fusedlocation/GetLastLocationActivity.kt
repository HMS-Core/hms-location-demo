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
import com.huawei.hmf.tasks.OnSuccessListener
import com.huawei.hms.location.FusedLocationProviderClient
import com.huawei.hms.location.LocationServices
import kotlinx.android.synthetic.main.activity_location_get_last_location.*

class GetLastLocationActivity : BaseActivity(), View.OnClickListener {
    companion object {
        private const val TAG = "GetLastLocationActivity"
    }

    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_get_last_location)
        // Button click listeners
        location_getLastLocation?.setOnClickListener(this@GetLastLocationActivity)
        addLogFragment()
        //Creating a Location Service Client
        mFusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(baseContext)
    }

    /**
     * Obtain the last known location
     */
    private fun getLastLocation() {
        try {
            val lastLocation =
                mFusedLocationProviderClient.lastLocation
            lastLocation.addOnSuccessListener(OnSuccessListener { location ->
                if (location == null) {
                    LocationLog.i(TAG, "getLastLocation onSuccess location is null")
                    return@OnSuccessListener
                }
                LocationLog.i(
                    TAG,
                    "getLastLocation onSuccess location[Longitude,Latitude]:${location.longitude},${location.latitude}"
                )
                return@OnSuccessListener
            }).addOnFailureListener { e: Exception ->
                LocationLog.e(TAG, "getLastLocation onFailure:" + e.message)
            }
        } catch (e: Exception) {
            LocationLog.e(TAG, "getLastLocation exception:${e.message}")
        }
    }

    override fun onClick(v: View) {
        try {
            when (v.id) {
                R.id.location_getLastLocation -> getLastLocation()
            }
        } catch (e: Exception) {
            Log.e(TAG, "GetLastLocationActivity Exception:$e")
        }
    }
}