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

import java.util.List;

import com.huawei.hms.location.ActivityConversionData;
import com.huawei.hms.location.ActivityConversionResponse;
import com.huawei.hms.location.ActivityIdentificationData;
import com.huawei.hms.location.ActivityIdentificationResponse;
import com.huawei.hms.location.LocationAvailability;
import com.huawei.hms.location.LocationResult;
import com.huawei.hmssample2.activity.ActivityIdentificationActivity;
import com.huawei.logger.LocationLog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

/**
 * location broadcast receiver
 * @author lwx877867
 * @since 2020-5-11
 */
public class LocationBroadcastReceiver extends BroadcastReceiver {
    public static final String ACTION_PROCESS_LOCATION = "com.huawei.hms.location.ACTION_PROCESS_LOCATION";

    private static final String TAG = "LocationReceiver";

    public static boolean isListenActivityIdentification = false;

    public static boolean isListenActivityConversion = false;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            StringBuilder sb = new StringBuilder();
            if (ACTION_PROCESS_LOCATION.equals(action)) {
                // Processing LocationResult information
                Log.i(TAG, "null != intent");
                String messageBack = "";
                StringBuffer buf = new StringBuffer();
                ActivityConversionResponse activityTransitionResult = ActivityConversionResponse.getDataFromIntent(intent);
                if (activityTransitionResult != null && isListenActivityConversion == true) {
                    List<ActivityConversionData> list = activityTransitionResult.getActivityConversionDatas();
                    for (int i = 0; i < list.size(); i++) {
                        Log.i(TAG, "activityTransitionEvent[" + i + "]" + list.get(i));
                        buf.append(list.get(i) + "\n");
                    }
                    messageBack = buf.toString();
                    LocationLog.d("TAG", messageBack);
                }

                ActivityIdentificationResponse activityRecognitionResult = ActivityIdentificationResponse.getDataFromIntent(intent);
                if (activityRecognitionResult != null && isListenActivityIdentification == true) {
                    LocationLog.i(TAG, "activityRecognitionResult:" + activityRecognitionResult);
                    List<ActivityIdentificationData> list = activityRecognitionResult.getActivityIdentificationDatas();
                    ActivityIdentificationActivity.setData(list);
                }
                if (LocationResult.hasResult(intent)) {
                    LocationResult result = LocationResult.extractResult(intent);
                    if (result != null) {
                        List<Location> locations = result.getLocations();
                        if (!locations.isEmpty()) {
                            sb.append("requestLocationUpdatesWithIntent[Longitude,Latitude,Accuracy]:");
                            sb.append((char)13).append((char)10);
                            for (Location location : locations) {
                                sb.append(location.getLongitude())
                                    .append(",")
                                    .append(location.getLatitude())
                                    .append(",")
                                    .append(location.getAccuracy())
                                    .append((char)13).append((char)10);

                            }
                        }
                    }
                }

//                Processing LocationAvailability information
                if (LocationAvailability.hasLocationAvailability(intent)) {
                    LocationAvailability locationAvailability =
                        LocationAvailability.extractLocationAvailability(intent);
                    if (locationAvailability != null) {
                        sb.append("[locationAvailability]:" + locationAvailability.isLocationAvailable());
                        sb.append((char)13).append((char)10);
                    }
                }
            }
            if (!"".equals(sb.toString())){
                LocationLog.i(TAG, sb.toString());
            }

        }
    }
    public static void addConversionListener(){
        isListenActivityConversion = true;
    }
    public static void removeConversionListener(){
        isListenActivityConversion = false;
    }
    public static void addIdentificationListener(){
        isListenActivityIdentification = true;
    }
    public static void removeIdentificationListener(){
        isListenActivityIdentification = false;
    }
}
