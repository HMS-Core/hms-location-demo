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
package com.huawei.hmssample2.geofence;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.huawei.hmf.tasks.OnCompleteListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.location.Geofence;
import com.huawei.hms.location.GeofenceRequest;
import com.huawei.hms.location.GeofenceService;
import com.huawei.hmssample2.R;
import com.huawei.hmssample2.location.fusedlocation.LocationBaseActivity;
import com.huawei.logger.LocationLog;

import java.util.ArrayList;
import java.util.List;

public class OperateGeoFenceActivity extends LocationBaseActivity implements View.OnClickListener {
    public String TAG = "operateGeoFenceActivity";
    private TextView geoFenceData;
    private TextView geoRequestData;
    private EditText removeWithPendingIntentInput;
    private EditText removeWithIDInput;
    private EditText trigger;
    public GeofenceService geofenceService;
    public static final ArrayList<RequestList> requestList = new ArrayList<RequestList>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operate_geo_fence);
        findViewById(R.id.getGeoFenceData).setOnClickListener(this);
        findViewById(R.id.CreateGeofence).setOnClickListener(this);
        findViewById(R.id.sendRequest).setOnClickListener(this);
        findViewById(R.id.sendRequestWithNew).setOnClickListener(this);
        findViewById(R.id.GetRequestMessage).setOnClickListener(this);
        findViewById(R.id.removeGeofence).setOnClickListener(this);
        findViewById(R.id.removeWithID).setOnClickListener(this);
        findViewById(R.id.removeWithIntent).setOnClickListener(this);
        removeWithPendingIntentInput = (EditText) findViewById(R.id.removeWithPendingIntentInput);
        removeWithIDInput = (EditText) findViewById(R.id.removeWithIDInput);
        trigger = (EditText) findViewById(R.id.trigger);
        geoFenceData = (TextView) findViewById(R.id.GeoFenceData);
        geoRequestData = (TextView) findViewById(R.id.GeoRequestData);
        geofenceService = new GeofenceService(this);
        addLogFragment();
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.CreateGeofence:
                    Intent geoFenceIntent = new Intent();
                    geoFenceIntent.setClass(this, GeoFenceActivity.class);
                    geoFenceIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(geoFenceIntent);
                    break;
                case R.id.removeGeofence:
                    GeoFenceData.createNewList();
                    break;
                case R.id.getGeoFenceData:
                    getData();
                    break;
                case R.id.sendRequest:
                    requestGeoFenceWithIntent();
                    break;
                case R.id.sendRequestWithNew:
                    requestGeoFenceWithNewIntent();
                    break;
                case R.id.GetRequestMessage:
                    getRequestMessage();
                    break;
                case R.id.removeWithIntent:
                    removeWithIntent();
                    break;
                case R.id.removeWithID:
                    removeWithID();
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            LocationLog.e(TAG, "operateGeoFenceActivity Exception:" + e);
        }
    }

    public void requestGeoFenceWithNewIntent() {
        if (GeoFenceData.returnList().isEmpty() == true) {
            geoRequestData.setText("no new request to add!");
            return;
        }
        if (checkUniqueID() == true) {
            geoRequestData.setText("ID already exist,please remove and add it agagin!");
            return;
        }
        GeofenceRequest.Builder geofenceRequest = new GeofenceRequest.Builder();
        geofenceRequest.createGeofenceList(GeoFenceData.returnList());
        if (trigger.getText() != null) {
            int trigGer = Integer.parseInt(trigger.getText().toString());
            geofenceRequest.setInitConversions(trigGer);
            LocationLog.d(TAG, "trigger is " + trigGer);
        } else {
            geofenceRequest.setInitConversions(5);
            LocationLog.d(TAG, "default trigger is 5");
        }

        PendingIntent pendingIntent = getPendingIntent();
        setList(pendingIntent, GeoFenceData.getRequestCode(), GeoFenceData.returnList());
        try {
            geofenceService.createGeofenceList(geofenceRequest.build(), pendingIntent)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(Task<Void> task) {
                            if (task.isSuccessful()) {
                                LocationLog.i(TAG, "add geofence success！");
                            } else {
                                // Get the status code for the error and log it using a user-friendly message.
                                LocationLog.w(TAG, "add geofence failed : " + task.getException().getMessage());
                            }
                        }
                    });
        } catch (Exception e) {
            LocationLog.i(TAG, "add geofence error：" + e.getMessage());
        }
        GeoFenceData.createNewList();
    }

    public PendingIntent findIntentByID(int a) {
        PendingIntent intent = null;
        for (int i = requestList.size() - 1; i >= 0; i--) {
            if (requestList.get(i).requestCode == a) {
                intent = requestList.get(i).pendingIntent;
                requestList.remove(i);
            }
        }
        return intent;
    }

    public void removeWithIntent() {
        int s = Integer.parseInt(removeWithPendingIntentInput.getText().toString());
        PendingIntent intent = findIntentByID(s);
        if (intent == null) {
            geoRequestData.setText("no such intent!");
            return;
        }
        try {
            geofenceService.deleteGeofenceList(intent).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(Task<Void> task) {
                    if (task.isSuccessful()) {
                        LocationLog.i(TAG, "delete geofence with intent success！");
                    } else {
                        // Get the status code for the error and log it using a user-friendly message.
                        LocationLog.w(TAG, "delete geofence with intent failed ");
                    }
                }
            });
        } catch (Exception e) {
            LocationLog.i(TAG, "delete geofence error：" + e.getMessage());
        }
    }

    public void removeWithID() {
        String s = removeWithIDInput.getText().toString();
        String[] str = s.split(" ");
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < str.length; i++)
            list.add(str[i]);
        try {
            geofenceService.deleteGeofenceList(list).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(Task<Void> task) {
                    if (task.isSuccessful()) {
                        LocationLog.i(TAG, "delete geofence with ID success！");
                    } else {
                        // Get the status code for the error and log it using a user-friendly message.
                        LocationLog.w(TAG, "delete geofence with ID failed ");
                    }
                }
            });
        } catch (Exception e) {
            LocationLog.i(TAG, "delete ID error：" + e.getMessage());
        }
        listRemoveID(str);
    }

    public void listRemoveID(String[] str) {
        for (int i = 0; i < requestList.size(); i++) {
            requestList.get(i).removeID(str);
        }
    }

    public void requestGeoFenceWithIntent() {
        if (GeoFenceData.returnList().isEmpty() == true) {
            geoRequestData.setText("no new request to add!");
            return;
        }
        if (requestList.isEmpty()) {
            geoRequestData.setText("no pengdingIntent to send!");
            return;
        }
        if (checkUniqueID() == true) {
            geoRequestData.setText("ID already exist,please remove and add it agagin!");
            return;
        }
        checkUniqueID();
        GeofenceRequest.Builder geofenceRequest = new GeofenceRequest.Builder();
        geofenceRequest.createGeofenceList(GeoFenceData.returnList());
        if (trigger.getText() != null) {
            int trigGer = Integer.parseInt(trigger.getText().toString());
            geofenceRequest.setInitConversions(trigGer);
            LocationLog.d(TAG, "trigger is " + trigGer);
        } else {
            geofenceRequest.setInitConversions(5);
            LocationLog.d(TAG, "default trigger is 5");
        }
        RequestList temp = requestList.get(requestList.size() - 1);
        PendingIntent pendingIntent = temp.pendingIntent;
        setList(pendingIntent, temp.requestCode, GeoFenceData.returnList());
        try {
            geofenceService.createGeofenceList(geofenceRequest.build(), pendingIntent)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(Task<Void> task) {
                            if (task.isSuccessful()) {
                                LocationLog.i(TAG, "add geofence success！");
                            } else {
                                // Get the status code for the error and log it using a user-friendly message.
                                LocationLog.w(TAG, "add geofence failed : " + task.getException().getMessage());
                            }
                        }
                    });
        } catch (Exception e) {
            LocationLog.i(TAG, "add geofence error：" + e.getMessage());
        }
        GeoFenceData.createNewList();
    }

    public boolean checkUniqueID() {
        for (int i = 0; i < requestList.size(); i++) {
            if (requestList.get(i).checkID() == true)
                return true;
        }
        return false;
    }

    public void getData() {
        ArrayList<Geofence> geofences = GeoFenceData.returnList();
        StringBuilder buf = new StringBuilder();
        String s = "";
        if (geofences.isEmpty()) {
            buf.append("no GeoFence Data!");
        }
        for (int i = 0; i < geofences.size(); i++) {
            buf.append("Unique ID is " + geofences.get(i).getUniqueId() + "\n");
        }
        s = buf.toString();
        geoFenceData.setText(s);
    }

    public void setList(PendingIntent intent, int code, ArrayList<Geofence> geofences) {
        RequestList temp = new RequestList(intent, code, geofences);
        requestList.add(temp);
    }

    public void getRequestMessage() {
        StringBuilder buf = new StringBuilder();
        String s = "";
        for (int i = 0; i < requestList.size(); i++) {
            buf.append(requestList.get(i).show());
        }
        if (s.equals("")) {
            buf.append("no request!");
        }
        s = buf.toString();
        geoRequestData.setText(s);
    }

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(this, GeoFenceBroadcastReceiver.class);
        intent.setAction(GeoFenceBroadcastReceiver.ACTION_PROCESS_LOCATION);
        Log.d(TAG, "new request");
        GeoFenceData.newRequest();
        return PendingIntent.getBroadcast(this, GeoFenceData.getRequestCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}

class RequestList {
    public PendingIntent pendingIntent;
    public int requestCode;
    public ArrayList<Geofence> geofences;

    public RequestList(PendingIntent pendingIntent, int requestCode, ArrayList<Geofence> geofences) {
        this.pendingIntent = pendingIntent;
        this.requestCode = requestCode;
        this.geofences = geofences;
    }

    public String show() {
        StringBuilder buf = new StringBuilder();
        String s = "";
        for (int i = 0; i < geofences.size(); i++) {
            buf.append("PendingIntent: " + String.valueOf(requestCode) + " UniqueID: " + geofences.get(i).getUniqueId() + "\n");
        }
        s = buf.toString();
        return s;
    }

    public boolean checkID() {
        ArrayList<Geofence> list = GeoFenceData.returnList();
        for (int j = 0; j < list.size(); j++) {
            String s = list.get(j).getUniqueId();
            for (int i = 0; i < geofences.size(); i++) {
                if (s.equals(geofences.get(i).getUniqueId())) {
                    return true;
                    //id already exist
                }
            }
        }
        return false;
    }

    public void removeID(String[] str) {
        for (int i = 0; i < str.length; i++) {
            String s = str[i];
            for (int j = geofences.size() - 1; j >= 0; j--) {
                if (s.equals(geofences.get(j).getUniqueId())) {
                    geofences.remove(j);
                }
            }
        }
    }
}