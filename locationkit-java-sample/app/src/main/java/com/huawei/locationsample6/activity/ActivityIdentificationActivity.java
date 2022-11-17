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

package com.huawei.locationsample6.activity;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hms.location.ActivityIdentification;
import com.huawei.hms.location.ActivityIdentificationData;
import com.huawei.hms.location.ActivityIdentificationService;
import com.huawei.locationsample6.R;
import com.huawei.locationsample6.RequestPermission;
import com.huawei.locationsample6.location.fusedlocation.LocationBaseActivity;
import com.huawei.locationsample6.location.fusedlocation.LocationBroadcastReceiver;
import com.huawei.logger.LocationLog;

import java.util.List;

public class ActivityIdentificationActivity extends LocationBaseActivity implements View.OnClickListener {
    private static final int PROGRESS_BAR_ORIGIN_WIDTH = 100;

    private static final int ENLARGE = 6;

    private static LinearLayout.LayoutParams type0;

    private static LinearLayout.LayoutParams type1;

    private static LinearLayout.LayoutParams type2;

    private static LinearLayout.LayoutParams type3;

    private static LinearLayout.LayoutParams type4;

    private static LinearLayout.LayoutParams type7;

    private static LinearLayout.LayoutParams type8;

    @SuppressLint("StaticFieldLeak")
    private static LinearLayout activityInVehicle;

    @SuppressLint("StaticFieldLeak")
    private static LinearLayout activityOnBicycle;

    @SuppressLint("StaticFieldLeak")
    private static LinearLayout activityOnFoot;

    @SuppressLint("StaticFieldLeak")
    private static LinearLayout activityStill;

    @SuppressLint("StaticFieldLeak")
    private static LinearLayout activityUnknown;

    @SuppressLint("StaticFieldLeak")
    private static LinearLayout activityWalking;

    @SuppressLint("StaticFieldLeak")
    private static LinearLayout activityRunning;

    public ActivityIdentificationService activityIdentificationService;

    public static final String TAG = "ActivityTransitionUpdate";

    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transition_type);
        activityIdentificationService = ActivityIdentification.getService(this);
        RequestPermission.requestActivityTransitionPermission(this);
        findViewById(R.id.requestActivityTransitionUpdate).setOnClickListener(this);
        findViewById(R.id.removeActivityTransitionUpdate).setOnClickListener(this);
        activityInVehicle = findViewById(R.id.activity_in_vehicle);
        if (activityInVehicle.getLayoutParams() instanceof LinearLayout.LayoutParams) {
            type0 = (LinearLayout.LayoutParams) activityInVehicle.getLayoutParams();
        }
        activityOnBicycle = findViewById(R.id.activity_on_bicycle);
        if (activityOnBicycle.getLayoutParams() instanceof LinearLayout.LayoutParams) {
            type1 = (LinearLayout.LayoutParams) activityOnBicycle.getLayoutParams();
        }
        activityOnFoot = findViewById(R.id.activity_on_foot);
        if (activityOnFoot.getLayoutParams() instanceof LinearLayout.LayoutParams) {
            type2 = (LinearLayout.LayoutParams) activityOnFoot.getLayoutParams();
        }
        activityStill = findViewById(R.id.activity_still);
        if (activityStill.getLayoutParams() instanceof LinearLayout.LayoutParams) {
            type3 = (LinearLayout.LayoutParams) activityStill.getLayoutParams();
        }
        activityUnknown = findViewById(R.id.activity_unknown);
        if (activityUnknown.getLayoutParams() instanceof LinearLayout.LayoutParams) {
            type4 = (LinearLayout.LayoutParams) activityUnknown.getLayoutParams();
        }
        activityWalking = findViewById(R.id.activity_walking);
        if (activityWalking.getLayoutParams() instanceof LinearLayout.LayoutParams) {
            type7 = (LinearLayout.LayoutParams) activityWalking.getLayoutParams();
        }
        activityRunning = findViewById(R.id.activity_running);
        if (activityRunning.getLayoutParams() instanceof LinearLayout.LayoutParams) {
            type8 = (LinearLayout.LayoutParams) activityRunning.getLayoutParams();
        }
        addLogFragment();
        reSet();
    }

    public void requestActivityUpdates(long detectionIntervalMillis) {
        try {
            if (pendingIntent != null) {
                removeActivityUpdates();
            }
            pendingIntent = getPendingIntent();
            LocationBroadcastReceiver.addIdentificationListener();
            activityIdentificationService.createActivityIdentificationUpdates(detectionIntervalMillis, pendingIntent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        LocationLog.i(TAG, "createActivityIdentificationUpdates onSuccess");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        LocationLog.e(TAG, "createActivityIdentificationUpdates onFailure:" + e.getMessage());
                    }
                });
        } catch (Exception e) {
            LocationLog.e(TAG, "createActivityIdentificationUpdates exception:" + e.getMessage());
        }
    }

    public void removeActivityUpdates() {
        reSet();
        try {
            LocationBroadcastReceiver.removeIdentificationListener();
            Log.i(TAG, "start to removeActivityUpdates");
            activityIdentificationService.deleteActivityIdentificationUpdates(pendingIntent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        LocationLog.i(TAG, "deleteActivityIdentificationUpdates onSuccess");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        LocationLog.e(TAG, "deleteActivityIdentificationUpdates onFailure:" + e.getMessage());
                    }
                });
        } catch (Exception e) {
            LocationLog.e(TAG, "removeActivityUpdates exception:" + e.getMessage());
        }
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.requestActivityTransitionUpdate:
                    requestActivityUpdates(5000);
                    break;
                case R.id.removeActivityTransitionUpdate:
                    removeActivityUpdates();
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            LocationLog.e(TAG, "RequestLocationUpdatesWithCallbackActivity Exception:" + e);
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
    protected void onDestroy() {
        if (pendingIntent != null) {
            removeActivityUpdates();
        }
        super.onDestroy();
    }

    public static void setData(List<ActivityIdentificationData> list) {
        reSet();
        for (int i = 0; i < list.size(); i++) {
            int type = list.get(i).getIdentificationActivity();
            int value = list.get(i).getPossibility();
            try {
                switch (type) {
                    case ActivityIdentificationData.VEHICLE:
                        type0.width = type0.width + value * ENLARGE;
                        activityInVehicle.setLayoutParams(type0);
                        break;
                    case ActivityIdentificationData.BIKE:
                        type1.width = type1.width + value * ENLARGE;
                        activityOnBicycle.setLayoutParams(type1);
                        break;
                    case ActivityIdentificationData.FOOT:
                        type2.width = type2.width + value * ENLARGE;
                        activityOnFoot.setLayoutParams(type2);
                        break;
                    case ActivityIdentificationData.STILL:
                        type3.width = type3.width + value * ENLARGE;
                        activityStill.setLayoutParams(type3);
                        break;
                    case ActivityIdentificationData.OTHERS:
                        type4.width = type4.width + value * ENLARGE;
                        activityUnknown.setLayoutParams(type4);
                        break;
                    case ActivityIdentificationData.WALKING:
                        type7.width = type7.width + value * ENLARGE;
                        activityWalking.setLayoutParams(type7);
                        break;
                    case ActivityIdentificationData.RUNNING:
                        type8.width = type8.width + value * ENLARGE;
                        activityRunning.setLayoutParams(type8);
                        break;
                    default:
                        break;
                }
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                LocationLog.e("ActivityTransitionUpdate", "setdata Exception");
            }
        }
    }

    public static void reSet() {
        type0.width = PROGRESS_BAR_ORIGIN_WIDTH;
        activityInVehicle.setLayoutParams(type0);
        type1.width = PROGRESS_BAR_ORIGIN_WIDTH;
        activityOnBicycle.setLayoutParams(type1);
        type2.width = PROGRESS_BAR_ORIGIN_WIDTH;
        activityOnFoot.setLayoutParams(type2);
        type3.width = PROGRESS_BAR_ORIGIN_WIDTH;
        activityStill.setLayoutParams(type3);
        type4.width = PROGRESS_BAR_ORIGIN_WIDTH;
        activityUnknown.setLayoutParams(type4);
        type7.width = PROGRESS_BAR_ORIGIN_WIDTH;
        activityWalking.setLayoutParams(type7);
        type8.width = PROGRESS_BAR_ORIGIN_WIDTH;
        activityRunning.setLayoutParams(type8);
    }
}
