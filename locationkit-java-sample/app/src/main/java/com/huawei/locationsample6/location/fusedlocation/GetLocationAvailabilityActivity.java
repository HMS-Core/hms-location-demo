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

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.location.FusedLocationProviderClient;
import com.huawei.hms.location.LocationAvailability;
import com.huawei.hms.location.LocationServices;
import com.huawei.locationsample6.R;
import com.huawei.logger.LocationLog;

/**
 * getLocationAvailability Example
 *
 * @since 2020-5-11
 */
public class GetLocationAvailabilityActivity extends LocationBaseActivity implements OnClickListener {
    public static final String TAG = "LocationAvailability";

    private FusedLocationProviderClient mFusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_get_location_availability);
        // Button click listeners
        findViewById(R.id.location_getLocationAvailability).setOnClickListener(this);
        addLogFragment();

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    /**
     * Obtaining Location Availability
     */
    private void getLocationAvailability() {
        try {
            Task<LocationAvailability> locationAvailability = mFusedLocationProviderClient.getLocationAvailability();
            locationAvailability.addOnSuccessListener(new OnSuccessListener<LocationAvailability>() {
                @Override
                public void onSuccess(LocationAvailability locationAvailability) {
                    if (locationAvailability != null) {
                        LocationLog.i(TAG, "getLocationAvailability onSuccess:" + locationAvailability.toString());
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    LocationLog.e(TAG, "getLocationAvailability onFailure:" + e.getMessage());
                }
            });
        } catch (Exception e) {
            LocationLog.e(TAG, "getLocationAvailability exception:" + e.getMessage());
        }
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.location_getLocationAvailability:
                    getLocationAvailability();
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            Log.e(TAG, "getLocationAvailability Exception:" + e);
        }
    }
}
