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

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hms.location.GeocoderService;
import com.huawei.hms.location.GetFromLocationNameRequest;
import com.huawei.hms.location.GetFromLocationRequest;
import com.huawei.hms.location.HWLocation;
import com.huawei.hms.location.LocationServices;
import com.huawei.location.lite.common.util.ExecutorUtil;
import com.huawei.location.lite.common.util.GsonUtil;
import com.huawei.locationsample6.NoDoubleClickUtils;
import com.huawei.locationsample6.R;
import com.huawei.logger.LocationLog;

import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Geocoder
 *
 * @since 2021-11-23
 */
public class GeocoderActivity extends LocationBaseActivity implements View.OnClickListener {
    private static final String TAG = "ReverseGeoCodeActivity";

    private EditText longitude;

    private EditText latitude;

    private EditText maxResults;

    private EditText locationName;

    private EditText revGeoLanguage;

    private EditText revGeoCountry;

    private EditText geoLanguage;

    private EditText geoCountry;

    private LinearLayout reverseGeoCodeLayout;

    private LinearLayout geoCodeLayout;

    private EditText lowerLeftLatitude;

    private EditText lowerLeftLongitude;

    private EditText upperRightLatitude;

    private EditText upperRightLongitude;

    private EditText geoResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geocoder);
        findViewById(R.id.reverseGeoCode).setOnClickListener(this);
        latitude = findViewById(R.id.latitude_value);
        longitude = findViewById(R.id.longitude_value);
        maxResults = findViewById(R.id.resultCount);
        locationName = findViewById(R.id.name);
        revGeoLanguage = findViewById(R.id.reverse_geocode_language);
        revGeoCountry = findViewById(R.id.reverse_geocode_country);
        reverseGeoCodeLayout = findViewById(R.id.reverseGeoCode_layout);
        geoCodeLayout = findViewById(R.id.geocode_layout);
        lowerLeftLatitude = findViewById(R.id.lowerLeftLatitude_input);
        lowerLeftLongitude = findViewById(R.id.lowerLeftLongitude_input);
        upperRightLatitude = findViewById(R.id.upperRightLatitude_input);
        upperRightLongitude = findViewById(R.id.upperRightLongitude_input);
        geoResults = findViewById(R.id.geocode_results);
        geoLanguage = findViewById(R.id.geocode_language);
        geoCountry = findViewById(R.id.geocode_country);
        addLogFragment();
    }

    @Override
    public void onClick(View v) {
        Log.i(TAG, "onClick: ");
        if (NoDoubleClickUtils.isFastDoubleClick()) {
            LocationLog.i(TAG, "please retry later!!!");
            return;
        }
        String latStr = latitude.getText().toString();
        String longiStr = longitude.getText().toString();
        String maxStr = maxResults.getText().toString();
        if (!checkLatValid(latStr)) {
            LocationLog.e(TAG, "Latitude is illegal");
            return;
        }
        if (!checkLongiValid(longiStr)) {
            LocationLog.e(TAG, "Longitude is illegal");
            return;
        }
        if (!isInt(maxStr)) {
            LocationLog.e(TAG, "maxResults is illegal");
            return;
        }
        final double lat = Double.parseDouble(latStr);
        final double longi = Double.parseDouble(longiStr);
        final int maxResult;
        try {
            maxResult = Integer.parseInt(maxStr);
        } catch (NumberFormatException e) {
            LocationLog.e(TAG, "maxResults is illegal");
            return;
        }
        final String language = revGeoLanguage.getText().toString();
        final String country = revGeoCountry.getText().toString();
        ExecutorUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                // Enter a proper region longitude and latitude. Otherwise, no geographic information is returned. If a
                // non-China region is used, transfer the longitude and latitude of the non-China region and ensure that
                // the longitude and latitude are correct.
                GetFromLocationRequest getFromLocationRequest = new GetFromLocationRequest(lat, longi, maxResult);
                Locale locale = new Locale(language, country);
                GeocoderService geocoderService = LocationServices.getGeocoderService(GeocoderActivity.this, locale);
                geocoderService.getFromLocation(getFromLocationRequest)
                    .addOnSuccessListener(new OnSuccessListener<List<HWLocation>>() {
                        @Override
                        public void onSuccess(List<HWLocation> hwLocation) {
                            printGeocoderResult(hwLocation);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            LocationLog.i(TAG, e.getMessage());
                        }
                    });
            }
        });
    }

    /**
     * Check whether a string is double using a regular expression.
     *
     * @param input String
     * @return Boolean value
     */
    private boolean isDouble(String input) {
        if (isInt(input)) {
            return true;
        }
        Matcher mer = Pattern.compile("^[+-]?[1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*$").matcher(input);
        return mer.matches();
    }

    public static boolean isInt(String input) {
        Matcher mer = Pattern.compile("^[+-]?[0-9]+$").matcher(input);
        return mer.matches();
    }

    private boolean checkLatValid(String latitude) {
        if (!isDouble(latitude)) {
            return false;
        }
        return true;
    }

    private boolean checkLongiValid(String longitude) {
        if (!isDouble(longitude)) {
            return false;
        }
        return true;
    }

    public void geoCoder(View view) {
        if (NoDoubleClickUtils.isFastDoubleClick()) {
            LocationLog.i(TAG, "please retry later!!!");
            return;
        }

        if (!isInt(geoResults.getText().toString())) {
            LocationLog.e(TAG, "maxResults is illegal");
            return;
        }
        final String addressName = locationName.getText().toString();
        final int results;
        try {
            results = Integer.parseInt(geoResults.getText().toString());
        } catch (Exception e) {
            LocationLog.e(TAG, "maxResults is illegal");
            return;
        }
        final String language = geoLanguage.getText().toString();
        final String country = geoCountry.getText().toString();
        ExecutorUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                GetFromLocationNameRequest getFromLocationNameRequest =
                    new GetFromLocationNameRequest(addressName, results);
                if (checkLatValid(lowerLeftLatitude.getText().toString())
                    && checkLongiValid(lowerLeftLongitude.getText().toString())
                    && checkLatValid(upperRightLatitude.getText().toString())
                    && checkLongiValid(upperRightLongitude.getText().toString())) {
                    double lowerLeftlat = Double.parseDouble(lowerLeftLatitude.getText().toString());
                    double lowerLeftlng = Double.parseDouble(lowerLeftLongitude.getText().toString());
                    double upperrightlat = Double.parseDouble(upperRightLatitude.getText().toString());
                    double upperrightlng = Double.parseDouble(upperRightLongitude.getText().toString());
                    getFromLocationNameRequest.setLowerLeftLatitude(lowerLeftlat);
                    getFromLocationNameRequest.setLowerLeftLongitude(lowerLeftlng);
                    getFromLocationNameRequest.setUpperRightLatitude(upperrightlat);
                    getFromLocationNameRequest.setUpperRightLongitude(upperrightlng);
                }
                Locale locale = new Locale(language, country);
                GeocoderService geocoderService = LocationServices.getGeocoderService(GeocoderActivity.this, locale);
                // Enter the correct location information. Otherwise, the geographic information cannot be parsed. For a
                // non-China region, transfer the location information of the non-China region.
                geocoderService.getFromLocationName(getFromLocationNameRequest)
                    .addOnSuccessListener(new OnSuccessListener<List<HWLocation>>() {
                        @Override
                        public void onSuccess(List<HWLocation> hwLocations) {
                            printGeocoderResult(hwLocations);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception ex) {
                            LocationLog.i(TAG, ex.getMessage());
                        }
                    });
            }
        });
    }

    private void printGeocoderResult(List<HWLocation> geocoderResult) {
        if (geocoderResult == null || geocoderResult.isEmpty()) {
            LocationLog.i(TAG, GsonUtil.getInstance().toJson(geocoderResult));
        } else {
            for (HWLocation hwLocation : geocoderResult) {
                StringBuilder builder = new StringBuilder();
                if (hwLocation != null) {

                    builder.append("Location:{latitude=")
                        .append(hwLocation.getLatitude())
                        .append(",longitude=")
                        .append(hwLocation.getLongitude())
                        .append(",countryName=")
                        .append(hwLocation.getCountryName())
                        .append(",countryCode=")
                        .append(hwLocation.getCountryCode())
                        .append(",state=")
                        .append(hwLocation.getState())
                        .append(",city=")
                        .append(hwLocation.getCity())
                        .append(",county=")
                        .append(hwLocation.getCounty())
                        .append(",street=")
                        .append(hwLocation.getStreet())
                        .append(",featureName=")
                        .append(hwLocation.getFeatureName())
                        .append(",postalCode=")
                        .append(hwLocation.getPostalCode())
                        .append(",phone=")
                        .append(hwLocation.getPhone())
                        .append(",url=")
                        .append(hwLocation.getUrl())
                        .append(",extraInfo=")
                        .append(GsonUtil.getInstance().toJson(hwLocation.getExtraInfo()))
                        .append("}" + System.lineSeparator());
                } else {
                    builder.append("Location:{}");
                }
                LocationLog.i(TAG, builder.toString());
            }
        }
    }

    public void switchMode(View view) {
        if (reverseGeoCodeLayout.isShown()) {
            reverseGeoCodeLayout.setVisibility(View.GONE);
            geoCodeLayout.setVisibility(View.VISIBLE);

        } else {
            geoCodeLayout.setVisibility(View.GONE);
            reverseGeoCodeLayout.setVisibility(View.VISIBLE);
        }
    }

}
