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

package com.huawei.locationsample6.location.fusedlocation;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;

import androidx.core.app.ActivityCompat;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hms.location.FusedLocationProviderClient;
import com.huawei.hms.location.HWLocation;
import com.huawei.hms.location.LocationAvailability;
import com.huawei.hms.location.LocationCallback;
import com.huawei.hms.location.LocationRequest;
import com.huawei.hms.location.LocationResult;
import com.huawei.hms.location.LocationServices;
import com.huawei.location.lite.common.util.ExecutorUtil;
import com.huawei.locationsample6.JsonDataUtil;
import com.huawei.locationsample6.R;
import com.huawei.logger.LocationLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RequestLocationUpdatesHDWithCallbackActivity extends LocationBaseActivity implements View.OnClickListener {
    private static final String TAG = "LocationWithHd";

    LocationCallback mLocationHDCallback;

    LocationCallback mLocationIndoorCallback;

    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hms_hd);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        TableLayout tableLayout = findViewById(R.id.callback_table_layout_show);
        String locationRequestJson = JsonDataUtil.getJson(this, "LocationRequest.json", true);
        initDataDisplayView(TAG, tableLayout, locationRequestJson);
        findViewById(R.id.btn_remove_hd).setOnClickListener(this);
        findViewById(R.id.btn_hd).setOnClickListener(this);
        findViewById(R.id.btn_remove_indoorHd).setOnClickListener(this);
        findViewById(R.id.btn_indoorHd).setOnClickListener(this);
        findViewById(R.id.btn_remove_indoorHdAndHigh).setOnClickListener(this);
        findViewById(R.id.btn_indoorHdAndHigh).setOnClickListener(this);
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
            case R.id.btn_indoorHd:
                getLocationWithIndoor();
                break;
            case R.id.btn_remove_indoorHd:
                removeLocationIndoor();
                break;
            case R.id.btn_indoorHdAndHigh:
                getLocationWithIndoor();
                break;
            case R.id.btn_remove_indoorHdAndHigh:
                removeLocationIndoor();
                break;
            default:
                break;
        }
    }

    private void removeLocationHd() {
        ExecutorUtil.getInstance().execute(new Runnable() {
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
        });
        Log.i(TAG, "call removeLocationUpdatesWithCallback success.");
    }

    private void removeLocationIndoor() {
        ExecutorUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    fusedLocationProviderClient.removeLocationUpdates(mLocationIndoorCallback)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                LocationLog.i(TAG, "removeLocationIndoor onSuccess");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(Exception e) {
                                LocationLog.i(TAG, "removeLocationIndoor onFailure:" + e.getMessage());
                            }
                        });
                } catch (Exception e) {
                    LocationLog.e(TAG, "removeLocationIndoor exception:" + e.getMessage());
                }
            }
        });
        Log.i(TAG, "call removeLocationUpdatesWithCallback success.");
    }

    private void getLocationWithHd() {
        ExecutorUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    LocationRequest locationRequest = new LocationRequest();
                    setLocationRequest(locationRequest);
                    // Sets the type of the returned coordinate:
                    // COORDINATE_TYPE_WGS84 Indicates that the 84 coordinate is returned.
                    // COORDINATE_TYPE_GCJ02 Indicates that the 02 coordinate is returned. The default value is
                    // COORDENATE_TYPE_WGS84.
                    // If a high-precision coordinate is returned, no conversion is performed.
                    locationRequest.setCoordinateType(LocationRequest.COORDINATE_TYPE_WGS84);
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
        });
    }

    private void getLocationWithIndoor() {
        ExecutorUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    LocationRequest locationRequest = new LocationRequest();
                    setLocationRequest(locationRequest);
                    // Sets the type of the returned coordinate:
                    // COORDINATE_TYPE_WGS84 Indicates that the 84 coordinate is returned.
                    // COORDINATE_TYPE_GCJ02 Indicates that the 02 coordinate is returned. The default value is
                    // COORDENATE_TYPE_WGS84.
                    locationRequest.setCoordinateType(LocationRequest.COORDINATE_TYPE_WGS84);
                    if (null == mLocationIndoorCallback) {
                        mLocationIndoorCallback = new LocationCallback() {
                            @Override
                            public void onLocationResult(LocationResult locationResult) {
                                Log.i(TAG, "getLocationWithIndoor callback onLocationResult print");
                                logResultIndoor(locationResult);
                            }

                            @Override
                            public void onLocationAvailability(LocationAvailability locationAvailability) {
                                Log.i(TAG, "getLocationWithIndoor callback onLocationAvailability print");
                                if (locationAvailability != null) {
                                    boolean flag = locationAvailability.isLocationAvailable();
                                    LocationLog.i(TAG, "onLocationAvailability isLocationAvailable:" + flag);
                                }
                            }
                        };
                    }
                    fusedLocationProviderClient
                        .requestLocationUpdatesEx(locationRequest, mLocationIndoorCallback, Looper.getMainLooper())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                LocationLog.i(TAG, "getLocationWithIndoor onSuccess");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(Exception e) {
                                LocationLog.i(TAG, "getLocationWithIndoor onFailure:" + e.getMessage());
                            }
                        });
                } catch (Exception e) {
                    LocationLog.i(TAG, "getLocationWithIndoor exception :" + e.getMessage());
                }
            }
        });
    }

    private void logResult(LocationResult locationRequest) {
        if (locationRequest != null) {
            Log.i(TAG, "getLocationWithHd callback  onLocationResult locationResult is not null");
            logLocation(locationRequest.getLocations());
            logHwLocation(locationRequest.getHWLocationList());
        }
    }

    private void logResultIndoor(LocationResult locationResult) {
        if (locationResult != null) {
            Log.i(TAG, "getLocationWithHd callback  onLocationResult locationResult is not null");
            logLocationIndoor(locationResult.getLocations());
            logHwLocationIndoor(locationResult.getHWLocationList());
        }
    }

    private void setLocationRequest(LocationRequest locationRequest) {
        TableLayout tableLayout = findViewById(R.id.callback_table_layout_show);
        ArrayList<String> paramList = new ArrayList<String>();
        TableRow[] rows = new TableRow[tableLayout.getChildCount()];
        for (int i = 0; i < rows.length; i++) {
            if (tableLayout.getChildAt(i) instanceof TableRow) {
                rows[i] = (TableRow) tableLayout.getChildAt(i);
                if (rows[i].getChildAt(1) instanceof EditText) {
                    EditText editText = (EditText) rows[i].getChildAt(1);
                    paramList.add(editText.getText().toString());
                }
            }
        }
        if (!paramList.isEmpty()) {
            locationRequest.setPriority(Integer.parseInt("".equals(paramList.get(0)) ? "200" : paramList.get(0)));
            locationRequest.setInterval(Long.parseLong("".equals(paramList.get(1)) ? "5000" : paramList.get(1)));
            locationRequest.setFastestInterval(Long.parseLong("".equals(paramList.get(2)) ? "5000" : paramList.get(2)));
            locationRequest.setExpirationTime(
                Long.parseLong("".equals(paramList.get(4)) ? "9223372036854775807" : paramList.get(4)));
            locationRequest.setExpirationDuration(
                Long.parseLong("".equals(paramList.get(5)) ? "9223372036854775807" : paramList.get(5)));
            locationRequest
                .setNumUpdates(Integer.parseInt("".equals(paramList.get(6)) ? "2147483647" : paramList.get(6)));
            locationRequest
                .setSmallestDisplacement(Float.parseFloat("".equals(paramList.get(7)) ? "0" : paramList.get(7)));
            locationRequest.setMaxWaitTime(Long.parseLong("".equals(paramList.get(8)) ? "0" : paramList.get(8)));
        }
    }

    private void logHwLocation(List<HWLocation> hwLocations) {
        if (hwLocations == null || hwLocations.isEmpty()) {
            Log.i(TAG, "getLocationWithHd callback hwLocations is empty");
            return;
        }
        for (HWLocation hwLocation : hwLocations) {
            if (hwLocation == null) {
                Log.i(TAG, "getLocationWithHd callback hwLocation is empty");
                return;
            }
            LocationLog.i(TAG,
                "[new]location result : " + "\n" + "Longitude = " + hwLocation.getLongitude() + "\n" + "Latitude = "
                    + hwLocation.getLatitude() + "\n" + "Accuracy = " + hwLocation.getAccuracy() + "\n"
                    + hwLocation.getCountryName() + "," + hwLocation.getState() + "," + hwLocation.getCity() + ","
                    + hwLocation.getCounty() + "," + hwLocation.getFeatureName());
        }
    }

    private void logLocation(List<Location> locations) {
        if (locations == null || locations.isEmpty()) {
            Log.i(TAG, "getLocationWithHd callback locations is empty");
            return;
        }
        for (Location location : locations) {
            if (location == null) {
                Log.i(TAG, "getLocationWithHd callback location is empty");
                return;
            }
            LocationLog.i(TAG, "[old]location result : " + "\n" + "Longitude = " + location.getLongitude() + "\n"
                + "Latitude = " + location.getLatitude() + "\n" + "Accuracy = " + location.getAccuracy());
        }
    }

    private void logHwLocationIndoor(List<HWLocation> hwLocations) {
        if (hwLocations == null || hwLocations.isEmpty()) {
            Log.i(TAG, "getLocationWithHd callback hwLocations is empty");
            return;
        }
        for (HWLocation hwLocation : hwLocations) {
            if (hwLocation == null) {
                Log.i(TAG, "getLocationWithHd callback hwLocation is empty");
                return;
            }
            LocationLog.i(TAG,
                "[new]location result : " + "Longitude = " + hwLocation.getLongitude() + System.lineSeparator()
                    + "Latitude = " + hwLocation.getLatitude() + System.lineSeparator() + "Accuracy = "
                    + hwLocation.getAccuracy());
            Map<String, Object> maps = hwLocation.getExtraInfo();
            parseIndoorLocation(maps);
        }
    }

    private void logLocationIndoor(List<Location> locations) {
        if (locations == null || locations.isEmpty()) {
            Log.i(TAG, "getLocationWithHd callback locations is empty");
            return;
        }
        for (Location location : locations) {
            if (location == null) {
                Log.i(TAG, "getLocationWithHd callback location is empty");
                return;
            }
            LocationLog.i(TAG,
                "[old]location result : " + System.lineSeparator() + "Longitude = " + location.getLongitude()
                    + System.lineSeparator() + "Latitude = " + location.getLatitude() + System.lineSeparator()
                    + "Accuracy = " + location.getAccuracy());
            Bundle extraInfo = location.getExtras();
            parseIndoorLocation(extraInfo);
        }
    }

    // Parsing Indoor Location Result Information
    public void parseIndoorLocation(Map<String, Object> maps) {
        if (maps != null && !maps.isEmpty()) {
            if (maps.containsKey("isHdNlpLocation")) {
                Object object = maps.get("isHdNlpLocation");
                if (object instanceof Boolean) {
                    boolean isIndoorLocation = (boolean) object;
                    if (isIndoorLocation) {
                        // floor:Floor ID
                        // (For example, 1 corresponds to F1. The mapping varies with buildings. For details, contact
                        // the operation personnel.)
                        int floor = (int) maps.get("floor");
                        // floorAcc:Floor confidence (value range: 0-100)
                        int floorAcc = (int) maps.get("floorAcc");
                        LocationLog.i(TAG, "[new]location result : " + System.lineSeparator() + System.lineSeparator()
                            + "floor = " + floor + System.lineSeparator() + "floorAcc = " + floorAcc);
                    }
                }
            }
        }
    }

    // Parsing Indoor Location Result Information
    public void parseIndoorLocation(Bundle extraInfo) {
        if (extraInfo == null) {
            return;
        }
        if (extraInfo.getBoolean("isHdNlpLocation", false)) {
            // Parsing Indoor Location Result Information
            // floor:Floor ID
            // (For example, 1 corresponds to F1. The mapping varies with buildings. For details, contact
            // the operation personnel.)
            int floor = extraInfo.getInt("floor", Integer.MIN_VALUE);
            // floorAcc:Floor confidence (value range: 0-100)
            int floorAcc = extraInfo.getInt("floorAcc", Integer.MIN_VALUE);
            LocationLog.i(TAG, "[old]location result : " + System.lineSeparator() + System.lineSeparator() + "floor = "
                + floor + System.lineSeparator() + "floorAcc = " + floorAcc);

        }
    }
}
