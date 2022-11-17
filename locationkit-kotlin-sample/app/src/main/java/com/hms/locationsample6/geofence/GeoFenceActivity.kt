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

package com.hms.locationsample6.geofence

import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import com.hms.locationsample6.R
import com.hms.locationsample6.activity.BaseActivity
import com.hms.locationsample6.logger.LocationLog
import com.hms.locationsample6.utils.GeoFenceData
import com.huawei.hms.location.*
import kotlinx.android.synthetic.main.activity_geo_fence.*

class GeoFenceActivity : BaseActivity(), View.OnClickListener {
    private var mLocationCallback: LocationCallback? = null
    private var mLocationRequest: LocationRequest? = null
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var mSettingsClient: SettingsClient

    companion object {
        private const val TAG = "GeoFenceActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_geo_fence)
        getCurrentLocation.setOnClickListener(this)
        geofence_btn.setOnClickListener(this)
        showGeoList.setOnClickListener(this)
        addLogFragment()
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        mSettingsClient = LocationServices.getSettingsClient(this)
        mLocationRequest = LocationRequest()
        // Sets the interval for location update (unit: Millisecond)
        mLocationRequest?.interval = 5000
        // Sets the priority
        mLocationRequest?.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        if (null == mLocationCallback) {
            mLocationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    val locations =
                        locationResult.locations
                    if (locations.isNotEmpty()) {
                        for (location in locations) {
                            setLatitude.setText(location.latitude.toString())
                            setLongitude.setText(location.longitude.toString())
                            removeLocationUpdatesWithCallback()
                        }
                    }
                }

                override fun onLocationAvailability(locationAvailability: LocationAvailability?) {

                    locationAvailability?.let {
                        val flag = locationAvailability.isLocationAvailable
                        LocationLog.i(TAG, "onLocationAvailability isLocationAvailable:$flag")
                    }
                }
            }
        }
    }

    private fun getText() {
        val temp = Data().apply {
            longitude = setLongitude?.text.toString().toDouble()
            latitude = setLatitude?.text.toString().toDouble()
            radius = setRadius?.text.toString().toFloat()
            uniqueId = setUniqueId?.text.toString()
            conversions = setConversions?.text.toString().toInt()
            validContinueTime = setValidContinueTime?.text.toString().toLong()
            dwellDelayTime = setDwellDelayTime?.text.toString().toInt()
            notificationInterval = setNotificationInterval?.text.toString().toInt()
        }
        GeoFenceData.addGeoFence(temp)
    }

    private fun getLocation() {
        requestLocationUpdatesWithCallback()
    }

    override fun onClick(v: View) {
        try {
            when (v.id) {
                R.id.geofence_btn -> getText()
                R.id.showGeoList -> GeoFenceData.show()
                R.id.getCurrentLocation -> getLocation()
            }
        } catch (e: Exception) {
            LocationLog.e(TAG, "GeoFenceActivity Exception:$e")
        }
    }

    private fun requestLocationUpdatesWithCallback() {
        try {
            val builder = LocationSettingsRequest.Builder()
            builder.addLocationRequest(mLocationRequest)
            val locationSettingsRequest = builder.build()
            // Before requesting location update, invoke checkLocationSettings to check device settings.
            val locationSettingsResponseTask =
                mSettingsClient.checkLocationSettings(locationSettingsRequest)
            locationSettingsResponseTask.addOnSuccessListener {
                Log.i(TAG, "check location settings success")
                mFusedLocationProviderClient.requestLocationUpdates(
                    mLocationRequest,
                    mLocationCallback,
                    Looper.getMainLooper()
                )
                    .addOnSuccessListener {
                        LocationLog.i(
                            TAG,
                            "requestLocationUpdatesWithCallback onSuccess"
                        )
                    }
                    .addOnFailureListener { e ->
                        LocationLog.e(
                            TAG,
                            "requestLocationUpdatesWithCallback onFailure: ${e.message}"
                        )
                    }
            }.addOnFailureListener { e ->
                LocationLog.e(TAG, "checkLocationSetting onFailure: ${e.message}")
            }
        } catch (e: Exception) {
            LocationLog.e(TAG, "requestLocationUpdatesWithCallback exception: ${e.message}")
        }
    }

    private fun removeLocationUpdatesWithCallback() {
        try {
            val voidTask =
                mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback)
            voidTask.addOnSuccessListener {
                LocationLog.i(
                    TAG,
                    "removeLocationUpdatesWithCallback onSuccess"
                )
            }
                .addOnFailureListener { e ->
                    LocationLog.e(
                        TAG,
                        "removeLocationUpdatesWithCallback onFailure: ${e.message}"
                    )
                }
        } catch (e: Exception) {
            LocationLog.e(TAG, "removeLocationUpdatesWithCallback exception: ${e.message}")
        }
    }
}

class Data {
    var latitude = 0.0
    var longitude = 0.0
    var radius = 0f
    var uniqueId: String? = null
    var conversions = 0
    var validContinueTime: Long = 0
    var dwellDelayTime = 0
    var notificationInterval = 0
}
