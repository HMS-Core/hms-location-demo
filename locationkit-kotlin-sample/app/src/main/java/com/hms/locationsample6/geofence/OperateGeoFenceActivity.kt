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

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import com.hms.locationsample6.R
import com.hms.locationsample6.activity.BaseActivity
import com.hms.locationsample6.logger.LocationLog
import com.hms.locationsample6.utils.GeoFenceData
import com.huawei.hms.location.Geofence
import com.huawei.hms.location.GeofenceRequest
import com.huawei.hms.location.GeofenceService
import com.huawei.hms.location.LocationServices
import kotlinx.android.synthetic.main.activity_operate_geo_fence.*
import java.util.*

class OperateGeoFenceActivity : BaseActivity(), View.OnClickListener {
    companion object {
        private const val TAG = "operateGeoFenceActivity"
    }

    private lateinit var geoFenceService: GeofenceService
    private var requestList = mutableListOf<RequestList>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_operate_geo_fence)
        getGeoFenceData.setOnClickListener(this)
        createGeofence.setOnClickListener(this)
        sendRequest.setOnClickListener(this)
        sendRequestWithNew.setOnClickListener(this)
        getRequestMessage.setOnClickListener(this)
        removeGeofence.setOnClickListener(this)
        removeWithID.setOnClickListener(this)
        removeWithIntent.setOnClickListener(this)
        geoFenceService = LocationServices.getGeofenceService(this)
        addLogFragment()
    }

    override fun onClick(v: View) {
        try {
            when (v.id) {
                R.id.createGeofence -> {
                    startActivity(Intent().apply {
                        setClass(this@OperateGeoFenceActivity, GeoFenceActivity::class.java)
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    })
                }
                R.id.removeGeofence -> GeoFenceData.createNewList()
                R.id.getGeoFenceData -> getData()
                R.id.sendRequest -> requestGeoFenceWithIntent()
                R.id.sendRequestWithNew -> requestGeoFenceWithNewIntent()
                R.id.getRequestMessage -> getRequestMessage()
                R.id.removeWithIntent -> removeWithIntent()
                R.id.removeWithID -> removeWithID()
            }
        } catch (e: Exception) {
            LocationLog.e(TAG, "operateGeoFenceActivity Exception:$e")
        }
    }

    private fun requestGeoFenceWithNewIntent() {
        if (GeoFenceData.returnList().isEmpty()) {
            geoRequestData.text = getString(R.string.no_new_request_add)
            return
        }
        if (checkUniqueID()) {
            geoRequestData.text = getString(R.string.id_already_exit)
            return
        }
        val geoFenceRequest = GeofenceRequest.Builder()
        geoFenceRequest.createGeofenceList(GeoFenceData.returnList())
        if (trigger?.text != null) {
            val triggerVal: Int = trigger?.text.toString().toInt()
            geoFenceRequest.setInitConversions(triggerVal)
            LocationLog.d(TAG, "trigger is $triggerVal")
        } else {
            geoFenceRequest.setInitConversions(5)
            LocationLog.d(TAG, "default trigger is 5")
        }
        val pendingIntent = getPendingIntent()
        setList(pendingIntent, GeoFenceData.requestCode(), GeoFenceData.returnList())
        try {
            geoFenceService.createGeofenceList(geoFenceRequest.build(), pendingIntent)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        LocationLog.i(TAG, "add geoFence success！")
                    } else {
                        // Get the status code for the error and log it using a user-friendly message.
                        LocationLog.w(
                            TAG,
                            "add geoFence failed : ${task.exception.message}"
                        )
                    }
                }
        } catch (e: Exception) {
            LocationLog.i(TAG, "add geofence error：${e.message}")
        }
        GeoFenceData.createNewList()
    }

    private fun findIntentByID(a: Int): PendingIntent? {
        var intent: PendingIntent? = null
        for (i in requestList.indices.reversed()) {
            if (requestList[i].requestCode == a) {
                intent = requestList[i].intent
                requestList.removeAt(i)
            }
        }
        return intent
    }

    private fun removeWithIntent() {
        val removeWithIntent = removeWithPendingIntentInput!!.text.toString().toInt()
        val intent = findIntentByID(removeWithIntent)
        if (intent == null) {
            geoRequestData.text = getString(R.string.no_such_intent)
            return
        }
        try {
            geoFenceService.deleteGeofenceList(intent).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    LocationLog.i(TAG, "delete geoFence with intent success！")
                } else {
                    // Get the status code for the error and log it using a user-friendly message.
                    LocationLog.w(TAG, "delete geoFence with intent failed ")
                }
            }
        } catch (e: Exception) {
            LocationLog.i(TAG, "delete geoFence error：${e.message}")
        }
    }

    private fun removeWithID() {
        val removeWithID: String = removeWithIDInput?.text.toString()
        val str = removeWithID.split(" ").toTypedArray()
        val list: MutableList<String> = mutableListOf()
        val geofenceList: MutableList<String> = mutableListOf()
        Collections.addAll(list, *str)
        for (i in requestList.indices) {
            for (j in requestList[i].geoFences.indices) {
                geofenceList.add(requestList[i].geoFences[j].uniqueId)
            }
        }
        for (i in list.indices) {
            if (!geofenceList.contains(list[i])) {
                LocationLog.i(TAG, "delete ID not found.")
                return
            }
        }
        try {
            geoFenceService.deleteGeofenceList(list).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    LocationLog.i(TAG, "delete geoFence with ID success！")
                } else {
                    // Get the status code for the error and log it using a user-friendly message.
                    LocationLog.w(TAG, "delete geoFence with ID failed ")
                }
            }
        } catch (e: Exception) {
            LocationLog.i(TAG, "delete ID error：${e.message}")
        }
        listRemoveID(str)
    }

    private fun listRemoveID(str: Array<String>) {
        for (i in requestList.indices) {
            requestList[i].removeID(str)
        }
    }

    private fun requestGeoFenceWithIntent() {
        if (GeoFenceData.returnList().isEmpty()) {
            geoRequestData.text = getString(R.string.no_new_request_add)
            return
        }
        if (requestList.isEmpty()) {
            geoRequestData.text = getString(R.string.no_pending_intent_send)
            return
        }
        if (checkUniqueID()) {
            geoRequestData.text = getString(R.string.id_already_exit)
            return
        }
        checkUniqueID()
        val geofenceRequest = GeofenceRequest.Builder()
        geofenceRequest.createGeofenceList(GeoFenceData.returnList())
        if (trigger?.text != null) {
            val triggerVal: Int = trigger?.text.toString().toInt()
            geofenceRequest.setInitConversions(triggerVal)
            LocationLog.d(TAG, "trigger is $triggerVal")
        } else {
            geofenceRequest.setInitConversions(5)
            LocationLog.d(TAG, "default trigger is 5")
        }
        val temp: RequestList = requestList[requestList.size - 1]
        val pendingIntent: PendingIntent = temp.intent!!
        setList(pendingIntent, temp.requestCode, GeoFenceData.returnList())
        geoFenceService.createGeofenceList(geofenceRequest.build(), pendingIntent)
        GeoFenceData.createNewList()
    }

    private fun checkUniqueID(): Boolean {
        for (i in requestList.indices) {
            if (requestList[i].checkID()) return true
        }
        return false
    }

    private fun getData() {
        val geoFences: MutableList<Geofence> = GeoFenceData.returnList()
        var geoFenceDataString = ""
        if (geoFences.isEmpty()) {
            geoFenceDataString = "no GeoFence Data!"
        }
        for (i in geoFences.indices) {
            geoFenceDataString += """Unique ID is ${geoFences[i].uniqueId}""".trimIndent()
        }
        geoFenceData.text = geoFenceDataString
    }

    private fun setList(intent: PendingIntent?, code: Int, geoFences: MutableList<Geofence>) {
        requestList.add(RequestList(intent, code, geoFences))
    }

    private fun getRequestMessage() {
        var requestMessage = ""
        for (i in requestList.indices) {
            requestMessage += requestList[i].show()
        }
        if (requestMessage == "") requestMessage = "no request!"
        geoRequestData.text = requestMessage
    }

    @SuppressLint("WrongConstant")
    private fun getPendingIntent(): PendingIntent {
        val intent = Intent(this, GeoFenceBroadcastReceiver::class.java)
        intent.action = GeoFenceBroadcastReceiver.ACTION_PROCESS_LOCATION
        Log.d(TAG, "new request")
        GeoFenceData.newRequest()
        return if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
            PendingIntent.getBroadcast(
                this,
                GeoFenceData.requestCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        } else {
            // For Android 12 or later devices, proactively configure the pendingIntent variability.
            // The default value is PendingIntent.FLAG_MUTABLE. If compileSDKVersion is 30 or less, set this parameter
            // to 1<<25.
            PendingIntent.getBroadcast(
                this,
                GeoFenceData.requestCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or (1 shl 25)
            )
        }
    }

    class RequestList(intent: PendingIntent?, requestCode: Int, geoFences: MutableList<Geofence>) {
        var intent: PendingIntent? = null
        var requestCode = 0
        var geoFences: MutableList<Geofence>

        init {
            this.intent = intent
            this.requestCode = requestCode
            this.geoFences = geoFences
        }

        fun show(): String {
            var requestData = ""
            for (i in geoFences.indices) {
                requestData += "PendingIntent: $requestCode UniqueID: ${geoFences[i].uniqueId} \n"
            }
            return requestData
        }

        fun checkID(): Boolean {
            val list: MutableList<Geofence> = GeoFenceData.returnList()
            for (j in list.indices) {
                val uniqueID = list[j].uniqueId
                for (i in geoFences.indices) {
                    if (uniqueID == geoFences[i].uniqueId) {
                        return true
                        //id already exist
                    }
                }
            }
            return false
        }

        fun removeID(str: Array<String>) {
            for (i in str.indices) {
                val removeID = str[i]
                for (j in geoFences.indices.reversed()) {
                    if (removeID == geoFences[j].uniqueId) {
                        geoFences.removeAt(j)
                    }
                }
            }
        }
    }
}