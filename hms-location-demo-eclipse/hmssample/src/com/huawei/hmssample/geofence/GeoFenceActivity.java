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

package com.huawei.hmssample.geofence;

import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.common.ResolvableApiException;
import com.huawei.hms.location.FusedLocationProviderClient;
import com.huawei.hms.location.Geofence;
import com.huawei.hms.location.LocationAvailability;
import com.huawei.hms.location.LocationCallback;
import com.huawei.hms.location.LocationRequest;
import com.huawei.hms.location.LocationResult;
import com.huawei.hms.location.LocationServices;
import com.huawei.hms.location.LocationSettingsRequest;
import com.huawei.hms.location.LocationSettingsResponse;
import com.huawei.hms.location.LocationSettingsStatusCodes;
import com.huawei.hms.location.SettingsClient;
import com.huawei.hmssample.R;
import com.huawei.hmssample.location.fusedlocation.LocationBaseActivity;
import com.huawei.logger.LocationLog;

import java.util.ArrayList;
import java.util.List;

public class GeoFenceActivity extends LocationBaseActivity implements View.OnClickListener{
    private EditText setlatitude;
    private EditText setlongitude;
    private EditText setradius;
    private EditText setUniqueId;
    private EditText setConversions;
    private EditText setValidContinueTime;
    private EditText setDwellDelayTime;
    private EditText setNotificationInterval;
    LocationCallback mLocationCallback;
    LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private SettingsClient mSettingsClient;
    public String TAG = "GeoFenceActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo_fence);
        setlatitude = (EditText) findViewById(R.id.setlatitude);
        setlongitude = (EditText) findViewById(R.id.setlongitude);
        setradius = (EditText) findViewById(R.id.setradius);
        setUniqueId = (EditText) findViewById(R.id.setUniqueId);
        setConversions = (EditText) findViewById(R.id.setConversions);
        setValidContinueTime = (EditText) findViewById(R.id.setValidContinueTime);
        setDwellDelayTime = (EditText) findViewById(R.id.setDwellDelayTime);
        setNotificationInterval = (EditText) findViewById(R.id.setNotificationInterval);
        findViewById(R.id.getCurrentLocation).setOnClickListener(this);
        findViewById(R.id.geofence_btn).setOnClickListener(this);
        findViewById(R.id.showGeoList).setOnClickListener(this);
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
                                setlatitude.setText(String.valueOf(location.getLatitude()));
                                setlongitude.setText(String.valueOf(location.getLongitude()));
                                removeLocationUpdatesWithCallback();
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
    public void getText(){
        Data temp = new Data();
        temp.longitude = Double.parseDouble(setlongitude.getText().toString());
        temp.latitude = Double.parseDouble(setlatitude.getText().toString());
        temp.radius = Float.parseFloat(setradius.getText().toString());
        temp.uniqueId = setUniqueId.getText().toString();
        temp.conversions = Integer.parseInt(setConversions.getText().toString());
        temp.validContinueTime = Long.parseLong(setValidContinueTime.getText().toString());
        temp.dwellDelayTime = Integer.parseInt(setDwellDelayTime.getText().toString());
        temp.notificationInterval = Integer.parseInt(setNotificationInterval.getText().toString());
        GeoFenceData.addGeofence(temp);
    }
    public void getLocation(){
        requestLocationUpdatesWithCallback();
    }
    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.geofence_btn:
                    getText();
                    break;
                case R.id.showGeoList:
                    GeoFenceData.show();
                    break;
                case R.id.getCurrentLocation:
                    getLocation();
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            LocationLog.e(TAG, "GeoFenceActivity Exception:" + e);
        }
    }
    private void requestLocationUpdatesWithCallback() {
        try {
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
            builder.addLocationRequest(mLocationRequest);
            LocationSettingsRequest locationSettingsRequest = builder.build();
            // Before requesting location update, invoke checkLocationSettings to check device settings.
            Task<LocationSettingsResponse> locationSettingsResponseTask = mSettingsClient.checkLocationSettings(locationSettingsRequest);
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
                                    LocationLog.e(TAG,
                                            "requestLocationUpdatesWithCallback onFailure:" + e.getMessage());
                                }
                            });
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            LocationLog.e(TAG, "checkLocationSetting onFailure:" + e.getMessage());
                            int statusCode = ((ApiException) e).getStatusCode();
                            switch (statusCode) {
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    try {
                                        //When the startResolutionForResult is invoked, a dialog box is displayed, asking you to open the corresponding permission.
                                        ResolvableApiException rae = (ResolvableApiException) e;
                                        rae.startResolutionForResult(GeoFenceActivity.this, 0);
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
    private void removeLocationUpdatesWithCallback() {
        try {
            Task<Void> voidTask = mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
            voidTask.addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    LocationLog.i(TAG, "removeLocationUpdatesWithCallback onSuccess");
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            LocationLog.e(TAG, "removeLocationUpdatesWithCallback onFailure:" + e.getMessage());
                        }
                    });
        } catch (Exception e) {
            LocationLog.e(TAG, "removeLocationUpdatesWithCallback exception:" + e.getMessage());
        }
    }
    protected void onDestroy() {
        //Removed when the location update is no longer required.
        super.onDestroy();
    }

}

class Data{
    public double latitude;
    public double longitude;
    public float radius;
    public String uniqueId;
    public int conversions;
    public long validContinueTime;
    public int dwellDelayTime;
    public int notificationInterval;
}
class GeoFenceData{
    private static int requestCode = 0;
    static ArrayList<Geofence> geofences = new ArrayList<Geofence>();;
    static Geofence.Builder geoBuild = new Geofence.Builder();
    public static void addGeofence(Data data){
        if(checkStyle( geofences,data.uniqueId) == false){
            LocationLog.d("GeoFenceActivity","not unique ID!");
            LocationLog.i("GeoFenceActivity", "addGeofence failed!");
            return;
        }
        geoBuild.setRoundArea(data.latitude,data.longitude,data.radius);
        geoBuild.setUniqueId(data.uniqueId);
        geoBuild.setConversions(data.conversions);
        geoBuild.setValidContinueTime(data.validContinueTime);
        geoBuild.setDwellDelayTime(data.dwellDelayTime);
        geoBuild.setNotificationInterval(data.notificationInterval);
        geofences.add(geoBuild.build());
        LocationLog.i("GeoFenceActivity", "addGeofence success!");
    }
    public static void createNewList(){
        geofences = new ArrayList<Geofence>();
    }
    public static boolean checkStyle(ArrayList<Geofence> geofences,String ID){
        for(int i = 0;i<geofences.size();i++){
            if(geofences.get(i).getUniqueId().equals(ID))
                return false;
        }
        return true;
    }
    public static ArrayList<Geofence> returnList(){
        return geofences;
    }
    public static void show(){
        if(geofences.isEmpty()){
            LocationLog.d("GeoFenceActivity","no GeoFence Data!");
        }
        for(int i = 0;i<geofences.size();i++){
                LocationLog.d("GeoFenceActivity","Unique ID is "+(geofences.get(i)).getUniqueId());
        }
    }
    public static void newRequest(){
        requestCode++;
    }
    public static int getRequestCode(){
        return requestCode;
    }
 }