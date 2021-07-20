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
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import com.hms.locationsample6.R
import com.hms.locationsample6.activity.BaseActivity
import com.hms.locationsample6.logger.LocationLog
import com.hms.locationsample6.utils.PermissionsUtils.checkGrantResults
import com.hms.locationsample6.utils.Utils.ACTION_PROCESS_LOCATION
import com.huawei.hms.location.FusedLocationProviderClient
import com.huawei.hms.location.LocationRequest
import com.huawei.hms.location.LocationServices
import kotlinx.android.synthetic.main.activity_request_location_update_with_intent.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RequestLocationUpdateWithIntentActivity : BaseActivity(), View.OnClickListener {
    companion object {
        private const val TAG = "RequestLocationUpdateWithIntentActivity"
    }


    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_location_update_with_intent)
        location_requestLocationUpdatesWithIntent.setOnClickListener(this@RequestLocationUpdateWithIntentActivity)
        location_removeLocationUpdatesWithIntent.setOnClickListener(this@RequestLocationUpdateWithIntentActivity)
        addLogFragment()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
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
        when (v?.id) {
            R.id.location_requestLocationUpdatesWithIntent -> requestLocationUpdatesWithIntent()
            R.id.location_removeLocationUpdatesWithIntent -> removeLocatonUpdatesWithIntent()

        }
    }

    private fun requestLocationUpdatesWithIntent() {
        GlobalScope.launch {
            try {
                val locationRequest = LocationRequest().apply {
                    this.interval = 10000
                    this.needAddress = true
                    this.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                }
                fusedLocationProviderClient.requestLocationUpdates(
                    locationRequest,
                    getPendingIntent()
                )
                    .addOnSuccessListener {
                        LocationLog.i(TAG, "requestLocationUpdatesWithIntent onSuccess")
                    }
                    .addOnFailureListener { e ->
                        LocationLog.i(
                            TAG,
                            "requestLocationUpdatesWithIntent onFailure:" + e.message
                        )
                    }
            } catch (e: Exception) {
                LocationLog.e(TAG, "requestLocationUpdatesWithIntent exception:" + e.message)
            }
        }
    }

    private fun getPendingIntent(): PendingIntent? {
        val intent = Intent(
            this@RequestLocationUpdateWithIntentActivity,
            LocationBroadcastReceiver::class.java
        )
        intent.action = ACTION_PROCESS_LOCATION
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun removeLocatonUpdatesWithIntent() {
        GlobalScope.launch {
            try {
                fusedLocationProviderClient.removeLocationUpdates(getPendingIntent())
                    .addOnSuccessListener {
                        LocationLog.i(TAG, "removeLocatonUpdatesWithIntent onSuccess")
                    }
                    .addOnFailureListener { e ->
                        LocationLog.i(TAG, "removeLocatonUpdatesWithIntent onFailure:" + e.message)
                    }
                LocationLog.i(TAG, "removeLocatonUpdatesWithIntent call finish")
            } catch (e: java.lang.Exception) {
                LocationLog.e(TAG, "removeLocatonUpdatesWithIntent exception:" + e.message)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            val isGranted: Boolean = checkGrantResults(grantResults)
            if (isGranted) {
                LocationLog.i(
                    TAG,
                    "onRequestPermissionsResult: apply ACCESS_FINE_LOCATION and ACCESS_COARSE_LOCATION successful"
                )
            } else {
                LocationLog.i(TAG, "onRequestPermissionsResult: apply ACCESS_FINE_LOCATION failed")
            }
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                LocationLog.i(TAG, "onRequestPermissionsResult: apply permission failed")
            }
        } else if (requestCode == 2) {
            val isGranted: Boolean = checkGrantResults(grantResults)
            if (isGranted) {
                LocationLog.i(
                    TAG,
                    "onRequestPermissionsResult: apply ACCESS_MOCK_LOCATION successful"
                )
            } else {
                LocationLog.i(TAG, "onRequestPermissionsResult: apply ACCESS_MOCK_LOCATION failed")
            }
        } else {
            LocationLog.i(TAG, "onRequestPermissionsResult: failed")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        removeLocatonUpdatesWithIntent()
    }
}