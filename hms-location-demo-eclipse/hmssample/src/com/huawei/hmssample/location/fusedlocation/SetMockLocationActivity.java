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

package com.huawei.hmssample.location.fusedlocation;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.location.FusedLocationProviderClient;
import com.huawei.hms.location.LocationServices;
import com.huawei.hmssample.R;
import com.huawei.logger.LocationLog;

import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * setMockLocation Usage Example
 * Set the specific analog position. You must call setMockMode(boolean) before calling this method and set it to true.
 * Enable the mobile phone to allow mock location.
 * 
 * @since 2020-5-11
 */
public class SetMockLocationActivity extends LocationBaseActivity implements OnClickListener {
    public static final String TAG = "SetMockLocationActivity";

    private FusedLocationProviderClient mFusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_set_mock_location);
        // Button click listeners
        findViewById(R.id.location_setMockLocation).setOnClickListener(this);
        addLogFragment();
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    /**
     * Set the specific mock location.
     */
    private void setMockLocation() {
        try {
            // Fill in the information sources such as gps and network based on the application situation.
            final Location mockLocation = new Location(LocationManager.GPS_PROVIDER);
            mockLocation.setLongitude(118.76);
            mockLocation.setLatitude(31.98);
            // Note: To enable the mock function, enable the android.permission.ACCESS_MOCK_LOCATION permission in the
            // AndroidManifest.xml file,
            // and set the application to the mock location app in the device setting.
            Task<Void> voidTask = mFusedLocationProviderClient.setMockLocation(mockLocation);
            voidTask.addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    LocationLog.i(TAG, "setMockLocation onSuccess");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    LocationLog.e(TAG, "setMockLocation onFailure:" + e.getMessage());
                }
            });
        } catch (Exception e) {
            LocationLog.e(TAG, "setMockLocation exception:" + e.getMessage());
        }
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.location_setMockLocation:
                    setMockLocation();
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            Log.e(TAG, "setMockLocation Exception:" + e);
        }
    }
}
