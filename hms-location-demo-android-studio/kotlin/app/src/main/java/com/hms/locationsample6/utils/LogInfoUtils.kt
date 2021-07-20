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

package com.hms.locationsample6.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.text.TextUtils
import android.util.Log
import java.math.BigInteger


object LogInfoUtils {
    private val TAG: String = "LogInfoUtils"

    private lateinit var loactionManager: LocationManager

    private lateinit var wifiManager: WifiManager

    private val wifiScanReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            var success = true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                success = intent!!.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)
            }
            if (success) {
                val scanResults: List<ScanResult> =
                    wifiManager.getScanResults()
                if (scanResults == null || scanResults.isEmpty()) {
                    Log.e(
                        TAG + "-WIFI",
                        "wifi scan result null"
                    )
                    return
                }
                val info: WifiInfo = wifiManager.getConnectionInfo()
                val currentWifiInfo = StringBuilder()
                if (info != null && !TextUtils.isEmpty(info.bssid)) {
                    var mac: Long = 0
                    if (!TextUtils.isEmpty(info.bssid)) {
                        mac = BigInteger(info.bssid.replace(":", ""), 16).toString()
                            .toLong()
                    }
                    currentWifiInfo.append("[Current Connect wifi: ")
                        .append(info.toString())
                        .append(", BSSID: ")
                        .append(info.bssid)
                        .append(", MAC: ")
                        .append(mac)
                        .append(".]")
                        .append("\n")
                } else {
                    currentWifiInfo.append("Current Connect wifi is null. ").append("\n")
                }
                Log.i(
                    TAG + "-WIFI",
                    currentWifiInfo.toString()
                )
                for (result in scanResults) {
                    var mac: Long = 0
                    if (!TextUtils.isEmpty(result.BSSID)) {
                        mac = BigInteger(result.BSSID.replace(":", ""), 16).toString()
                            .toLong()
                    }
                    Log.i(
                        TAG + "-WIFI",
                        "[$result, MAC: $mac]\n"
                    )
                }
            }
        }

    }
}