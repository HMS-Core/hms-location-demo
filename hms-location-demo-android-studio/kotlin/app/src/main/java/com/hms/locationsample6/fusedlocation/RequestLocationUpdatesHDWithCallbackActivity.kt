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

    private fun logResult(locationRequest: LocationResult?) {
        locationRequest?.let {
            Log.i(TAG, "getLocationWithHd callback  onLocationResult locationResult is not null")
            val locations = locationRequest.getLocations()
            var hdFlag: String
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
                    hdFlag = if (hwLocation.accuracy < 2) {
                        "result is HD"
                    } else {
                        ""
                    }
                    LocationLog.i(
                        TAG,
                        """[new]location result :Longitude = ${hwLocation.longitude}Latitude = ${hwLocation.latitude}Accuracy = ${hwLocation.accuracy}${hwLocation.countryName},${hwLocation.state},${hwLocation.city},${hwLocation.county},${hwLocation.featureName}$hdFlag""".trimIndent()
                    )
                }
            }
        }
    }

    private fun logLocation(locations: List<Location>?) {
        var hdFlag = ""
        var hdSecurity = ""
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
            var hdbBinary = false
            val extraInfo = location.extras
            var sourceType = 0
            if (extraInfo != null && !extraInfo.isEmpty && extraInfo.containsKey("SourceType")) {
                sourceType = extraInfo.getInt("SourceType", -1)
                hdbBinary = getBinaryFlag(sourceType)
            }
            val hDSecurityType = location.extras.getInt("HDSecurityType", -1)
            val hDEncryptType = location.extras.getInt("HDEncryptType", -1)
            if (hdbBinary) {
                hdFlag = "result is HD"
                if (hDEncryptType == 1) {
                    val key =
                        "XXXXXXXXXXXXXXX" // 解密算法SM4ECB,具体解密密钥请联系商务人员XXX,接入流程请参考相关文档
                    val sLatitude = location.extras.getString("HDEncryptLat")
                    val sLongitude = location.extras.getString("HDEncryptLng")
                    val mLatitude: String? = sLatitude?.let { myDecrypt(it, key) }
                    val mLongitude: String? = sLongitude?.let { myDecrypt(it, key) }
                    try {
                        location.latitude = mLatitude!!.toDouble()
                        location.longitude = mLongitude!!.toDouble()
                    } catch (e: java.lang.Exception) {
                        LocationLog.i(
                            TAG,
                            "failed to convert the data type."
                        )
                    }
                }
            }
            if (hDSecurityType == 0) {
                hdSecurity = "non-biased, non-encrypted, high-precision WGS84"
            }
            if (hDSecurityType == 1) {
                hdSecurity = "high precision with biased encryption GCJ02"
            }
            LocationLog.i(
                TAG,
                """
                    [old]location result : 
                    Longitude = ${location.longitude}
                    Latitude = ${location.latitude}
                    Accuracy = ${location.accuracy}
                    SourceType = $sourceType
                    $hdFlag
                    $hdSecurity
                    """.trimIndent()
            )
        }
    }

    private fun myDecrypt(Latitude: String, key: String): String {
        // 具体解密方法请自行查询，这里暂不提供
        return "XXXXXXXXXXX"
    }

    private fun getBinaryFlag(sourceType: Int): Boolean {
        var flag = false
        if (sourceType <= 0) {
            return false
        }
        val binary = Integer.toBinaryString(sourceType)
        if (binary.length >= 4) {
            val isbinary = binary.substring(binary.length - 4)[0].toString() + ""
            flag = isbinary == "1"
        }
        return flag
    }
}