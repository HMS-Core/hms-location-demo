package com.huawei.hmssample2.location.fusedlocation;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.view.View;
import android.widget.TableLayout;

import com.huawei.hmf.tasks.OnCompleteListener;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.common.ResolvableApiException;
import com.huawei.hms.location.LocationRequest;
import com.huawei.hms.location.LocationServices;
import com.huawei.hms.location.LocationSettingsRequest;
import com.huawei.hms.location.LocationSettingsResponse;
import com.huawei.hms.location.LocationSettingsStates;
import com.huawei.hms.location.LocationSettingsStatusCodes;
import com.huawei.hms.location.SettingsClient;
import com.huawei.hmssample2.JsonDataUtil;
import com.huawei.hmssample2.R;
import com.huawei.logger.LocationLog;

public class CheckSettingActivity extends LocationBaseActivity implements View.OnClickListener {
    private static final String TAG = "CheckSettingActivity";
    private SettingsClient settingsClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_setting);
        settingsClient = LocationServices.getSettingsClient(this);
        findViewById(R.id.checkLocationSetting).setOnClickListener(this);
        TableLayout tableLayout = findViewById(R.id.check_setting_table_layout_show);
        String locationRequestJson = JsonDataUtil.getJson(this, "LocationRequest.json", true);
        initDataDisplayView(TAG, tableLayout, locationRequestJson);
        String settingJson = JsonDataUtil.getJson(this, "CheckLocationSettings.json", true);
        initDataDisplayView(TAG, tableLayout, settingJson);
        addLogFragment();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.checkLocationSetting) {
            checkSettings();
        }
    }

    private void checkSettings() {
        new Thread() {
            @Override
            public void run() {
                try {
                    CheckSettingsRequest checkSettingsRequest = new CheckSettingsRequest()
                    LocationRequest locationRequest = new LocationRequest();
                    checkSettingsRequest.setLocationRequest(locationRequest);
                    checkSettingsRequest.setAlwaysShow(false);
                    checkSettingsRequest.setNeedBle(false);
                    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                            .addLocationRequest(checkSettingsRequest.getLocationRequest())
                            .setAlwaysShow(checkSettingsRequest.isAlwaysShow())
                            .setNeedBle(checkSettingsRequest.isNeedBle());
                    settingsClient.checkLocationSettings(builder.build())
                            .addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
                                @Override
                                public void onComplete(Task<LocationSettingsResponse> task) {
                                    if (task != null && task.isSuccessful()) {
                                        LocationSettingsResponse response = task.getResult();
                                        if (response == null) {
                                            return;
                                        }
                                        LocationSettingsStates locationSettingsStates =
                                                response.getLocationSettingsStates();
                                        StringBuilder stringBuilder = new StringBuilder();
                                        stringBuilder.append("\nisBlePresent=")
                                                .append(locationSettingsStates.isBlePresent());
                                        stringBuilder.append(",\nisBleUsable=")
                                                .append(locationSettingsStates.isBleUsable());
										stringBuilder.append(",\nisGNSSPresent=")
                                                .append(locationSettingsStates.isGnssPresent());
                                        stringBuilder.append(",\nisGNSSUsable=")
                                                .append(locationSettingsStates.isGnssUsable());
                                        stringBuilder.append(",\nisLocationPresent=")
                                                .append(locationSettingsStates.isLocationPresent());
                                        stringBuilder.append(",\nisLocationUsable=")
                                                .append(locationSettingsStates.isLocationUsable());
                                        stringBuilder.append(",\nisNetworkLocationUsable=")
                                                .append(locationSettingsStates.isNetworkLocationUsable());
                                        stringBuilder.append(",\nisNetworkLocationPresent=")
                                                .append(locationSettingsStates.isNetworkLocationPresent());
                                        stringBuilder.append(",\nisHMSLocationUsable=")
                                                .append(locationSettingsStates.isHMSLocationUsable());
                                        stringBuilder.append(",\nisHMSLocationPresent=")
                                                .append(locationSettingsStates.isHMSLocationPresent());
                                        LocationLog.i(TAG, "checkLocationSetting onComplete:" + stringBuilder.toString());
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(Exception e) {
                                    LocationLog.i(TAG, "checkLocationSetting onFailure:" + e.getMessage());
                                    int statusCode = ((ApiException) e).getStatusCode();
                                    switch (statusCode) {
                                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                            android.util.Log.i(TAG,
                                                    "Location settings are not satisfied. Attempting to upgrade "
                                                            + "location settings ");
                                            try {
                                                // Show the dialog by calling startResolutionForResult(), and check the
                                                // result in onActivityResult().
                                                ResolvableApiException rae = (ResolvableApiException) e;
                                                rae.startResolutionForResult(CheckSettingActivity.this, 0);
                                            } catch (IntentSender.SendIntentException sie) {
                                                android.util.Log.i(TAG, "PendingIntent unable to execute request.");
                                            }
                                            break;
                                    }
                                }
                            });
                } catch (Exception e) {
                    LocationLog.i(TAG, "checkLocationSetting exception:" + e.getMessage());
                }
            }
        }.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (null == data) {
            return;
        }
        LocationSettingsStates locationSettingsStates = LocationSettingsStates.fromIntent(data);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\nisBlePresent=").append(locationSettingsStates.isBlePresent());
        stringBuilder.append(",\nisBleUsable=").append(locationSettingsStates.isBleUsable());
        stringBuilder.append(",\nisGnssPresent=").append(locationSettingsStates.isGnssPresent());
        stringBuilder.append(",\nisGnssUsable=").append(locationSettingsStates.isGnssUsable());
        stringBuilder.append(",\nisLocationPresent=").append(locationSettingsStates.isLocationPresent());
        stringBuilder.append(",\nisLocationUsable=").append(locationSettingsStates.isLocationUsable());
        stringBuilder.append(",\nisNetworkLocationPresent=").append(locationSettingsStates.isNetworkLocationPresent());
        stringBuilder.append(",\nisNetworkLocationUsable=").append(locationSettingsStates.isNetworkLocationUsable());
        stringBuilder.append(",\nisHMSLocationUsable=").append(locationSettingsStates.isHMSLocationUsable());
        stringBuilder.append(",\nisHMSLocationPresent=").append(locationSettingsStates.isHMSLocationPresent());
        LocationLog.i(TAG, "checkLocationSetting onComplete:" + stringBuilder.toString());
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case 0:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        LocationLog.i(TAG, "User agreed to make required location settings changes.");
                        // Nothing to do. startLocationupdates() gets called in onResume again.
                        break;
                    case Activity.RESULT_CANCELED:
                        LocationLog.i(TAG, "User chose not to make required location settings changes.");
                        break;
                }
                break;
        }
    }

    private class CheckSettingsRequest {
        private LocationRequest locationRequest;

        private boolean alwaysShow;

        private boolean needBle;

        public LocationRequest getLocationRequest() {
            return locationRequest;
        }

        public void setLocationRequest(LocationRequest locationRequest) {
            this.locationRequest = locationRequest;
        }

        public boolean isAlwaysShow() {
            return alwaysShow;
        }

        public void setAlwaysShow(boolean alwaysShow) {
            this.alwaysShow = alwaysShow;
        }

        public boolean isNeedBle() {
            return needBle;
        }

        public void setNeedBle(boolean needBle) {
            this.needBle = needBle;
        }
    }
}
