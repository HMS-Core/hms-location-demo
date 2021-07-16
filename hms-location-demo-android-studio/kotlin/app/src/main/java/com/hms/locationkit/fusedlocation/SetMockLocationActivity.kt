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

package com.hms.locationkit.fusedlocation

import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import com.hms.locationkit.R
import com.hms.locationkit.activity.BaseActivity
import com.hms.locationkit.logger.LocationLog
import com.huawei.hms.location.FusedLocationProviderClient
import com.huawei.hms.location.LocationServices
import kotlinx.android.synthetic.main.activity_location_set_mock_location.*

class SetMockLocationActivity : BaseActivity(), View.OnClickListener {
    companion object {
        private const val TAG = "SetMockLocationActivity"
    }

    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_set_mock_location)
        // Button click listeners
        location_setMockLocation.setOnClickListener(this)
        addLogFragment()
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
    }

/*
     * Set the specific mock location.
     */

    private fun setMockLocation() {
        try {
            //Fill in the information sources such as gps and network based on the application situation.
            val mockLocation = Location(
                LocationManager.GPS_PROVIDER
            )
            mockLocation.longitude = 118.76
            mockLocation.latitude = 31.98
            // Note: To enable the mock function, enable the android.permission.ACCESS_MOCK_LOCATION permission in the AndroidManifest.xml file,
            // and set the application to the mock location app in the device setting.
            val voidTask =
                mFusedLocationProviderClient?.setMockLocation(mockLocation)
            voidTask?.addOnSuccessListener {
                LocationLog.i(
                    TAG,
                    "setMockLocation onSuccess"
                )
            }
                ?.addOnFailureListener { e ->
                    LocationLog.e(
                        TAG,
                        "setMockLocation onFailure:${e.message}"
                    )
                }
        } catch (e: Exception) {
            LocationLog.e(TAG, "setMockLocation exception:${e.message}")
        }
    }

    override fun onClick(v: View) {
        try {
            when (v.id) {
                R.id.location_setMockLocation -> setMockLocation()
            }
        } catch (e: Exception) {
            Log.e(TAG, "setMockLocation Exception:$e")
        }
    }
}
