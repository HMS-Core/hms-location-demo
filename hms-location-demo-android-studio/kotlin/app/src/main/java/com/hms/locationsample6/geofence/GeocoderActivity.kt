/**
 * Copyright 2020 Huawei Technologies co, Ltd All
 * Rights reserved
 * Licenced under the Apache License,Version 2.0(the "License");
 * you may not use this file except in compliance with license
 * you may obtain a copy of the license at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by application law or agreed to in writing software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permission and
 * limitations under the License
 */

package com.hms.locationsample6.geofence

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import com.hms.locationsample6.R
import com.hms.locationsample6.activity.BaseActivity
import com.hms.locationsample6.logger.LocationLog
import com.hms.locationsample6.utils.NoDoubleClickUtils
import com.huawei.hmf.tasks.OnSuccessListener
import com.huawei.hms.location.GetFromLocationNameRequest
import com.huawei.hms.location.GetFromLocationRequest
import com.huawei.hms.location.HWLocation
import com.huawei.hms.location.LocationServices
import com.huawei.location.lite.common.util.ExecutorUtil
import com.huawei.location.lite.common.util.GsonUtil
import kotlinx.android.synthetic.main.activity_geocoder.*
import java.util.*
import java.util.regex.Pattern

class GeocoderActivity : BaseActivity(), View.OnClickListener {
    private val TAG = "ReverseGeoCodeActivity"

    private var longitude: EditText? = null

    private var latitude: EditText? = null

    private var maxResults: EditText? = null

    private var locationName: EditText? = null

    private var revGeoLanguage: EditText? = null

    private var revGeoCountry: EditText? = null

    private var geoLanguage: EditText? = null

    private var geoCountry: EditText? = null

    private var reverseGeoCodeLayout: LinearLayout? = null

    private var geoCodeLayout: LinearLayout? = null

    private var lowerLeftLatitude: EditText? = null

    private var lowerLeftLongitude: EditText? = null

    private var upperRightLatitude: EditText? = null

    private var upperRightLongitude: EditText? = null

    private var geoResults: EditText? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_geocoder)
        btn_reverseGeoCode?.setOnClickListener(this)
        btn_switch_mode?.setOnClickListener(this)
        btn_geocode?.setOnClickListener(this)
        latitude = findViewById(R.id.latitude_value)
        longitude = findViewById(R.id.longitude_value)
        maxResults = findViewById(R.id.resultCount)
        locationName = findViewById(R.id.name)
        revGeoLanguage = findViewById(R.id.reverse_geocode_language)
        revGeoCountry = findViewById(R.id.reverse_geocode_country)
        reverseGeoCodeLayout = findViewById(R.id.reverseGeoCode_layout)
        geoCodeLayout = findViewById(R.id.geocode_layout)
        lowerLeftLatitude = findViewById(R.id.lowerLeftLatitude_input)
        lowerLeftLongitude = findViewById(R.id.lowerLeftLongitude_input)
        upperRightLatitude = findViewById(R.id.upperRightLatitude_input)
        upperRightLongitude = findViewById(R.id.upperRightLongitude_input)
        geoResults = findViewById(R.id.geocode_results)
        geoLanguage = findViewById(R.id.geocode_language)
        geoCountry = findViewById(R.id.geocode_country)
        addLogFragment()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_reverseGeoCode -> reverseGeoCode()
            R.id.btn_switch_mode -> switchMode()
            R.id.btn_geocode -> geoCoder()

        }
    }

    private fun reverseGeoCode() {
        if (NoDoubleClickUtils.isFastDoubleClick()) {
            LocationLog.i(TAG, "please retry later!!!")
            return
        }
        val latStr = latitude!!.text.toString()
        val longiStr = longitude!!.text.toString()
        val maxStr = maxResults!!.text.toString()
        if (!checkLatValid(latStr)) {
            LocationLog.e(TAG, "Latitude is illegal")
            return
        }
        if (!checkLongiValid(longiStr)) {
            LocationLog.e(TAG, "Longitude is illegal")
            return
        }
        if (!isInt(maxStr)) {
            LocationLog.e(TAG, "maxResults is illegal")
            return
        }
        val lat = latStr.toDouble()
        val longi = longiStr.toDouble()
        val maxResult: Int
        maxResult = try {
            maxStr.toInt()
        } catch (e: NumberFormatException) {
            LocationLog.e(TAG, "maxResults is illegal")
            return
        }
        val language = revGeoLanguage!!.text.toString()
        val country = revGeoCountry!!.text.toString()
        ExecutorUtil.getInstance().execute {
            val getFromLocationRequest =
                GetFromLocationRequest(lat, longi, maxResult)
            val locale = Locale(language, country)
            val geocoderService =
                LocationServices.getGeocoderService(this@GeocoderActivity, locale)
            geocoderService.getFromLocation(getFromLocationRequest)
                .addOnSuccessListener { hwLocation -> printGeocoderResult(hwLocation) }
                .addOnFailureListener { e ->
                    LocationLog.i(
                        TAG,
                        "getFromLocation fail:${e.message}"
                    )
                }
        }
    }

    private fun switchMode() {
        if (reverseGeoCodeLayout!!.isShown) {
            reverseGeoCodeLayout!!.visibility = View.GONE
            geoCodeLayout!!.visibility = View.VISIBLE
        } else {
            geoCodeLayout!!.visibility = View.GONE
            reverseGeoCodeLayout!!.visibility = View.VISIBLE
        }
    }

    private fun geoCoder() {
        if (NoDoubleClickUtils.isFastDoubleClick()) {
            LocationLog.i(TAG, "please retry later!!!")
            return
        }
        if (!isInt(geoResults!!.text.toString())) {
            LocationLog.e(TAG, "maxResults is illegal")
            return
        }
        val addressName = locationName!!.text.toString()
        val results: Int
        results = try {
            geoResults!!.text.toString().toInt()
        } catch (e: Exception) {
            LocationLog.e(TAG, "maxResults is illegal")
            return
        }
        val language = geoLanguage!!.text.toString()
        val country = geoCountry!!.text.toString()
        ExecutorUtil.getInstance().execute {
            val getFromLocationNameRequest =
                GetFromLocationNameRequest(addressName, results)
            if (checkLatValid(lowerLeftLatitude!!.text.toString())
                && checkLongiValid(lowerLeftLongitude!!.text.toString())
                && checkLatValid(upperRightLatitude!!.text.toString())
                && checkLongiValid(upperRightLongitude!!.text.toString())
            ) {
                val lowerLeftLat =
                    lowerLeftLatitude!!.text.toString().toDouble()
                val lowerLeftLng =
                    lowerLeftLongitude!!.text.toString().toDouble()
                val upperRightLat =
                    upperRightLatitude!!.text.toString().toDouble()
                val upperRightLng =
                    upperRightLongitude!!.text.toString().toDouble()
                getFromLocationNameRequest.lowerLeftLatitude = lowerLeftLat
                getFromLocationNameRequest.lowerLeftLongitude = lowerLeftLng
                getFromLocationNameRequest.upperRightLatitude = upperRightLat
                getFromLocationNameRequest.upperRightLongitude = upperRightLng
            }
            val locale = Locale(language, country)
            val geocoderService =
                LocationServices.getGeocoderService(this@GeocoderActivity, locale)
            geocoderService.getFromLocationName(getFromLocationNameRequest)
                .addOnSuccessListener(OnSuccessListener<List<HWLocation>?> { hwLocations ->
                    printGeocoderResult(
                        hwLocations
                    )
                })
                .addOnFailureListener { ex ->
                    LocationLog.i(
                        TAG,
                        "getFromLocationName fail:${ex.message}"
                    )
                }
        }
    }

    /**
     * Check whether a string is double using a regular expression.
     *
     * @param input String
     * @return Boolean value
     */
    private fun isDouble(input: String): Boolean {
        if (isInt(input)) {
            return true
        }
        val mer =
            Pattern.compile("^[+-]?[1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*$")
                .matcher(input)
        return mer.matches()
    }

    fun isInt(input: String?): Boolean {
        val mer =
            Pattern.compile("^[+-]?[0-9]+$").matcher(input)
        return mer.matches()
    }

    private fun checkLatValid(latitude: String): Boolean {
        return isDouble(latitude)
    }

    private fun checkLongiValid(longitude: String): Boolean {
        return isDouble(longitude)
    }


    private fun printGeocoderResult(geocoderResult: List<HWLocation>?) {
        if (geocoderResult == null || geocoderResult.isEmpty()) {
            LocationLog.i(TAG, GsonUtil.getInstance().toJson(geocoderResult))
        } else {
            for (hwLocation in geocoderResult) {
                val builder = StringBuilder()
                if (hwLocation != null) {
                    builder.append("Location:{latitude=")
                        .append(hwLocation.latitude)
                        .append(",longitude=")
                        .append(hwLocation.longitude)
                        .append(",countryName=")
                        .append(hwLocation.countryName)
                        .append(",countryCode=")
                        .append(hwLocation.countryCode)
                        .append(",state=")
                        .append(hwLocation.state)
                        .append(",city=")
                        .append(hwLocation.city)
                        .append(",county=")
                        .append(hwLocation.county)
                        .append(",street=")
                        .append(hwLocation.street)
                        .append(",featureName=")
                        .append(hwLocation.featureName)
                        .append(",postalCode=")
                        .append(hwLocation.postalCode)
                        .append(",phone=")
                        .append(hwLocation.phone)
                        .append(",url=")
                        .append(hwLocation.url)
                        .append(",extraInfo=")
                        .append(GsonUtil.getInstance().toJson(hwLocation.extraInfo))
                        .append("}" + System.lineSeparator())
                } else {
                    builder.append("Location:{}")
                }
                LocationLog.i(TAG, builder.toString())
            }
        }
    }
}