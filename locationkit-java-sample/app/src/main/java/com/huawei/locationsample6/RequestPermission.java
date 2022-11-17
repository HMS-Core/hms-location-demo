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

package com.huawei.locationsample6;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.huawei.logger.LocationLog;

public class RequestPermission {
    public static final String TAG = "RequestPermission";

    public static void requestLocationPermission(Context context) {
        // check location permisiion
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            Log.i(TAG, "android sdk < 28 Q");
            if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                String[] strings =
                    {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
                if (context instanceof Activity) {
                    ActivityCompat.requestPermissions((Activity) context, strings, 1);
                }
            }
        } else {
            if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context,
                    "android.permission.ACCESS_BACKGROUND_LOCATION") != PackageManager.PERMISSION_GRANTED) {
                String[] strings = {Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION, "android.permission.ACCESS_BACKGROUND_LOCATION"};
                if (context instanceof Activity) {
                    ActivityCompat.requestPermissions((Activity) context, strings, 2);
                }
            }
        }
    }

    public static void requestActivityTransitionPermission(Context context) {
        // check location permisiion
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            if (ActivityCompat.checkSelfPermission(context,
                "com.huawei.hms.permission.ACTIVITY_RECOGNITION") != PackageManager.PERMISSION_GRANTED) {
                String[] permissions = {"com.huawei.hms.permission.ACTIVITY_RECOGNITION"};
                if (context instanceof Activity) {
                    ActivityCompat.requestPermissions((Activity) context, permissions, 1);
                }
                LocationLog.i(TAG, "requestActivityTransitionButtonHandler: apply permission");
            }
        } else {
            if (ActivityCompat.checkSelfPermission(context,
                "android.permission.ACTIVITY_RECOGNITION") != PackageManager.PERMISSION_GRANTED) {
                String[] permissions = {"android.permission.ACTIVITY_RECOGNITION"};
                if (context instanceof Activity) {
                    ActivityCompat.requestPermissions((Activity) context, permissions, 2);
                }
                LocationLog.i(TAG, "requestActivityTransitionButtonHandler: apply permission");
            }
        }
    }

    public static void requestBackgroundPermission(Context context) {
        if (ActivityCompat.checkSelfPermission(context,
            "android.permission.ACCESS_BACKGROUND_LOCATION") != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = {"android.permission.ACCESS_BACKGROUND_LOCATION"};
            if (context instanceof Activity) {
                ActivityCompat.requestPermissions((Activity) context, permissions, 0);
            }
            LocationLog.i(TAG, "android.permission.ACCESS_BACKGROUND_LOCATION");
        }
    }
}
