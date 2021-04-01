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

package com.huawei.hmssample2;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hms.location.LocationEnhanceService;
import com.huawei.hms.location.LocationServices;
import com.huawei.hms.location.NavigationRequest;
import com.huawei.hms.location.NavigationResult;
import com.huawei.hmssample2.location.fusedlocation.LocationBaseActivity;
import com.huawei.logger.LocationLog;

public class NavigationContextStateActivity extends LocationBaseActivity implements View.OnClickListener {

    public static final String TAG = "GetNavigationContextStateActivity";

    private LocationEnhanceService locationEnhanceService;

    private EditText requestType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_context_state);
        findViewById(R.id.getNavigationContextState).setOnClickListener(this);
        requestType = findViewById(R.id.typeText);
        addLogFragment();
        locationEnhanceService = LocationServices.getLocationEnhanceService(this);
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.getNavigationContextState:
                    getNavigationContextState();
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            Log.i(TAG, "getNavigationContextState Exception:" + e);
        }
    }

    private void getNavigationContextState() {
        try {
            int type = Integer.parseInt(requestType.getText().toString());
            NavigationRequest request = new NavigationRequest(type);
            locationEnhanceService.getNavigationState(request)
                .addOnSuccessListener(new OnSuccessListener<NavigationResult>() {
                    @Override
                    public void onSuccess(NavigationResult result) {
                        LocationLog.i(TAG, "get NavigationResult sucess, State is :" + result.getState()
                            + " , Possibility is : " + result.getPossibility());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        LocationLog.i(TAG, "get NavigationResult fail " + e.getMessage());
                    }
                });
            LocationLog.i(TAG, "getNavigationContextStatecall finish");
        } catch (Exception e) {
            LocationLog.i(TAG, "getNavigationContextState exception:" + e.getMessage());
        }
    }
}