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

package com.huawei.hmssample.activity;

import android.app.PendingIntent;
import android.content.Intent;
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
import com.huawei.hmssample.R;
import com.huawei.hmssample.RequestPermission;
import com.huawei.hmssample.location.fusedlocation.LocationBaseActivity;
import com.huawei.hmssample.location.fusedlocation.LocationBroadcastReceiver;
import com.huawei.logger.LocationLog;

import java.util.ArrayList;
import java.util.List;

public class ActivityConversionActivity extends LocationBaseActivity implements View.OnClickListener {
    private CheckBox IN_VEHICLE_IN;
    private CheckBox WALKING_IN;
    private CheckBox WALKING_OUT;
    private CheckBox IN_VEHICLE_OUT;
    private CheckBox ON_BICYCLE_IN;
    private CheckBox ON_BICYCLE_OUT;
    private CheckBox ON_FOOT_IN;
    private CheckBox ON_FOOT_OUT;
    private CheckBox STILL_IN;
    private CheckBox STILL_OUT;
    private CheckBox RUNNING_IN;
    private CheckBox RUNNING_OUT;

    public String TAG = "ActivityTransitionConvert";

    public ActivityIdentificationService activityIdentificationService;

    public ActivityConversionRequest activityTransitionRequest;

    public List<ActivityConversionInfo> transitions;

    private PendingIntent pendingIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transition);
        activityIdentificationService = ActivityIdentification.getService(this);
        RequestPermission.requestActivityTransitionPermission(this);
        IN_VEHICLE_IN = (CheckBox) findViewById(R.id.IN_VEHICLE_IN);
        IN_VEHICLE_OUT = (CheckBox) findViewById(R.id.IN_VEHICLE_OUT);
        ON_BICYCLE_IN = (CheckBox) findViewById(R.id.ON_BICYCLE_IN);
        ON_BICYCLE_OUT = (CheckBox) findViewById(R.id.ON_BICYCLE_OUT);
        ON_FOOT_IN = (CheckBox) findViewById(R.id.ON_FOOT_IN);
        ON_FOOT_OUT = (CheckBox) findViewById(R.id.ON_FOOT_OUT);
        STILL_IN = (CheckBox) findViewById(R.id.STILL_IN);
        STILL_OUT = (CheckBox) findViewById(R.id.STILL_OUT);
        WALKING_IN = (CheckBox) findViewById(R.id.WALKING_IN);
        WALKING_OUT = (CheckBox) findViewById(R.id.WALKING_OUT);
        RUNNING_IN = (CheckBox) findViewById(R.id.RUNNING_IN);
        RUNNING_OUT = (CheckBox) findViewById(R.id.RUNNING_OUT);
        findViewById(R.id.btnSubmit).setOnClickListener(this);
        findViewById(R.id.btnMove).setOnClickListener(this);
        addLogFragment();
    }

    public void getRequest() {
        transitions = new ArrayList<ActivityConversionInfo>();
        ActivityConversionInfo.Builder activityTransition = new ActivityConversionInfo.Builder();
        RequestValueResut requestValueResut = new RequestValueResut();
        if (IN_VEHICLE_IN.isChecked())
            requestValueResut.addList(100, 0);
        if (IN_VEHICLE_OUT.isChecked())
            requestValueResut.addList(100, 1);
        if (ON_BICYCLE_IN.isChecked())
            requestValueResut.addList(101, 0);
        if (ON_BICYCLE_OUT.isChecked())
            requestValueResut.addList(101, 1);
        if (ON_FOOT_IN.isChecked())
            requestValueResut.addList(102, 0);
        if (ON_FOOT_OUT.isChecked())
            requestValueResut.addList(102, 1);
        if (STILL_IN.isChecked())
            requestValueResut.addList(103, 0);
        if (STILL_OUT.isChecked())
            requestValueResut.addList(103, 1);
        if (WALKING_IN.isChecked())
            requestValueResut.addList(107, 0);
        if (WALKING_OUT.isChecked())
            requestValueResut.addList(107, 1);
        if (RUNNING_IN.isChecked())
            requestValueResut.addList(108, 0);
        if (RUNNING_OUT.isChecked())
            requestValueResut.addList(108, 1);
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
            if(pendingIntent != null){
                removeActivityTransitionUpdates();
            }
            LocationBroadcastReceiver.addConversionListener();
            pendingIntent = getPendingIntent();
            activityTransitionRequest = new ActivityConversionRequest(transitions);
            Task<Void> task = activityIdentificationService.createActivityConversionUpdates(activityTransitionRequest, pendingIntent);
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
        if(pendingIntent != null) {
            removeActivityTransitionUpdates();
        }
        super.onDestroy();
    }

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(this, LocationBroadcastReceiver.class);
        intent.setAction(LocationBroadcastReceiver.ACTION_PROCESS_LOCATION);
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
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
