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

package com.hms.locationsample6.fusedlocation

import android.os.Bundle
import android.view.View
import android.widget.EditText
import com.hms.locationsample6.R
import com.hms.locationsample6.activity.BaseActivity
import com.hms.locationsample6.logger.LocationLog.Companion.i
import com.huawei.hms.location.LocationEnhanceService
import com.huawei.hms.location.LocationServices
import com.huawei.hms.location.NavigationRequest

class NavigationContextStateActivity : BaseActivity(),
    View.OnClickListener {
    private lateinit var locationEnhanceService: LocationEnhanceService
    private lateinit var requestType: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation_context_state)
        findViewById<View>(R.id.getNavigationContextState).setOnClickListener(this)
        requestType = findViewById(R.id.typeText)
        addLogFragment()
        locationEnhanceService = LocationServices.getLocationEnhanceService(this)
    }

    override fun onClick(v: View) {
        try {
            when (v.id) {
                R.id.getNavigationContextState -> navigationContextState
                else -> {
                }
            }
        } catch (e: Exception) {
            i(
                TAG,
                "getNavigationContextState Exception:$e"
            )
        }
    }

    private val navigationContextState: Unit
        private get() {
            try {
                val type = requestType.text.toString().toInt()
                val request = NavigationRequest(type)
                    locationEnhanceService.getNavigationState(request)
                        .addOnSuccessListener { result ->
                            i(
                                TAG,
                                "get NavigationResult sucess, State is :" + result.state + " , Possibility is : "
                                        + result.possibility
                            )
                        }
                        .addOnFailureListener { e ->
                            i(
                                TAG,
                                "get NavigationResult fail " + e.message
                            )
                        }
                i(
                    TAG,
                    "getNavigation ContextStatecall finish"
                )
            } catch (e: Exception) {
                i(
                    TAG,
                    "getNavigationContextState exception:" + e.message
                )
            }
        }

    companion object {
        const val TAG = "GetNavigationContextStateActivity"
    }
}