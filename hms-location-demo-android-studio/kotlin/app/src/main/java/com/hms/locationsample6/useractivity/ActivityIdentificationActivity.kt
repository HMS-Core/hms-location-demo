/*
*       Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.
        Licensed under the Apache License, Version 2.0 (the "License");
        you may not use this file except in compliance with the License.
        You may obtain a copy of the License at
        http://www.apache.org/licenses/LICENSE-2.0
        Unless required by applicable law or agreed to in writing, software
        distributed under the License is distributed on an "AS IS" BASIS,
        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        See the License for the specific language governing permissions and
        limitations under the License.
*/

package com.hms.locationsample6.useractivity

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import com.hms.locationsample6.R
import com.hms.locationsample6.activity.BaseActivity
import com.hms.locationsample6.fusedlocation.LocationBroadcastReceiver
import com.hms.locationsample6.logger.LocationLog.Companion.e
import com.hms.locationsample6.logger.LocationLog.Companion.i
import com.hms.locationsample6.utils.RequestPermission.requestActivityTransitionPermission
import com.hms.locationsample6.utils.Utils.ACTION_PROCESS_LOCATION
import com.hms.locationsample6.utils.Utils.addIdentificationListener
import com.hms.locationsample6.utils.Utils.removeIdentificationListener
import com.huawei.hms.location.ActivityIdentification
import com.huawei.hms.location.ActivityIdentificationData
import com.huawei.hms.location.ActivityIdentificationService
import kotlinx.android.synthetic.main.activity_transition_type.*

class ActivityIdentificationActivity : BaseActivity(),
    View.OnClickListener {
    val tag = "IdentificationActivity"
    private var activityIdentificationService: ActivityIdentificationService? = null
    private var pendingIntent: PendingIntent? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transition_type)
        activityIdentificationService = ActivityIdentification.getService(this)
        requestActivityTransitionPermission(this)
        requestActivityTransitionUpdate.setOnClickListener(this)
        removeActivityTransitionUpdate.setOnClickListener(this)
        activityIN_VEHICLE =
            activity_IN_VEHICLE as LinearLayout
        params_vehicle =
            activityIN_VEHICLE!!.layoutParams as LinearLayout.LayoutParams
        activityON_BICYCLE =
            activity_ON_BICYCLE as LinearLayout
        params_bicycle =
            activityON_BICYCLE!!.layoutParams as LinearLayout.LayoutParams
        activityON_FOOT =
            activity_ON_FOOT as LinearLayout
        params_foot =
            activityON_FOOT!!.layoutParams as LinearLayout.LayoutParams
        activitySTILL =
            activity_STILL as LinearLayout
        params_still =
            activitySTILL!!.layoutParams as LinearLayout.LayoutParams
        activityUNKNOWN =
            activity_UNKNOWN as LinearLayout
        params_unknown =
            activityUNKNOWN!!.layoutParams as LinearLayout.LayoutParams
        activityWALKING =
            activity_WALKING as LinearLayout
        params_walking =
            activityWALKING!!.layoutParams as LinearLayout.LayoutParams
        activityRunning =
            activity_Running as LinearLayout
        params_running =
            activityRunning!!.layoutParams as LinearLayout.LayoutParams
        addLogFragment()
        reSet()
    }

    private fun requestActivityUpdates(detectionIntervalMillis: Long) {
        try {
            if (pendingIntent != null) {
                removeActivityUpdates()
            }
            pendingIntent = getPendingIntent()
            addIdentificationListener()
            activityIdentificationService!!.createActivityIdentificationUpdates(
                detectionIntervalMillis,
                pendingIntent
            )
                .addOnSuccessListener {
                    i(
                        tag,
                        "createActivityIdentificationUpdates onSuccess"
                    )
                }
                .addOnFailureListener { e ->
                    e(
                        tag,
                        "createActivityIdentificationUpdates onFailure:" + e.message
                    )
                }
        } catch (e: Exception) {
            e(
                tag,
                "createActivityIdentificationUpdates exception:" + e.message
            )
        }
    }

    private fun removeActivityUpdates() {
        reSet()
        try {
            removeIdentificationListener()
            Log.i(tag, "start to removeActivityUpdates")
            activityIdentificationService!!.deleteActivityIdentificationUpdates(pendingIntent)
                .addOnSuccessListener {
                    i(
                        tag,
                        "deleteActivityIdentificationUpdates onSuccess"
                    )
                }
                .addOnFailureListener { e ->
                    e(
                        tag,
                        "deleteActivityIdentificationUpdates onFailure:" + e.message
                    )
                }
        } catch (e: Exception) {
            e(tag, "removeActivityUpdates exception:" + e.message)
        }
    }

    override fun onClick(v: View) {
        try {
            when (v.id) {
                R.id.requestActivityTransitionUpdate -> requestActivityUpdates(5000)
                R.id.removeActivityTransitionUpdate -> removeActivityUpdates()
                else -> {
                }
            }
        } catch (e: Exception) {
            e(
                tag,
                "RequestLocationUpdatesWithCallbackActivity Exception:$e"
            )
        }
    }

    private fun getPendingIntent(): PendingIntent {
        val intent = Intent(this, LocationBroadcastReceiver::class.java)
        intent.action = ACTION_PROCESS_LOCATION
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    override fun onDestroy() {
        if (pendingIntent != null) {
            removeActivityUpdates()
        }
        super.onDestroy()
    }

    companion object {
        private const val progressBarOriginWidth = 100
        private const val enlarge = 6
        private var params_vehicle: LinearLayout.LayoutParams? = null
        private var params_bicycle: LinearLayout.LayoutParams? = null
        private var params_foot: LinearLayout.LayoutParams? = null
        private var params_still: LinearLayout.LayoutParams? = null
        private var params_unknown: LinearLayout.LayoutParams? = null
        private var params_walking: LinearLayout.LayoutParams? = null
        private var params_running: LinearLayout.LayoutParams? = null
        private var activityIN_VEHICLE: LinearLayout? = null
        private var activityON_BICYCLE: LinearLayout? = null
        private var activityON_FOOT: LinearLayout? = null
        private var activitySTILL: LinearLayout? = null
        private var activityUNKNOWN: LinearLayout? = null
        private var activityWALKING: LinearLayout? = null
        private var activityRunning: LinearLayout? = null
        fun setData(list: List<ActivityIdentificationData>) {
            reSet()
            for (i in list.indices) {
                val type = list[i].identificationActivity
                val value = list[i].possibility
                try {
                    when (type) {
                        ActivityIdentificationData.VEHICLE -> {
                            params_vehicle!!.width =
                                params_vehicle!!.width + value * enlarge
                            activityIN_VEHICLE!!.layoutParams = params_vehicle
                        }
                        ActivityIdentificationData.BIKE -> {
                            params_bicycle!!.width =
                                params_bicycle!!.width + value * enlarge
                            activityON_BICYCLE!!.layoutParams = params_bicycle
                        }
                        ActivityIdentificationData.FOOT -> {
                            params_foot!!.width =
                                params_foot!!.width + value * enlarge
                            activityON_FOOT!!.layoutParams = params_foot
                        }
                        ActivityIdentificationData.STILL -> {
                            params_still!!.width =
                                params_still!!.width + value * enlarge
                            activitySTILL!!.layoutParams = params_still
                        }
                        ActivityIdentificationData.OTHERS -> {
                            params_unknown!!.width =
                                params_unknown!!.width + value * enlarge
                            activityUNKNOWN!!.layoutParams = params_unknown
                        }
                        ActivityIdentificationData.WALKING -> {
                            params_walking!!.width =
                                params_walking!!.width + value * enlarge
                            activityWALKING!!.layoutParams = params_walking
                        }
                        ActivityIdentificationData.RUNNING -> {
                            params_running!!.width =
                                params_running!!.width + value * enlarge
                            activityRunning!!.layoutParams = params_running
                        }
                        else -> {
                        }
                    }
                } catch (e: RuntimeException) {
                    throw e
                } catch (e: Exception) {
                    e("ActivityTransitionUpdate", "setdata Exception")
                }
            }
        }

        fun reSet() {
            params_vehicle!!.width =
                progressBarOriginWidth
            activityIN_VEHICLE!!.layoutParams = params_vehicle
            params_bicycle!!.width =
                progressBarOriginWidth
            activityON_BICYCLE!!.layoutParams = params_bicycle
            params_foot!!.width =
                progressBarOriginWidth
            activityON_FOOT!!.layoutParams = params_foot
            params_still!!.width =
                progressBarOriginWidth
            activitySTILL!!.layoutParams = params_still
            params_unknown!!.width =
                progressBarOriginWidth
            activityUNKNOWN!!.layoutParams = params_unknown
            params_walking!!.width =
                progressBarOriginWidth
            activityWALKING!!.layoutParams = params_walking
            params_running!!.width =
                progressBarOriginWidth
            activityRunning!!.layoutParams = params_running
        }
    }
}