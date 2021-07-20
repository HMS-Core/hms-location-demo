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
import android.app.Activity
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import com.hms.locationsample6.R
import com.hms.locationsample6.activity.BaseActivity
import com.hms.locationsample6.logger.LocationLog
import com.hms.locationsample6.utils.JsonDataUtil
import com.huawei.hmf.tasks.OnCompleteListener
import com.huawei.hmf.tasks.OnFailureListener
import com.huawei.hms.common.ApiException
import com.huawei.hms.common.ResolvableApiException
import com.huawei.hms.location.*
import kotlinx.android.synthetic.main.activity_check_settings.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CheckSettingsActivity : BaseActivity(), View.OnClickListener {
    companion object {
        private const val TAG = "CheckSettingsActivity"
    }

    private lateinit var settingsClient: SettingsClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_settings)
        checkLocationSetting.setOnClickListener(this@CheckSettingsActivity)
        addLogFragment()
        settingsClient = LocationServices.getSettingsClient(this@CheckSettingsActivity)
        initDataDisplayView(
            check_setting_table_layout_show,
            JsonDataUtil.getJson(this@CheckSettingsActivity, "CheckLocationSettings.json", true)
        )
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val strings = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            ActivityCompat.requestPermissions(this, strings, 1)
            return
        }
    }

    override fun onClick(v: View?) {
        checkSettings()
    }

    private fun checkSettings() {
        GlobalScope.launch {
            try {
                val settingJson = JsonDataUtil.getJson(baseContext, "CheckLocationSettings.json", true)
                val checkSettingsRequest = CheckSettingsRequest()
                handleData(checkSettingsRequest, settingJson)
                val builder = LocationSettingsRequest.Builder()
                    .addLocationRequest(checkSettingsRequest.locationRequest)
                    .setAlwaysShow(checkSettingsRequest.isAlwaysShow)
                    .setNeedBle(checkSettingsRequest.isNeedBle)
                settingsClient.checkLocationSettings(builder.build())
                    .addOnCompleteListener(OnCompleteListener { task ->
                        if (task != null && task.isSuccessful) {
                            val response = task.result ?: return@OnCompleteListener
                            val locationSettingsStates = response.locationSettingsStates
                            val stringBuilder = StringBuilder()
                            stringBuilder.append("\nisBlePresent=")
                                .append(locationSettingsStates.isBlePresent)
                            stringBuilder.append(",\nisBleUsable=")
                                .append(locationSettingsStates.isBleUsable)
                            stringBuilder.append(",\nisGNSSPresent=")
                                .append(locationSettingsStates.isGnssPresent)
                            stringBuilder.append(",\nisGNSSUsable=")
                                .append(locationSettingsStates.isGnssUsable)
                            stringBuilder.append(",\nisLocationPresent=")
                                .append(locationSettingsStates.isLocationPresent)
                            stringBuilder.append(",\nisLocationUsable=")
                                .append(locationSettingsStates.isLocationUsable)
                            stringBuilder.append(",\nisNetworkLocationUsable=")
                                .append(locationSettingsStates.isNetworkLocationUsable)
                            stringBuilder.append(",\nisNetworkLocationPresent=")
                                .append(locationSettingsStates.isNetworkLocationPresent)
                            stringBuilder.append(",\nisHMSLocationUsable=")
                                .append(locationSettingsStates.isHMSLocationUsable)
                            stringBuilder.append(",\nisHMSLocationPresent=")
                                .append(locationSettingsStates.isHMSLocationPresent)
                            LocationLog.i(TAG, "checkLocationSetting onComplete:$stringBuilder")
                        }
                    })
                    .addOnFailureListener(OnFailureListener { e ->
                        LocationLog.i(TAG, "checkLocationSetting onFailure:" + e.message)
                        val statusCode = (e as ApiException).statusCode
                        when (statusCode) {
                            LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                                Log.i(
                                    TAG,
                                    "Location settings are not satisfied. Attempting to upgrade " + "location settings "
                                )
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    val rae = e as ResolvableApiException
                                    rae.startResolutionForResult(this@CheckSettingsActivity, 0)
                                } catch (sie: SendIntentException) {
                                    Log.i(TAG, "PendingIntent unable to execute request.")
                                }
                            }
                        }
                    })
            } catch (e: Exception) {
                LocationLog.i(TAG, "checkLocationSetting exception:" + e.message)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data == null)
            return
        val locationSettingsStates = LocationSettingsStates.fromIntent(data)
        val stringBuilder = java.lang.StringBuilder()
        stringBuilder.append("\nisBlePresent=")
            .append(locationSettingsStates.isBlePresent)
        stringBuilder.append(",\nisBleUsable=")
            .append(locationSettingsStates.isBleUsable)
        stringBuilder.append(",\nisGNSSPresent=")
            .append(locationSettingsStates.isGnssPresent)
        stringBuilder.append(",\nisGNSSUsable=")
            .append(locationSettingsStates.isGnssUsable)
        stringBuilder.append(",\nisLocationPresent=")
            .append(locationSettingsStates.isLocationPresent)
        stringBuilder.append(",\nisLocationUsable=")
            .append(locationSettingsStates.isLocationUsable)
        stringBuilder.append(",\nisNetworkLocationPresent=")
            .append(locationSettingsStates.isNetworkLocationPresent)
        stringBuilder.append(",\nisNetworkLocationUsable=")
            .append(locationSettingsStates.isNetworkLocationUsable)
        stringBuilder.append(",\nisHMSLocationUsable=")
            .append(locationSettingsStates.isHMSLocationUsable)
        stringBuilder.append(",\nisHMSLocationPresent=")
            .append(locationSettingsStates.isHMSLocationPresent)
        LocationLog.i(TAG, "checkLocationSetting onComplete:$stringBuilder")
        when (requestCode) {
            0 -> when (resultCode) {
                Activity.RESULT_OK -> LocationLog.i(
                    TAG,
                    "User agreed to make required location settings changes."
                )
                Activity.RESULT_CANCELED -> LocationLog.i(
                    TAG,
                    "User chose not to make required location settings changes."
                )
            }
        }
    }

    private class CheckSettingsRequest {
        var locationRequest: LocationRequest = LocationRequest()
        var isAlwaysShow = false
        var isNeedBle = false

    }

}