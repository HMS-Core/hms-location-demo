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

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.TableLayout
import androidx.core.app.ActivityCompat
import com.hms.locationsample6.R
import com.hms.locationsample6.activity.BaseActivity
import com.hms.locationsample6.logger.LocationLog
import com.hms.locationsample6.utils.JsonDataUtil
import com.huawei.hms.location.*
import kotlinx.android.synthetic.main.activity_hms_hd.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class RequestLocationUpdatesHDWithCallbackActivity : BaseActivity(), View.OnClickListener {
    companion object {
        private const val TAG = "RequestLocationHD"
    }

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private var mLocationHDCallback: LocationCallback? = null
    private var mLocationIndoorCallback: LocationCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hms_hd)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        val tableLayout: TableLayout = callback_table_layout_show
        val locationRequestJson: String = JsonDataUtil.getJson(
            this@RequestLocationUpdatesHDWithCallbackActivity,
            "LocationRequestHd.json",
            true
        )
        initDataDisplayView(tableLayout, locationRequestJson)
        btn_remove_hd.setOnClickListener(this)
        btn_hd.setOnClickListener(this)
        btn_indoorHd.setOnClickListener(this)
        btn_remove_indoorHd.setOnClickListener(this)
        addLogFragment()
        if (ActivityCompat.checkSelfPermission(
                this@RequestLocationUpdatesHDWithCallbackActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this@RequestLocationUpdatesHDWithCallbackActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val strings = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            ActivityCompat.requestPermissions(this, strings, 1)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_hd -> getLocationWithHd()
            R.id.btn_remove_hd -> removeLocationHd()
            R.id.btn_indoorHd -> getLocationWithIndoor()
            R.id.btn_remove_indoorHd -> removeLocationIndoor()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        removeLocationHd()
    }

    private fun removeLocationHd() {
        GlobalScope.launch {
            try {
                fusedLocationProviderClient.removeLocationUpdates(mLocationHDCallback)
                    .addOnSuccessListener { LocationLog.i(TAG, "removeLocationHd onSuccess") }
                    .addOnFailureListener { e ->
                        LocationLog.i(TAG, "removeLocationHd onFailure:${e.message}")
                    }
            } catch (e: Exception) {
                LocationLog.e(TAG, "removeLocationHd exception:${e.message}")
            }
        }
        Log.i(TAG, "call removeLocationUpdatesWithCallback success.")
    }

    private fun removeLocationIndoor() {
        GlobalScope.launch {
            try {
                fusedLocationProviderClient.removeLocationUpdates(mLocationIndoorCallback)
                    .addOnSuccessListener { LocationLog.i(TAG, "removeLocationIndoor onSuccess") }
                    .addOnFailureListener { e ->
                        LocationLog.i(TAG, "removeLocationIndoor onFailure:${e.message}")
                    }
            } catch (e: Exception) {
                LocationLog.e(TAG, "removeLocationIndoor exception:${e.message}")
            }
        }
        Log.i(TAG, "call removeLocationUpdatesWithCallback success.")
    }

    private fun getLocationWithHd() {
        GlobalScope.launch {
            try {
                val locationRequest = LocationRequest()
                val jsonString: String = JsonDataUtil.getJson(
                    this@RequestLocationUpdatesHDWithCallbackActivity,
                    "LocationRequestHd.json",
                    true
                )
                handleData(locationRequest, jsonString)
                // Sets the type of the returned coordinate:
                // COORDINATE_TYPE_WGS84 Indicates that the 84 coordinate is returned.
                // COORDINATE_TYPE_GCJ02 Indicates that the 02 coordinate is returned. The default value is COORDENATE_TYPE_WGS84.
                // If a high-precision coordinate is returned, no conversion is performed.
                locationRequest.coordinateType = LocationRequest.COORDINATE_TYPE_WGS84
                if (null == mLocationHDCallback) {
                    mLocationHDCallback = object : LocationCallback() {
                        override fun onLocationResult(locationRequest: LocationResult) {
                            Log.i(TAG, "getLocationWithHd callback onLocationResult print")
                            logResult(locationRequest)
                        }

                        override fun onLocationAvailability(locationAvailability: LocationAvailability?) {
                            Log.i(TAG, "getLocationWithHd callback onLocationAvailability print")
                            locationAvailability?.let {
                                val flag = locationAvailability.isLocationAvailable
                                LocationLog.i(
                                    TAG,
                                    "onLocationAvailability isLocationAvailable:$flag"
                                )
                            }

                        }
                    }
                }
                fusedLocationProviderClient.requestLocationUpdatesEx(
                    locationRequest, mLocationHDCallback,
                    Looper.getMainLooper()
                ).addOnSuccessListener { LocationLog.i(TAG, "getLocationWithHd onSuccess") }
                    .addOnFailureListener { e ->
                        LocationLog.i(TAG, "getLocationWithHd onFailure:${e.message}")
                    }
            } catch (e: Exception) {
                LocationLog.i(TAG, "getLocationWithHd exception :${e.message}")
            }

        }
    }

    private fun getLocationWithIndoor() {
        GlobalScope.launch {
            try {
                val locationRequest = LocationRequest()
                val jsonString: String = JsonDataUtil.getJson(
                    this@RequestLocationUpdatesHDWithCallbackActivity,
                    "LocationRequestHd.json",
                    true
                )
                handleData(locationRequest, jsonString)
                // Sets the type of the returned coordinate:
                // COORDINATE_TYPE_WGS84 Indicates that the 84 coordinate is returned.
                // COORDINATE_TYPE_GCJ02 Indicates that the 02 coordinate is returned. The default value is COORDENATE_TYPE_WGS84.
                locationRequest.coordinateType = LocationRequest.COORDINATE_TYPE_WGS84
                if (null == mLocationIndoorCallback) {
                    mLocationIndoorCallback = object : LocationCallback() {
                        override fun onLocationResult(locationResult: LocationResult) {
                            Log.i(TAG, "getLocationWithIndoor callback onLocationResult print")
                            logResultIndoor(locationResult)
                        }

                        override fun onLocationAvailability(locationAvailability: LocationAvailability) {
                            Log.i(
                                TAG,
                                "getLocationWithIndoor callback onLocationAvailability print"
                            )
                            locationAvailability?.let {
                                val flag = locationAvailability.isLocationAvailable
                                LocationLog.i(
                                    TAG,
                                    "onLocationAvailability isLocationAvailable:$flag"
                                )
                            }
                        }
                    }
                }
                fusedLocationProviderClient.requestLocationUpdatesEx(
                    locationRequest, mLocationIndoorCallback,
                    Looper.getMainLooper()
                ).addOnSuccessListener {
                    LocationLog.i(TAG, "getLocationWithIndoor onSuccess")
                }.addOnFailureListener { e ->
                    LocationLog.i(TAG, "getLocationWithIndoor onFailure:" + e.message)
                }
            } catch (e: Exception) {
                LocationLog.i(
                    TAG, "getLocationWithIndoor exception :" + e.message
                )
            }
        }
    }

    private fun logResult(locationRequest: LocationResult?) {
        locationRequest?.let {
            Log.i(TAG, "getLocationWithHd callback  onLocationResult locationResult is not null")
            val locations = locationRequest.getLocations()
            if (locations.isNotEmpty()) {
                Log.i(TAG, "getLocationWithHd callback  onLocationResult location is not empty")
                logLocation(locations)
            }
            val hwLocations =
                locationRequest.hwLocationList
            if (hwLocations.isNotEmpty()) {
                Log.i(TAG, "getLocationWithHd callback  onLocationResult location is not empty")
                for (hwLocation in hwLocations) {

                    if (hwLocation.countryName.isEmpty()) {
                        return
                    }
                    LocationLog.i(
                        TAG,
                        """[new]location result :Longitude = ${hwLocation.longitude}Latitude = ${hwLocation.latitude}Accuracy = ${hwLocation.accuracy}${hwLocation.countryName},${hwLocation.state},${hwLocation.city},${hwLocation.county},${hwLocation.featureName}""".trimIndent()
                    )
                }
            }
        }
    }

    private fun logHwLocationIndoor(hwLocations: List<HWLocation>?) {
        if (hwLocations == null || hwLocations.isEmpty()) {
            Log.i(TAG, "getLocationWithHd callback hwLocations is empty")
            return
        }
        for (hwLocation in hwLocations) {
            if (hwLocation == null) {
                Log.i(TAG, "getLocationWithHd callback hwLocation is empty")
                return
            }
            LocationLog.i(
                TAG,
                """
                    [new]location result : Longitude = ${hwLocation.longitude}
                    Latitude = ${hwLocation.latitude}
                    Accuracy = ${hwLocation.accuracy}
                    """.trimIndent()
            )
            val maps = hwLocation.extraInfo
            parseIndoorLocation(maps)
        }
    }

    private fun logResultIndoor(locationResult: LocationResult?) {
        if (locationResult != null) {
            Log.i(
                TAG,
                "getLocationWithHd callback  onLocationResult locationResult is not null"
            )
            logLocationIndoor(locationResult.locations)
            logHwLocationIndoor(locationResult.hwLocationList)
        }
    }

    private fun logLocationIndoor(locations: List<Location>?) {
        if (locations == null || locations.isEmpty()) {
            Log.i(TAG, "getLocationWithHd callback locations is empty")
            return
        }
        for (location in locations) {
            if (location == null) {
                Log.i(TAG, "getLocationWithHd callback location is empty")
                return
            }
            LocationLog.i(
                TAG,
                """
                    [old]location result : 
                    Longitude = ${location.longitude}
                    Latitude = ${location.latitude}
                    Accuracy = ${location.accuracy}
                    """.trimIndent()
            )
            val extraInfo = location.extras
            parseIndoorLocation(extraInfo)
        }
    }

    // Parsing Indoor Location Result Information
    private fun parseIndoorLocation(maps: Map<String?, Any?>?) {
        if (maps != null && maps.isNotEmpty()) {
            if (maps.containsKey("isHdNlpLocation")) {
                val hdLocation = maps["isHdNlpLocation"]
                if (hdLocation is Boolean) {
                    if (hdLocation) {
                        // Parsing Indoor Location Result Information
                        // floor:Floor ID
                        // (For example, 1 corresponds to F1. The mapping varies with buildings. For details, contact the operation personnel.)
                        val floor = maps["floor"] as Int
                        // floorAcc:Floor confidence (value range: 0-100)
                        val floorAcc = maps["floorAcc"] as Int
                        LocationLog.i(
                            TAG,
                            "[new]location result : \nfloor = $floor\nfloorAcc = $floorAcc"
                        )
                    }
                }
            }
        }
    }

    // Parsing Indoor Location Result Information
    private fun parseIndoorLocation(extraInfo: Bundle?) {
        if (extraInfo == null) {
            return
        }
        if (extraInfo.getBoolean("isHdNlpLocation", false)) {

            // floor:Floor ID
            // (For example, 1 corresponds to F1. The mapping varies with buildings. For details, contact the operation personnel.)
            val floor = extraInfo.getInt("floor", Int.MIN_VALUE)
            // floorAcc:Floor confidence (value range: 0-100)
            val floorAcc = extraInfo.getInt("floorAcc", Int.MIN_VALUE)
            LocationLog.i(
                TAG,
                "[old]location result : \nfloor = $floor\nfloorAcc = $floorAcc"
            )
        }
    }


    private fun logLocation(locations: List<Location>?) {
        if (locations == null || locations.isEmpty()) {
            Log.i(
                TAG,
                "getLocationWithHd callback locations is empty"
            )
            return
        }
        for (location in locations) {
            if (location == null) {
                Log.i(
                    TAG,
                    "getLocationWithHd callback location is empty"
                )
                return
            }
            LocationLog.i(
                TAG,
                """
                    [old]location result : 
                    Longitude = ${location.longitude}
                    Latitude = ${location.latitude}
                    Accuracy = ${location.accuracy}
                    """.trimIndent()
            )
        }
    }
}