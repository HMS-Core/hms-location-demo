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

package com.huawei.locationsample6.location.fusedlocation;

import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ResolvableApiException;
import com.huawei.hms.location.FusedLocationProviderClient;
import com.huawei.hms.location.LocationServices;
import com.huawei.locationsample6.R;
import com.huawei.logger.LocationLog;

/**
 * getLastLocation Example
 *
 * @since 2020-5-11
 */
public class GetLastLocationActivity extends LocationBaseActivity implements OnClickListener {
    public static final String TAG = "HuaweiPushActivity";

    private FusedLocationProviderClient mFusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_get_last_location);
        // Button click listeners
        findViewById(R.id.location_getLastLocation).setOnClickListener(this);
        addLogFragment();
        // Creating a Location Service Client
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getBaseContext());
    }

    /**
     * Obtain the last known location
     */
    private void getLastLocation() {
        try {
            Task<Location> lastLocation = mFusedLocationProviderClient.getLastLocation();
            lastLocation.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location == null) {
                        LocationLog.i(TAG, "getLastLocation onSuccess location is null");
                        return;
                    }
                    LocationLog.i(TAG, "getLastLocation onSuccess location[Longitude,Latitude]:"
                        + location.getLongitude() + "," + location.getLatitude());
                    return;
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    if (e instanceof ResolvableApiException) {
                        ResolvableApiException apiException = (ResolvableApiException) e;
                        LocationLog.e(TAG, "getLastLocation onFailure:" + apiException.getStatusCode());
                        try {
                            apiException.startResolutionForResult(GetLastLocationActivity.this, 2009);
                        } catch (IntentSender.SendIntentException ex) {
                            ex.printStackTrace();
                        }
                    } else {
                        LocationLog.e(TAG, "getLastLocation onFailure:" + e.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            LocationLog.e(TAG, "getLastLocation exception:" + e.getMessage());
        }
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.location_getLastLocation:
                    getLastLocation();
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            Log.e(TAG, "GetLastLocationActivity Exception:" + e);
        }
    }
}
