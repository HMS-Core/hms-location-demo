/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.huawei.locationsample6.location.fusedlocation;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.location.FusedLocationProviderClient;
import com.huawei.hms.location.LocationServices;
import com.huawei.locationsample6.R;
import com.huawei.logger.LocationLog;

/**
 * setMockMode Usage Example
 * Enable the mobile phone to allow mock location.
 *
 * @since 2020-5-11
 */
public class SetMockModeActivity extends LocationBaseActivity
    implements OnClickListener, RadioGroup.OnCheckedChangeListener {
    public static final String TAG = "SetMockModeActivity";

    // the mockMode flag
    private boolean mMockFlag;

    private RadioGroup mRadioGroupSetMockMode;

    private FusedLocationProviderClient mFusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_set_mock_mode);
        // Button click listeners
        findViewById(R.id.location_setMockMode).setOnClickListener(this);
        mRadioGroupSetMockMode = findViewById(R.id.radioGroup_mockMode);
        mRadioGroupSetMockMode.setOnCheckedChangeListener(this);
        addLogFragment();
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    /**
     * Setting the mock Mode
     */
    private void setMockMode() {
        try {
            Log.i(TAG, "setMockMode mock mode is " + mMockFlag);
            // Note: To enable the mock function, enable the android.permission.ACCESS_MOCK_LOCATION permission in the
            // AndroidManifest.xml file,
            // and set the application to the mock location app in the device setting.
            Task<Void> voidTask = mFusedLocationProviderClient.setMockMode(mMockFlag);
            voidTask.addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    LocationLog.i(TAG, "setMockMode onSuccess");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    LocationLog.e(TAG, "setMockMode onFailure:" + e.getMessage());
                }
            });
        } catch (Exception e) {
            LocationLog.e(TAG, "setMockMode exception:" + e.getMessage());
        }
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.location_setMockMode:
                    setMockMode();
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            Log.e(TAG, "setMockMode Exception:" + e);
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        // If you do not need to simulate a location, set mode to false. Otherwise, other applications cannot use the
        // positioning function of Huawei location service.
        RadioButton radioButton = group.findViewById(checkedId);
        mMockFlag = Boolean.valueOf(radioButton.getText().toString());
    }
}
