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
import android.widget.CheckBox;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.location.ActivityConversionInfo;
import com.huawei.hms.location.ActivityConversionRequest;
import com.huawei.hms.location.ActivityIdentification;
import com.huawei.hms.location.ActivityIdentificationService;
import com.huawei.locationsample6.R;
import com.huawei.locationsample6.RequestPermission;
import com.huawei.locationsample6.location.fusedlocation.LocationBaseActivity;
import com.huawei.locationsample6.location.fusedlocation.LocationBroadcastReceiver;
import com.huawei.logger.LocationLog;

import java.util.ArrayList;
import java.util.List;

public class ActivityConversionActivity extends LocationBaseActivity implements View.OnClickListener {
    public static final String TAG = "ActivityTransitionConvert";

    public ActivityIdentificationService activityIdentificationService;

    public ActivityConversionRequest activityTransitionRequest;

    public List<ActivityConversionInfo> transitions;

    private CheckBox cbInVehicleIn;

    private CheckBox cbWalkingIn;

    private CheckBox cbWalkingOut;

    private CheckBox cbInVehicleOut;

    private CheckBox cbOnBicycleIn;

    private CheckBox cbOnBicycleOut;

    private CheckBox cbOnFootIn;

    private CheckBox cbOnFootOut;

    private CheckBox cbStillIn;

    private CheckBox cbStillOut;

    private CheckBox cbRunningIn;

    private CheckBox cbRunningOut;

    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transition);
        activityIdentificationService = ActivityIdentification.getService(this);
        RequestPermission.requestActivityTransitionPermission(this);
        cbInVehicleIn = findViewById(R.id.IN_VEHICLE_IN);
        cbInVehicleOut = findViewById(R.id.IN_VEHICLE_OUT);
        cbOnBicycleIn = findViewById(R.id.ON_BICYCLE_IN);
        cbOnBicycleOut = findViewById(R.id.ON_BICYCLE_OUT);
        cbOnFootIn = findViewById(R.id.ON_FOOT_IN);
        cbOnFootOut = findViewById(R.id.ON_FOOT_OUT);
        cbStillIn = findViewById(R.id.STILL_IN);
        cbStillOut = findViewById(R.id.STILL_OUT);
        cbWalkingIn = findViewById(R.id.WALKING_IN);
        cbWalkingOut = findViewById(R.id.WALKING_OUT);
        cbRunningIn = findViewById(R.id.RUNNING_IN);
        cbRunningOut = findViewById(R.id.RUNNING_OUT);
        findViewById(R.id.btnSubmit).setOnClickListener(this);
        findViewById(R.id.btnMove).setOnClickListener(this);
        addLogFragment();
    }

    public void getRequest() {
        transitions = new ArrayList<ActivityConversionInfo>();
        ActivityConversionInfo.Builder activityTransition = new ActivityConversionInfo.Builder();
        RequestValueResut requestValueResut = new RequestValueResut();
        if (cbInVehicleIn.isChecked()) {
            requestValueResut.addList(100, 0);
        }
        if (cbInVehicleOut.isChecked()) {
            requestValueResut.addList(100, 1);
        }
        if (cbOnBicycleIn.isChecked()) {
            requestValueResut.addList(101, 0);
        }
        if (cbOnBicycleOut.isChecked()) {
            requestValueResut.addList(101, 1);
        }
        if (cbOnFootIn.isChecked()) {
            requestValueResut.addList(102, 0);
        }
        if (cbOnFootOut.isChecked()) {
            requestValueResut.addList(102, 1);
        }
        if (cbStillIn.isChecked()) {
            requestValueResut.addList(103, 0);
        }
        if (cbStillOut.isChecked()) {
            requestValueResut.addList(103, 1);
        }
        if (cbWalkingIn.isChecked()) {
            requestValueResut.addList(107, 0);
        }
        if (cbWalkingOut.isChecked()) {
            requestValueResut.addList(107, 1);
        }
        if (cbRunningIn.isChecked()) {
            requestValueResut.addList(108, 0);
        }
        if (cbRunningOut.isChecked()) {
            requestValueResut.addList(108, 1);
        }
        ArrayList<RequestValue> result = requestValueResut.result;
        for (int i = 0; i < result.size(); i++) {
            RequestValue temp = result.get(i);
            activityTransition.setActivityType(temp.activityType);
            activityTransition.setConversionType(temp.activityTransition);
            transitions.add(activityTransition.build());
        }
        Log.d(TAG, "transitions size is " + transitions.size());
    }

    public void requestActivityTransitionUpdate() {
        try {
            if (pendingIntent != null) {
                removeActivityTransitionUpdates();
            }
            LocationBroadcastReceiver.addConversionListener();
            pendingIntent = getPendingIntent();
            activityTransitionRequest = new ActivityConversionRequest(transitions);
            Task<Void> task =
                activityIdentificationService.createActivityConversionUpdates(activityTransitionRequest, pendingIntent);
            task.addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    LocationLog.i(TAG, "createActivityConversionUpdates onSuccess");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    LocationLog.e(TAG, "createActivityConversionUpdates onFailure:" + e.getMessage());
                }
            });
        } catch (Exception e) {
            LocationLog.e(TAG, "createActivityConversionUpdates exception:" + e.getMessage());
        }
    }

    public void removeActivityTransitionUpdates() {
        try {
            LocationBroadcastReceiver.removeConversionListener();
            activityIdentificationService.deleteActivityConversionUpdates(pendingIntent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        LocationLog.i(TAG, "deleteActivityConversionUpdates onSuccess");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        LocationLog.e(TAG, "deleteActivityConversionUpdates onFailure:" + e.getMessage());
                    }
                });
        } catch (Exception e) {
            LocationLog.e(TAG, "removeActivityTransitionUpdates exception:" + e.getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        if (pendingIntent != null) {
            removeActivityTransitionUpdates();
        }
        super.onDestroy();
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
            return PendingIntent.getBroadcast(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | (1<<25));
        }
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.btnSubmit:
                    getRequest();
                    requestActivityTransitionUpdate();
                    break;
                case R.id.btnMove:
                    removeActivityTransitionUpdates();
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            LocationLog.e(TAG, "RequestLocationUpdatesWithCallbackActivity Exception:" + e);
        }
    }

    class RequestValue {
        public int activityType;

        public int activityTransition;

        RequestValue(int a, int b) {
            this.activityType = a;
            this.activityTransition = b;
        }
    }

    class RequestValueResut {
        public ArrayList<RequestValue> result = new ArrayList<RequestValue>();

        public void addList(int activityType, int activityTransition) {
            RequestValue temp = new RequestValue(activityType, activityTransition);
            result.add(temp);
        }
    }
}
