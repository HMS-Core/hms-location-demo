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

package com.huawei.hmssample2.location.fusedlocation;

import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.common.ResolvableApiException;
import com.huawei.hms.location.FusedLocationProviderClient;
import com.huawei.hms.location.LocationAvailability;
import com.huawei.hms.location.LocationCallback;
import com.huawei.hms.location.LocationRequest;
import com.huawei.hms.location.LocationResult;
import com.huawei.hms.location.LocationServices;
import com.huawei.hms.location.LocationSettingsRequest;
import com.huawei.hms.location.LocationSettingsResponse;
import com.huawei.hms.location.LocationSettingsStatusCodes;
import com.huawei.hms.location.SettingsClient;
import com.huawei.hmssample2.LogInfoUtil;
import com.huawei.hmssample2.R;
import com.huawei.logger.LocationLog;

import java.util.List;

/**
 * Example of Using requestLocationUpdates and removeLocationUpdates.
 * Requests a location update and calls back on the specified Looper thread. This method requires that the requester
 * process exist for continuous callback.
 * If you still want to receive the callback after the process is killed, see requestLocationUpdates (LocationRequest
 * request,PendingIntent callbackIntent)
 *
 * @since 2020-5-11
 */
public class RequestLocationUpdatesWithCallbackActivity extends LocationBaseActivity implements OnClickListener {
    public static final String TAG = "LocationUpdatesCallback";

    LocationCallback mLocationCallback;

    LocationRequest mLocationRequest;

    private FusedLocationProviderClient mFusedLocationProviderClient;

    private SettingsClient mSettingsClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_request_location_updates_callback);
        // Button click listeners
        findViewById(R.id.location_requestLocationUpdatesWithCallback).setOnClickListener(this);
        findViewById(R.id.location_removeLocationUpdatesWithCallback).setOnClickListener(this);
        addLogFragment();
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);
        mLocationRequest = new LocationRequest();
        // Sets the interval for location update (unit: Millisecond)
        mLocationRequest.setInterval(5000);
        // Sets the priority
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (null == mLocationCallback) {
            mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult != null) {
                        List<Location> locations = locationResult.getLocations();
                        if (!locations.isEmpty()) {
                            for (Location location : locations) {
                                LocationLog.i(TAG,
                                    "onLocationResult location[Longitude,Latitude,Accuracy]:" + location.getLongitude()
                                        + "," + location.getLatitude() + "," + location.getAccuracy());
                            }
                        }
                    }
                }

                @Override
                public void onLocationAvailability(LocationAvailability locationAvailability) {
                    if (locationAvailability != null) {
                        boolean flag = locationAvailability.isLocationAvailable();
                        LocationLog.i(TAG, "onLocationAvailability isLocationAvailable:" + flag);
                    }
                }
            };
        }
    }

    /**
     * Requests a location update and calls back on the specified Looper thread.
     */
    private void requestLocationUpdatesWithCallback() {
        Log.i(TAG, "requestLocationUpdatesWithCallback");
        LogInfoUtil.getLogInfo(this);
        try {
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
            builder.addLocationRequest(mLocationRequest);
            LocationSettingsRequest locationSettingsRequest = builder.build();
            // Before requesting location update, invoke checkLocationSettings to check device settings.
            Task<LocationSettingsResponse> locationSettingsResponseTask =
                mSettingsClient.checkLocationSettings(locationSettingsRequest);
            locationSettingsResponseTask.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
                @Override
                public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                    Log.i(TAG, "check location settings success");
                    mFusedLocationProviderClient
                        .requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.getMainLooper())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                LocationLog.i(TAG, "requestLocationUpdatesWithCallback onSuccess");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(Exception e) {
                                LocationLog.e(TAG, "requestLocationUpdatesWithCallback onFailure:" + e.getMessage());
                            }
                        });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    LocationLog.e(TAG, "checkLocationSetting onFailure:" + e.getMessage());
                    int statusCode = ((ApiException) e).getStatusCode();
                    switch (statusCode) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                // When the startResolutionForResult is invoked, a dialog box is displayed, asking you
                                // to open the corresponding permission.
                                ResolvableApiException rae = (ResolvableApiException) e;
                                rae.startResolutionForResult(RequestLocationUpdatesWithCallbackActivity.this, 0);
                            } catch (IntentSender.SendIntentException sie) {
                                Log.e(TAG, "PendingIntent unable to execute request.");
                            }
                            break;
                        default:
                            break;
                    }
                }
            });
        } catch (Exception e) {
            LocationLog.e(TAG, "requestLocationUpdatesWithCallback exception:" + e.getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        // Removed when the location update is no longer required.
        removeLocationUpdatesWithCallback();
        super.onDestroy();
    }

    /**
     * Removed when the location update is no longer required.
     */
    private void removeLocationUpdatesWithCallback() {
        try {
            Task<Void> voidTask = mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
            voidTask.addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    LocationLog.i(TAG, "removeLocationUpdatesWithCallback onSuccess");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    LocationLog.e(TAG, "removeLocationUpdatesWithCallback onFailure:" + e.getMessage());
                }
            });
        } catch (Exception e) {
            LocationLog.e(TAG, "removeLocationUpdatesWithCallback exception:" + e.getMessage());
        }
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.location_requestLocationUpdatesWithCallback:
                    requestLocationUpdatesWithCallback();
                    break;
                case R.id.location_removeLocationUpdatesWithCallback:
                    removeLocationUpdatesWithCallback();
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            Log.e(TAG, "RequestLocationUpdatesWithCallbackActivity Exception:" + e);
        }
    }
}
