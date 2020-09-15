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

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
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
import com.huawei.hmssample2.JsonDataUtil;
import com.huawei.hmssample2.LogInfoUtil;
import com.huawei.hmssample2.R;
import com.huawei.logger.LocationLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class RequestLocationUpdatesHDWithCallbackActivity extends LocationBaseActivity implements View.OnClickListener {
    private static final String TAG = "RequestLocationUpdatesHDWithCallbackActivity";

    private FusedLocationProviderClient fusedLocationProviderClient;

    LocationCallback mLocationHDCallback;

    protected void initDataDisplayView(TableLayout tableLayout, String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            Iterator iterator = jsonObject.keys();
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                String value = jsonObject.getString(key);

                TableRow tableRow = new TableRow(getBaseContext());

                TextView textView = new TextView(getBaseContext());
                textView.setText(key);
                textView.setTextColor(Color.GRAY);
                textView.setId(getBaseContext().getResources()
                    .getIdentifier(key + "_key", "id", getBaseContext().getPackageName()));
                tableRow.addView(textView);

                EditText editText = new EditText(getBaseContext());
                editText.setText(value);
                editText.setId(getBaseContext().getResources()
                    .getIdentifier(key + "_value", "id", getBaseContext().getPackageName()));
                editText.setTextColor(Color.DKGRAY);
                tableRow.addView(editText);
                tableLayout.addView(tableRow);
            }
        } catch (JSONException e) {
            Log.e(TAG, "initDataDisplayView JSONException:" + e.getMessage());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hms_hd);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        TableLayout tableLayout = findViewById(R.id.callback_table_layout_show);
        String locationRequestJson = JsonDataUtil.getJson(this, "LocationRequest.json", true);
        initDataDisplayView(tableLayout, locationRequestJson);
        findViewById(R.id.btn_remove_hd).setOnClickListener(this);
        findViewById(R.id.btn_hd).setOnClickListener(this);
        addLogFragment();
        if (ActivityCompat.checkSelfPermission(this,
            android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            String[] strings =
                {android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION};
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
        LogInfoUtil.getLogInfo(this);
        new Thread() {
            @Override
            public void run() {
                try {
                    LocationRequest locationRequest = new LocationRequest();
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
                    fusedLocationProviderClient
                        .requestLocationUpdatesEx(locationRequest, mLocationHDCallback, Looper.getMainLooper())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                LocationLog.i(TAG, "getLocationWithHd onSuccess");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
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
            logLocation(locationRequest.getLocations());
            logHwLocation(locationRequest.getHWLocationList());
        }
    }

    private void logHwLocation(List<HWLocation> hwLocations) {
        if (hwLocations == null || hwLocations.isEmpty()) {
            Log.i(TAG, "getLocationWithHd callback hwLocations is empty");
            return;
        }
        for (HWLocation hwLocation : hwLocations) {
            if (hwLocation == null ) {
                Log.i(TAG, "getLocationWithHd callback hwLocation is empty");
                return;
            }
            boolean hdbBinary = false;
            Map<String, Object> extraInfo = hwLocation.getExtraInfo();
            int SourceType = 0;
            if (extraInfo != null && !extraInfo.isEmpty() && extraInfo.containsKey("SourceType")) {
                Object object = extraInfo.get("SourceType");
                if (object instanceof Integer) {
                    SourceType = (int) object;
                    hdbBinary = getBinaryFlag(SourceType);
                }
            }
            String hdFlag = "";
            if (hdbBinary) {
                hdFlag = "result is HD";
            }
            LocationLog.i(TAG,
                "[new]location result : " + "\n" + "Longitude = " + hwLocation.getLongitude() + "\n" + "Latitude = "
                    + hwLocation.getLatitude() + "\n" + "Accuracy = " + hwLocation.getAccuracy() + "\n"
                    + "SourceType = " + SourceType + "\n" + hwLocation.getCountryName() + "," + hwLocation.getState()
                    + "," + hwLocation.getCity() + "," + hwLocation.getCounty() + "," + hwLocation.getFeatureName()
                    + "\n" + hdFlag);
        }
    }

    private void logLocation(List<Location> locations) {
        String hdFlag = "";
        if (locations == null || locations.isEmpty()) {
            Log.i(TAG, "getLocationWithHd callback locations is empty");
            return;
        }
        for (Location location : locations) {
            if (location == null ) {
                Log.i(TAG, "getLocationWithHd callback location is empty");
                return;
            }
            boolean hdbBinary = false;
            Bundle extraInfo = location.getExtras();
            int SourceType = 0;
            if (extraInfo != null && !extraInfo.isEmpty() && extraInfo.containsKey("SourceType")) {
                SourceType = extraInfo.getInt("SourceType", -1);
                hdbBinary = getBinaryFlag(SourceType);
            }
            if (hdbBinary) {
                hdFlag = "result is HD";
            }
            LocationLog.i(TAG,
                "[old]location result : " + "\n" + "Longitude = " + location.getLongitude() + "\n" + "Latitude = "
                    + location.getLatitude() + "\n" + "Accuracy = " + location.getAccuracy() + "\n" + "SourceType = "
                    + SourceType + "\n" + hdFlag);
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
