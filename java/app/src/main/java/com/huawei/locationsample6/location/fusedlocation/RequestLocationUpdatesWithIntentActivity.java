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

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.location.FusedLocationProviderClient;
import com.huawei.hms.location.LocationRequest;
import com.huawei.hms.location.LocationServices;
import com.huawei.hms.location.LocationSettingsRequest;
import com.huawei.hms.location.LocationSettingsResponse;
import com.huawei.hms.location.SettingsClient;
import com.huawei.locationsample6.LogInfoUtil;
import com.huawei.locationsample6.R;
import com.huawei.logger.LocationLog;

/**
 * Example of Using requestLocationUpdates and removeLocationUpdates
 * Use the PendingIntent mode to continuously request the location update.
 * The location update result LocationResult and LocationAvailability are encapsulated in the Intent, and the send() of
 * the transferred PendingIntent is invoked to send the result to the requester.
 * If the requester process is killed, use this method to continue to call back.
 * If the requester does not want to receive the location update result when the process is killed, see
 * requestLocationUpdates (LocationRequest request,LocationCallback callback,Looper looper).
 *
 * @since 2020-5-11
 */
public class RequestLocationUpdatesWithIntentActivity extends LocationBaseActivity implements OnClickListener {
    public static final String TAG = "LocationUpdatesIntent";

    LocationRequest mLocationRequest;

    private FusedLocationProviderClient mFusedLocationProviderClient;

    private SettingsClient mSettingsClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_request_location_updates_intent);
        // Button click listeners
        findViewById(R.id.location_requestLocationUpdatesWithIntent).setOnClickListener(this);
        findViewById(R.id.location_removeLocationUpdatesWithIntent).setOnClickListener(this);
        addLogFragment();
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);
        mLocationRequest = new LocationRequest();
        // Sets the interval for location update (unit: Millisecond)
        mLocationRequest.setInterval(10000);
        // Sets the priority
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    protected void onDestroy() {
        // Removed when the location update is no longer required.
        removeLocationUpdatesWithIntent();
        super.onDestroy();
    }

    /**
     * Request for location update
     */
    private void requestLocationUpdatesWithIntent() {
        LogInfoUtil.getLogInfo(this);
        try {
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
            builder.addLocationRequest(mLocationRequest);
            LocationSettingsRequest locationSettingsRequest = builder.build();
            // Before requesting location update, invoke checkLocationSettings to check device settings.
            Task<LocationSettingsResponse> locationSettingsResponseTask =
                mSettingsClient.checkLocationSettings(locationSettingsRequest);
            locationSettingsResponseTask.addOnSuccessListener(locationSettingsResponse -> {
                Log.i(TAG, "check location settings success");
                mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, getPendingIntent())
                    .addOnSuccessListener(aVoid -> LocationLog.i(TAG, "requestLocationUpdatesWithIntent onSuccess"))
                    .addOnFailureListener(
                        e -> LocationLog.e(TAG, "requestLocationUpdatesWithIntent onFailure:" + e.getMessage()));
            }).addOnFailureListener(e -> LocationLog.e(TAG, "checkLocationSetting onFailure:" + e.getMessage()));
        } catch (Exception e) {
            LocationLog.e(TAG, "requestLocationUpdatesWithIntent exception:" + e.getMessage());
        }
    }

    /**
     * Remove Location Update
     */
    private void removeLocationUpdatesWithIntent() {
        try {
            Task<Void> voidTask = mFusedLocationProviderClient.removeLocationUpdates(getPendingIntent());
            voidTask.addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    LocationLog.i(TAG, "removeLocationUpdatesWithIntent onSuccess");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    LocationLog.e(TAG, "removeLocationUpdatesWithIntent onFailure:" + e.getMessage());
                }
            });
        } catch (Exception e) {
            LocationLog.e(TAG, "removeLocationUpdatesWithIntent exception:" + e.getMessage());
        }
    }

    @SuppressLint("WrongConstant")
    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(this, LocationBroadcastReceiver.class);
        intent.setAction(LocationBroadcastReceiver.ACTION_PROCESS_LOCATION);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
            return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            // For Android 12 or later devices, proactively configure the pendingIntent variability.
            // The default value is PendingIntent.FLAG_MUTABLE. If compileSDKVersion is 30 or less, set this parameter
            // to 1<<25.
            return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | (1 << 25));
        }
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.location_requestLocationUpdatesWithIntent:
                    requestLocationUpdatesWithIntent();
                    break;
                case R.id.location_removeLocationUpdatesWithIntent:
                    removeLocationUpdatesWithIntent();
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            Log.e(TAG, "RequestLocationUpdatesWithIntentActivity Exception:" + e);
        }
    }
}
