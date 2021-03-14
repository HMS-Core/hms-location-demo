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

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hms.location.FusedLocationProviderClient;
import com.huawei.hms.location.HWLocation;
import com.huawei.hms.location.LocationAvailability;
import com.huawei.hms.location.LocationCallback;
import com.huawei.hms.location.LocationRequest;
import com.huawei.hms.location.LocationResult;
import com.huawei.hms.location.LocationServices;
import com.huawei.hmssample.R;
import com.huawei.logger.LocationLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class RequestLocationUpdatesHDWithCallbackActivity extends LocationBaseActivity implements View.OnClickListener {
    private static final String TAG = "RequestLocationUpdatesHDWithCallbackActivity";

    private FusedLocationProviderClient fusedLocationProviderClient;

    LocationCallback mLocationHDCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hms_hd);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        TableLayout tableLayout = (TableLayout)findViewById(R.id.callback_table_layout_show);
        String locationRequestJson = "{\"priority\": 102,\"interval\": 5000,\"fastestInterval\": 5000,\"isFastestIntervalExplicitlySet\": false,\"expirationTime\": 9223372036854775807,\"expirationDuration\": 9223372036854775807,\"numUpdates\": 2147483647,\"smallestDisplacement\": 0,\"maxWaitTime\": 0,\"needAddress\": false,\"language\": \"zh\",\"countryCode\": \"CN\"}";
        initDataDisplayView(TAG,tableLayout, locationRequestJson);
        findViewById(R.id.btn_remove_hd).setOnClickListener(this);
        findViewById(R.id.btn_hd).setOnClickListener(this);
        addLogFragment();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            String[] strings = {
                    android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION
            };
            ActivityCompat.requestPermissions(this, strings, 1);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_hd:
                getLocationWithHd();
                break;
            case R.id.btn_remove_hd:
                removeLocationHd();
                break;
            default:
                break;
        }
    }

    private void removeLocationHd() {
        new Thread() {
            @Override
            public void run() {
                try {
                    fusedLocationProviderClient.removeLocationUpdates(mLocationHDCallback)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    LocationLog.i(TAG, "removeLocationHd onSuccess");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(Exception e) {
                                    LocationLog.i(TAG, "removeLocationHd onFailure:" + e.getMessage());
                                }
                            });
                } catch (Exception e) {
                    LocationLog.e(TAG, "removeLocationHd exception:" + e.getMessage());
                }
            }
        }.start();
        Log.i(TAG, "call removeLocationUpdatesWithCallback success.");
    }

    private void getLocationWithHd() {
        new Thread() {
            @Override
            public void run() {
                try {
                    LocationRequest locationRequest = new LocationRequest();
                    setLocationRequest(locationRequest);
                    if (null == mLocationHDCallback) {
                        mLocationHDCallback = new LocationCallback() {
                            @Override
                            public void onLocationResult(LocationResult locationRequest) {
                                Log.i(TAG, "getLocationWithHd callback onLocationResult print");
                                logResult(locationRequest);
                            }

                            @Override
                            public void onLocationAvailability(LocationAvailability locationAvailability) {
                                Log.i(TAG, "getLocationWithHd callback onLocationAvailability print");
                                if (locationAvailability != null) {
                                    boolean flag = locationAvailability.isLocationAvailable();
                                    LocationLog.i(TAG, "onLocationAvailability isLocationAvailable:" + flag);
                                }
                            }
                        };
                    }
                    fusedLocationProviderClient.requestLocationUpdatesEx(locationRequest, mLocationHDCallback,
                            Looper.getMainLooper()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            LocationLog.i(TAG, "getLocationWithHd onSuccess");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            LocationLog.i(TAG, "getLocationWithHd onFailure:" + e.getMessage());
                        }
                    });
                } catch (Exception e) {
                    LocationLog.i(TAG, "getLocationWithHd exception :" + e.getMessage());
                }
            }
        }.start();
    }

    private void logResult(LocationResult locationRequest) {
        if (locationRequest != null) {
            Log.i(TAG, "getLocationWithHd callback  onLocationResult locationResult is not null");
            List<Location> locations = locationRequest.getLocations();
            logLocation(locations);
            List<HWLocation> hwLocations = locationRequest.getHWLocationList();
            logHwLocation(hwLocations);
        }
    }
    
    private void setLocationRequest(LocationRequest locationRequest) {
        TableLayout tableLayout = findViewById(R.id.callback_table_layout_show);
        ArrayList<String> paramList = new ArrayList<String>();
        TableRow[] rows = new TableRow[tableLayout.getChildCount()];
        for (int i = 0; i < rows.length; i++) {
            rows[i] = (TableRow) tableLayout.getChildAt(i);
            if (rows[i].getChildAt(1) instanceof EditText) {
                paramList.add(((EditText) rows[i].getChildAt(1)).getText().toString());
                Log.d(TAG, ((EditText) rows[i].getChildAt(1)).getText().toString());
            }
        }
        locationRequest.setPriority(Integer.parseInt("".equals(paramList.get(0)) ? "102" : paramList.get(0)));
        locationRequest.setInterval(Long.parseLong("".equals(paramList.get(1)) ? "5000" : paramList.get(1)));
        locationRequest.setFastestInterval(Long.parseLong("".equals(paramList.get(2)) ? "5000" : paramList.get(2)));
        locationRequest
            .setExpirationTime(Long.parseLong("".equals(paramList.get(4)) ? "9223372036854775807" : paramList.get(4)));
        locationRequest.setExpirationDuration(
            Long.parseLong("".equals(paramList.get(5)) ? "9223372036854775807" : paramList.get(5)));
        locationRequest.setNumUpdates(Integer.parseInt("".equals(paramList.get(6)) ? "2147483647" : paramList.get(6)));
        locationRequest.setSmallestDisplacement(Float.parseFloat("".equals(paramList.get(7)) ? "0" : paramList.get(7)));
        locationRequest.setMaxWaitTime(Long.parseLong("".equals(paramList.get(8)) ? "0" : paramList.get(8)));
        locationRequest.setNeedAddress(Boolean.parseBoolean("".equals(paramList.get(9)) ? "false" : paramList.get(9)));
        locationRequest.setLanguage("".equals(paramList.get(10)) ? "zh" : paramList.get(10));
        locationRequest.setCountryCode("".equals(paramList.get(11)) ? "CN" : paramList.get(11));

    }

    private void logHwLocation(List<HWLocation> hwLocations) {
        if (hwLocations == null || hwLocations.isEmpty()) {
            Log.i(TAG, "getLocationWithHd callback hwLocations is empty");
            return;
        }

        for (HWLocation hwLocation : hwLocations) {
            // 是否有位置语义结果
            if (TextUtils.isEmpty(hwLocation.getCountryName())) {
                return;
            }
            boolean hdbBinary = false;
            Map<String, Object> extraInfo = hwLocation.getExtraInfo();
            int sourceType = 0;
            if (extraInfo != null && !extraInfo.isEmpty() && extraInfo.containsKey("sourceType")) {
                Object object = extraInfo.get("sourceType");
                if (object instanceof Integer) {
                    sourceType = (Integer) object;
                    hdbBinary = getBinaryFlag(sourceType);
                }
            }
            String hdFlag = "";
            if (hdbBinary) {
                hdFlag = "result is HD";
            }
            LocationLog.i(TAG,
                "[new]location result : " + "\n" + "Longitude = " + hwLocation.getLongitude() + "\n" + "Latitude = "
                    + hwLocation.getLatitude() + "\n" + "Accuracy = " + hwLocation.getAccuracy() + "\n"
                    + "sourceType = " + sourceType + "\n" + hwLocation.getCountryName() + "," + hwLocation.getState()
                    + "," + hwLocation.getCity() + "," + hwLocation.getCounty() + "," + hwLocation.getFeatureName()
                    + "\n" + hdFlag);
        }
    }

    private void logLocation(List<Location> locations) {
        String hdFlag = "";
        if (locations == null || locations.isEmpty()) {
            Log.i(TAG, "getLocationWithHd callback location is empty");
            return;
        }
        for (Location location : locations) {
            boolean hdbBinary = false;
            Bundle extraInfo = location.getExtras();
            int sourceType = 0;
            if (extraInfo != null && !extraInfo.isEmpty() && extraInfo.containsKey("sourceType")) {
                sourceType = extraInfo.getInt("sourceType", -1);
                hdbBinary = getBinaryFlag(sourceType);
            }
            if (hdbBinary) {
                hdFlag = "result is HD";
            }
            LocationLog.i(TAG,
                "[old]location result : " + "\n" + "Longitude = " + location.getLongitude() + "\n" + "Latitude = "
                    + location.getLatitude() + "\n" + "Accuracy = " + location.getAccuracy() + "\n" + "sourceType = "
                    + sourceType + "\n" + hdFlag);
        }
    }

    private boolean getBinaryFlag(int sourceType) {
        boolean flag = false;
        if (sourceType <= 0) {
            return false;
        }
        String binary = Integer.toBinaryString(sourceType);
        if (binary.length() >= 4) {
            String isbinary = binary.substring(binary.length() - 4).charAt(0) + "";
            flag = isbinary.equals("1");
        }
        return flag;
    }
   
}