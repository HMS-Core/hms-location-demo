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

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import com.hms.locationsample6.R
import com.hms.locationsample6.activity.BaseActivity
import com.hms.locationsample6.logger.LocationLog
import com.hms.locationsample6.utils.RequestPermission.requestLocationPermission
import com.huawei.hms.location.FusedLocationProviderClient
import com.huawei.hms.location.LocationServices
import kotlinx.android.synthetic.main.activity_location_set_mock_mode.*
import java.lang.Boolean.valueOf

class SetMockModeActivity : BaseActivity(), View.OnClickListener,
    RadioGroup.OnCheckedChangeListener {
    companion object {
        private const val TAG = "SetMockModeActivity"
    }

    //the mockMode flag
    private var mMockFlag = false


    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_set_mock_mode)
        // Button click listeners
        location_setMockMode.setOnClickListener(this)
        radioGroup_mockMode.setOnCheckedChangeListener(this)
        addLogFragment()
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        requestLocationPermission(this)
    }

    /**
     * Setting the mock Mode
     */
    private fun setMockMode() {
        try {
            Log.i(TAG, "setMockMode mock mode is $mMockFlag")
            // Note: To enable the mock function, enable the android.permission.ACCESS_MOCK_LOCATION permission in the AndroidManifest.xml file,
            // and set the application to the mock location app in the device setting.
            val voidTask =
                mFusedLocationProviderClient.setMockMode(mMockFlag)
            voidTask.addOnSuccessListener { LocationLog.i(TAG, "setMockMode onSuccess") }
                .addOnFailureListener { e ->
                    LocationLog.e(TAG, "setMockMode onFailure:${e.message}")
                }
        } catch (e: Exception) {
            LocationLog.e(TAG, "setMockMode exception:${e.message}")
        }
    }

    override fun onClick(v: View) {
        try {
            when (v.id) {
                R.id.location_setMockMode -> setMockMode()

            }
        } catch (e: Exception) {
            Log.e(TAG, "setMockMode Exception:$e")
        }
    }

    override fun onCheckedChanged(group: RadioGroup, checkedId: Int) {
        //If you do not need to simulate a location, set mode to false. Otherwise, other applications cannot use the positioning function of Huawei location service.
        val radioButton: RadioButton = findViewById(checkedId)
        mMockFlag = valueOf(radioButton.text.toString())
    }


}