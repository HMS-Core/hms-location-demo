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

package com.huawei.locationsample6.activity;

import android.app.PendingIntent;
import android.content.Intent;
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
    public String TAG = "ActivityTransitionUpdate";

    private final static int PROGRESS_BAR_ORIGIN_WIDTH = 100;

    private final static int enlarge = 6;

    public ActivityIdentificationService activityIdentificationService;

    private static LinearLayout.LayoutParams type0;

    private static LinearLayout.LayoutParams type1;

    private static LinearLayout.LayoutParams type2;

    private static LinearLayout.LayoutParams type3;

    private static LinearLayout.LayoutParams type4;

    private static LinearLayout.LayoutParams type7;

    private static LinearLayout.LayoutParams type8;

    private static LinearLayout activityIN_VEHICLE;

    private static LinearLayout activityON_BICYCLE;

    private static LinearLayout activityON_FOOT;

    private static LinearLayout activitySTILL;

    private static LinearLayout activityUNKNOWN;

    private static LinearLayout activityWALKING;

    private static LinearLayout activityRunning;

    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transition_type);
        activityIdentificationService = ActivityIdentification.getService(this);
        RequestPermission.requestActivityTransitionPermission(this);
        findViewById(R.id.requestActivityTransitionUpdate).setOnClickListener(this);
        findViewById(R.id.removeActivityTransitionUpdate).setOnClickListener(this);
        activityIN_VEHICLE = (LinearLayout) findViewById(R.id.activityIN_VEHICLE);
        type0 = (LinearLayout.LayoutParams) activityIN_VEHICLE.getLayoutParams();
        activityON_BICYCLE = (LinearLayout) findViewById(R.id.activityON_BICYCLE);
        type1 = (LinearLayout.LayoutParams) activityON_BICYCLE.getLayoutParams();
        activityON_FOOT = (LinearLayout) findViewById(R.id.activityON_FOOT);
        type2 = (LinearLayout.LayoutParams) activityON_FOOT.getLayoutParams();
        activitySTILL = (LinearLayout) findViewById(R.id.activitySTILL);
        type3 = (LinearLayout.LayoutParams) activitySTILL.getLayoutParams();
        activityUNKNOWN = (LinearLayout) findViewById(R.id.activityUNKNOWN);
        type4 = (LinearLayout.LayoutParams) activityUNKNOWN.getLayoutParams();
        activityWALKING = (LinearLayout) findViewById(R.id.activityWALKING);
        type7 = (LinearLayout.LayoutParams) activityWALKING.getLayoutParams();
        activityRunning = (LinearLayout) findViewById(R.id.activityRunning);
        type8 = (LinearLayout.LayoutParams) activityRunning.getLayoutParams();
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

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(this, LocationBroadcastReceiver.class);
        intent.setAction(LocationBroadcastReceiver.ACTION_PROCESS_LOCATION);
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
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
                        type0.width = type0.width + value * enlarge;
                        activityIN_VEHICLE.setLayoutParams(type0);
                        break;
                    case ActivityIdentificationData.BIKE:
                        type1.width = type1.width + value * enlarge;
                        activityON_BICYCLE.setLayoutParams(type1);
                        break;
                    case ActivityIdentificationData.FOOT:
                        type2.width = type2.width + value * enlarge;
                        activityON_FOOT.setLayoutParams(type2);
                        break;
                    case ActivityIdentificationData.STILL:
                        type3.width = type3.width + value * enlarge;
                        activitySTILL.setLayoutParams(type3);
                        break;
                    case ActivityIdentificationData.OTHERS:
                        type4.width = type4.width + value * enlarge;
                        activityUNKNOWN.setLayoutParams(type4);
                        break;
                    case ActivityIdentificationData.WALKING:
                        type7.width = type7.width + value * enlarge;
                        activityWALKING.setLayoutParams(type7);
                        break;
                    case ActivityIdentificationData.RUNNING:
                        type8.width = type8.width + value * enlarge;
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
        activityIN_VEHICLE.setLayoutParams(type0);
        type1.width = PROGRESS_BAR_ORIGIN_WIDTH;
        activityON_BICYCLE.setLayoutParams(type1);
        type2.width = PROGRESS_BAR_ORIGIN_WIDTH;
        activityON_FOOT.setLayoutParams(type2);
        type3.width = PROGRESS_BAR_ORIGIN_WIDTH;
        activitySTILL.setLayoutParams(type3);
        type4.width = PROGRESS_BAR_ORIGIN_WIDTH;
        activityUNKNOWN.setLayoutParams(type4);
        type7.width = PROGRESS_BAR_ORIGIN_WIDTH;
        activityWALKING.setLayoutParams(type7);
        type8.width = PROGRESS_BAR_ORIGIN_WIDTH;
        activityRunning.setLayoutParams(type8);
    }
}
