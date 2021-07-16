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

package com.hms.locationkit.useractivity

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.hms.locationkit.R
import com.hms.locationkit.activity.BaseActivity
import com.hms.locationkit.fusedlocation.LocationBroadcastReceiver
import com.hms.locationkit.logger.LocationLog
import com.hms.locationkit.utils.RequestPermission.requestActivityTransitionPermission
import com.hms.locationkit.utils.Utils
import com.hms.locationkit.utils.Utils.ACTION_PROCESS_LOCATION
import com.huawei.hms.location.ActivityConversionInfo
import com.huawei.hms.location.ActivityConversionRequest
import com.huawei.hms.location.ActivityIdentification
import com.huawei.hms.location.ActivityIdentificationService
import kotlinx.android.synthetic.main.activity_transition.*

class ActivityConversionActivity : BaseActivity(), View.OnClickListener {
    companion object {
        private var TAG = "ActivityTransitionConvert"
    }

    private lateinit var activityIdentificationService: ActivityIdentificationService

    private lateinit var activityTransitionRequest: ActivityConversionRequest

    private lateinit var transitions: MutableList<ActivityConversionInfo>
    private var pendingIntent: PendingIntent? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transition)
        activityIdentificationService = ActivityIdentification.getService(this)
        requestActivityTransitionPermission(this)
        btnSubmit.setOnClickListener(this)
        btnMove.setOnClickListener(this)
        addLogFragment()
    }

    private fun getRequest() {
        transitions = mutableListOf()
        val activityTransition = ActivityConversionInfo.Builder()
        val requestValueResult = RequestValueResult()
        if (inVehicleIn.isChecked) requestValueResult.addList(100, 0)
        if (inVehicleOut.isChecked) requestValueResult.addList(100, 1)
        if (onBicycleIn.isChecked) requestValueResult.addList(101, 0)
        if (onBicycleOut.isChecked) requestValueResult.addList(101, 1)
        if (onFootIn.isChecked) requestValueResult.addList(102, 0)
        if (onFootOut.isChecked) requestValueResult.addList(102, 1)
        if (stillIn.isChecked) requestValueResult.addList(103, 0)
        if (stillOut.isChecked) requestValueResult.addList(103, 1)
        if (walkingIn.isChecked) requestValueResult.addList(107, 0)
        if (walkingOut.isChecked) requestValueResult.addList(107, 1)
        if (runningIn.isChecked) requestValueResult.addList(108, 0)
        if (runningOut.isChecked) requestValueResult.addList(108, 1)
        val result = requestValueResult.result
        for (indi in result.indices) {
            val temp = result[indi]
            activityTransition.apply {
                setActivityType(temp.ActivityType)
                setConversionType(temp.ActivityTransition)
            }
            transitions.add(activityTransition.build())
        }
        Log.d(TAG, "transitions size is ${transitions.size}")
    }

    private fun requestActivityTransitionUpdate() {
        try {
            pendingIntent?.let {
                removeActivityTransitionUpdates()
            }
            Utils.addConversionListener()
            pendingIntent = getPendingIntent()
            activityTransitionRequest = ActivityConversionRequest(transitions)
            val task = activityIdentificationService.createActivityConversionUpdates(
                activityTransitionRequest,
                pendingIntent
            )
            task.addOnSuccessListener {
                LocationLog.i(
                    TAG,
                    "createActivityConversionUpdates onSuccess"
                )
            }
                .addOnFailureListener { e ->
                    LocationLog.e(
                        TAG,
                        "createActivityConversionUpdates onFailure:${e.message}"
                    )
                }
        } catch (e: Exception) {
            LocationLog.e(TAG, "createActivityConversionUpdates exception:${e.message}")
        }
    }

    private fun removeActivityTransitionUpdates() {
        try {
            Utils.removeConversionListener()
            activityIdentificationService.deleteActivityConversionUpdates(pendingIntent)
                .addOnSuccessListener {
                    LocationLog.i(
                        TAG,
                        "deleteActivityConversionUpdates onSuccess"
                    )
                }
                .addOnFailureListener { e ->
                    LocationLog.e(TAG, "deleteActivityConversionUpdates onFailure:${e.message}")
                }
        } catch (e: Exception) {
            LocationLog.e(TAG, "removeActivityTransitionUpdates exception:${e.message}")
        }
    }

    override fun onDestroy() {
        pendingIntent?.let {
            removeActivityTransitionUpdates()
        }
        super.onDestroy()
    }

    private fun getPendingIntent(): PendingIntent? {
        val intent = Intent(this, LocationBroadcastReceiver::class.java)
        intent.action = ACTION_PROCESS_LOCATION
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    override fun onClick(v: View) {
        try {
            when (v.id) {
                R.id.btnSubmit -> {
                    getRequest()
                    requestActivityTransitionUpdate()
                }
                R.id.btnMove -> removeActivityTransitionUpdates()

            }
        } catch (e: Exception) {
            LocationLog.e(TAG, "RequestLocationUpdatesWithCallbackActivity Exception:$e")
        }
    }

    internal class RequestValue(var ActivityType: Int, var ActivityTransition: Int)

    internal class RequestValueResult {
        var result = mutableListOf<RequestValue>()
        fun addList(activityType: Int, activityTransition: Int) {
            val temp = RequestValue(activityType, activityTransition)
            result.add(temp)
        }
    }
}