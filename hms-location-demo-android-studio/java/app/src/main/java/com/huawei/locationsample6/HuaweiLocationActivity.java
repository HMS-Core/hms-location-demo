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

package com.huawei.locationsample6;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import androidx.core.app.ActivityCompat;

import com.huawei.locationsample6.activity.ActivityConversionActivity;
import com.huawei.locationsample6.activity.ActivityIdentificationActivity;
import com.huawei.locationsample6.geofence.OperateGeoFenceActivity;
import com.huawei.locationsample6.location.fusedlocation.CheckSettingActivity;
import com.huawei.locationsample6.location.fusedlocation.GetLastLocationActivity;
import com.huawei.locationsample6.location.fusedlocation.GetLocationAvailabilityActivity;
import com.huawei.locationsample6.location.fusedlocation.RequestLocationUpdatesHDWithCallbackActivity;
import com.huawei.locationsample6.location.fusedlocation.RequestLocationUpdatesWithCallbackActivity;
import com.huawei.locationsample6.location.fusedlocation.RequestLocationUpdatesWithIntentActivity;
import com.huawei.locationsample6.location.fusedlocation.SetMockLocationActivity;
import com.huawei.locationsample6.location.fusedlocation.SetMockModeActivity;

/**
 * Demonstration of Huawei Location Service Usage
 *
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
        findViewById(R.id.getNavigationContextState).setOnClickListener(this);
        findViewById(R.id.check_setting).setOnClickListener(this);

        // You must have the ACCESS_COARSE_LOCATION or ACCESS_FINE_LOCATION permission. Otherwise, the location service
        // is unavailable.
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            Log.i(TAG, "android sdk < 28 Q");
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
                    android.Manifest.permission.ACCESS_COARSE_LOCATION, "android.permission.ACCESS_BACKGROUND_LOCATION"};
                ActivityCompat.requestPermissions(this, strings, 2);
            }
        }
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.location_requestLocationUpdatesWithIntent:
                    startIntent(RequestLocationUpdatesWithIntentActivity.class);
                    break;
                case R.id.location_getLastLocation:
                    startIntent(GetLastLocationActivity.class);
                    break;
                case R.id.location_getLocationAvailability:
                    startIntent(GetLocationAvailabilityActivity.class);
                    break;
                case R.id.location_requestLocationUpdatesWithCallback:
                    startIntent(RequestLocationUpdatesWithCallbackActivity.class);
                    break;
                case R.id.location_setMockLocation:
                    startIntent(SetMockLocationActivity.class);
                    break;
                case R.id.location_setMockMode:
                    startIntent(SetMockModeActivity.class);
                    break;
                case R.id.GeoFence:
                    startIntent(OperateGeoFenceActivity.class);
                    break;
                case R.id.location_activity_transition_update:
                    startIntent(ActivityConversionActivity.class);
                    break;
                default:
                    otherClick(v.getId());
                    break;
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            Log.i(TAG, "HuaweiLocation Exception:" + e);
        }
    }

    private void otherClick(int id) {
        switch (id) {
            case R.id.location_activity_update:
                startIntent(ActivityIdentificationActivity.class);
                break;
            case R.id.locationHD:
                startIntent(RequestLocationUpdatesHDWithCallbackActivity.class);
                break;
            case R.id.getNavigationContextState:
                startIntent(NavigationContextStateActivity.class);
                break;
            case R.id.check_setting:
                startIntent(CheckSettingActivity.class);
                break;
            default:
                throw new IllegalArgumentException("view error:" + id);
        }
    }

    private void startIntent(Class<?> clazz) {
        Intent intent = new Intent(this, clazz);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
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
