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
package com.huawei.hmssample2;

import com.huawei.hmssample2.activity.ActivityConversionActivity;
import com.huawei.hmssample2.activity.ActivityIdentificationActivity;
import com.huawei.hmssample2.geofence.OperateGeoFenceActivity;
import com.huawei.hmssample2.location.fusedlocation.GetLastLocationActivity;
import com.huawei.hmssample2.location.fusedlocation.GetLocationAvailabilityActivity;
import com.huawei.hmssample2.location.fusedlocation.RequestLocationUpdatesHDWithCallbackActivity;
import com.huawei.hmssample2.location.fusedlocation.RequestLocationUpdatesWithCallbackActivity;
import com.huawei.hmssample2.location.fusedlocation.RequestLocationUpdatesWithIntentActivity;
import com.huawei.hmssample2.location.fusedlocation.SetMockLocationActivity;
import com.huawei.hmssample2.location.fusedlocation.SetMockModeActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * Demonstration of Huawei Location Service Usage
 * @author lwx877867
 * @since 2020-5-11
 */
public class HuaweiLocationActivity extends Activity implements OnClickListener {
    public static final String TAG = "HuaweiLocationActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_huaweilocation);
        // Button click listeners
        findViewById(R.id.location_requestLocationUpdatesWithIntent).setOnClickListener(this);
        findViewById(R.id.location_getLocationAvailability).setOnClickListener(this);
        findViewById(R.id.location_getLastLocation).setOnClickListener(this);
        findViewById(R.id.location_requestLocationUpdatesWithCallback).setOnClickListener(this);
        findViewById(R.id.location_setMockLocation).setOnClickListener(this);
        findViewById(R.id.location_setMockMode).setOnClickListener(this);
        findViewById(R.id.location_activity_update).setOnClickListener(this);
        findViewById(R.id.location_activity_transition_update).setOnClickListener(this);
        findViewById(R.id.GeoFence).setOnClickListener(this);
        findViewById(R.id.locationHD).setOnClickListener(this);

        //You must have the ACCESS_COARSE_LOCATION or ACCESS_FINE_LOCATION permission. Otherwise, the location service is unavailable.
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            Log.i(TAG, "sdk < 28 Q");
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                String[] strings =
                        {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
                ActivityCompat.requestPermissions(this, strings, 1);
            }
        } else {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this,
                    "android.permission.ACCESS_BACKGROUND_LOCATION") != PackageManager.PERMISSION_GRANTED) {
                String[] strings = {android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        "android.permission.ACCESS_BACKGROUND_LOCATION"};
                ActivityCompat.requestPermissions(this, strings, 2);
            }
        }
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.location_requestLocationUpdatesWithIntent:
                    Intent updatesWithIntent = new Intent();
                    updatesWithIntent.setClass(this, RequestLocationUpdatesWithIntentActivity.class);
                    updatesWithIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(updatesWithIntent);
                    break;
                case R.id.location_getLastLocation:
                    Intent lastlocationIntent = new Intent();
                    lastlocationIntent.setClass(this, GetLastLocationActivity.class);
                    lastlocationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(lastlocationIntent);
                    break;
                case R.id.location_getLocationAvailability:
                    Intent locationAvailabilityIntent = new Intent();
                    locationAvailabilityIntent.setClass(this, GetLocationAvailabilityActivity.class);
                    locationAvailabilityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(locationAvailabilityIntent);
                    break;

                case R.id.location_requestLocationUpdatesWithCallback:
                    Intent updatesWithCallback = new Intent();
                    updatesWithCallback.setClass(this, RequestLocationUpdatesWithCallbackActivity.class);
                    updatesWithCallback.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(updatesWithCallback);
                    break;
                case R.id.location_setMockLocation:
                    Intent mockLocationIntent = new Intent();
                    mockLocationIntent.setClass(this, SetMockLocationActivity.class);
                    mockLocationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mockLocationIntent);
                    break;
                case R.id.location_setMockMode:
                    Intent mockModeIntent = new Intent();
                    mockModeIntent.setClass(this, SetMockModeActivity.class);
                    mockModeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mockModeIntent);
                    break;
                case R.id.GeoFence:
                    Intent geoFenceIntent = new Intent();
                    geoFenceIntent.setClass(this, OperateGeoFenceActivity.class);
                    geoFenceIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(geoFenceIntent);
                    break;
                case R.id.location_activity_transition_update:
                    Intent locationIntent3 = new Intent();
                    locationIntent3.setClass(this, ActivityConversionActivity.class);
                    locationIntent3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(locationIntent3);
                    break;
                case R.id.location_activity_update:
                    Intent locationIntent4 = new Intent();
                    locationIntent4.setClass(this, ActivityIdentificationActivity.class);
                    locationIntent4.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(locationIntent4);
                    break;
                case R.id.locationHD:
                    Intent locationIntent5 = new Intent();
                    locationIntent5.setClass(this, RequestLocationUpdatesHDWithCallbackActivity.class);
                    locationIntent5.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(locationIntent5);
                    break;
                default:
                    break;
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            Log.i(TAG, "HuaweiLocation Exception:" + e);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "onRequestPermissionsResult: apply LOCATION PERMISSION successful");
            } else {
                Log.i(TAG, "onRequestPermissionsResult: apply LOCATION PERMISSSION  failed");
            }
        }

        if (requestCode == 2) {
            if (grantResults.length > 2 && grantResults[2] == PackageManager.PERMISSION_GRANTED
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "onRequestPermissionsResult: apply ACCESS_BACKGROUND_LOCATION successful");
            } else {
                Log.i(TAG, "onRequestPermissionsResult: apply ACCESS_BACKGROUND_LOCATION  failed");
            }
        }
    }
}
