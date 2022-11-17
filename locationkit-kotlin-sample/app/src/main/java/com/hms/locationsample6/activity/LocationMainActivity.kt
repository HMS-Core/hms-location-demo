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

package com.hms.locationsample6.activity

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.checkSelfPermission
import com.hms.locationsample6.R
import com.hms.locationsample6.fusedlocation.*
import com.hms.locationsample6.geofence.OperateGeoFenceActivity
import com.hms.locationsample6.useractivity.ActivityConversionActivity
import com.hms.locationsample6.useractivity.ActivityIdentificationActivity
import com.hms.locationsample6.utils.RequestPermission

class LocationMainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "LocationMainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.location_main_activity)

        //You must have the ACCESS_COARSE_LOCATION or ACCESS_FINE_LOCATION permission. Otherwise, the location service is unavailable.

        // check location permission
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            Log.i(TAG, "sdk < 28 Q")
            if (checkSelfPermission(
                    this,
                    ACCESS_FINE_LOCATION
                ) != PERMISSION_GRANTED
                && checkSelfPermission(
                    this,
                    ACCESS_COARSE_LOCATION
                ) != PERMISSION_GRANTED
            ) {
                val strings = arrayOf(
                    ACCESS_FINE_LOCATION,
                    ACCESS_COARSE_LOCATION
                )
                ActivityCompat.requestPermissions(this, strings, 1)
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (checkSelfPermission(
                    this,
                    ACCESS_FINE_LOCATION
                ) != PERMISSION_GRANTED
                && checkSelfPermission(
                    this,
                    ACCESS_COARSE_LOCATION
                ) != PERMISSION_GRANTED
            ) {
                val strings = arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION)
                ActivityCompat.requestPermissions(this, strings, 3)
            } else {
                RequestPermission.requestBackgroundPermission(this)
            }
        } else {
            if (checkSelfPermission(
                    this,
                    ACCESS_FINE_LOCATION
                ) != PERMISSION_GRANTED && checkSelfPermission(
                    this,
                    ACCESS_COARSE_LOCATION
                ) != PERMISSION_GRANTED && checkSelfPermission(
                    this,
                    "android.permission.ACCESS_BACKGROUND_LOCATION"
                ) != PERMISSION_GRANTED
            ) {
                val strings = arrayOf(
                    ACCESS_FINE_LOCATION,
                    ACCESS_COARSE_LOCATION,
                    "android.permission.ACCESS_BACKGROUND_LOCATION"
                )
                ActivityCompat.requestPermissions(this, strings, 2)
            }
        }
    }

    /**
     * click listener here!
     * Base on Click your activity  RequestLocationUpdatesWithCallbackActivity
     * will start
     */
    fun fuseLocation(view: View) {
        Log.d("click", "ButtonClick  {$view}")
        startActivity(Intent(this, RequestLocationUpdatesWithCallbackActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        })
    }

    /**
     *  click listener setMockLocation!
     * Base on Click your activity SetMockLocationActivity
     * will start
     */
    fun setMockLocation(view: View) {
        Log.d("click", "ButtonClick  {$view}")
        startActivity(Intent(this, SetMockLocationActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        })
    }

    /**
     * click listener setMockMODE!
     * Base on Click your activity SetMockModeActivity
     * will start
     */
    fun setMockMode(view: View) {
        Log.d("click", "ButtonClick  {$view}")
        startActivity(Intent(this, SetMockModeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        })
    }

    /**
     * click listener geoFenceLocation!
     * Base on Click your activity OperateGeoFenceActivity
     * will start
     */
    fun geoFenceLocation(view: View) {
        Log.d("click", "ButtonClick  {$view}")
        startActivity(Intent(this, OperateGeoFenceActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        })
    }

    fun requestLocationUpdatesWithIntent(view: View) {
        Log.d("click", "ButtonClick  {$view}")
        startActivity(Intent(this, RequestLocationUpdateWithIntentActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        })
    }

    /**
     * click listener locationHD!
     * Base on Click your activity RequestLocationUpdatesHDWithCallbackActivity
     * will start
     */
    fun locationHD(view: View) {
        Log.d("click", "ButtonClick  {$view}")
        startActivity(Intent(this, RequestLocationUpdatesHDWithCallbackActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        })
    }

    /**
     * click listener lastLocation!
     * Base on Click your activity GetLastLocationActivity
     * will start
     */
    fun lastLocation(view: View) {
        Log.d("click", "ButtonClick  {$view}")
        startActivity(Intent(this, GetLastLocationActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        })
    }

    /**
     * click listener locationAvailability!
     * Base on Click your activity GetLocationAvailabilityActivity
     * will start
     */
    fun locationAvailability(view: View) {
        Log.d("click", "ButtonClick  {$view}")
        startActivity(Intent(this, GetLocationAvailabilityActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        })
    }

    /**
     * click listener locationTransitionUpdate!
     * Base on Click your activity ActivityConversionActivity
     * will start
     */
    fun activityTransitionUpdate(view: View) {
        Log.d("click", "ButtonClick  {$view}")
        startActivity(Intent(this, ActivityConversionActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        })

    }

    /**
     * click listener locationTransitionUpdate!
     * Base on Click your activity NavigationContextStateActivity
     * will start
     */
    fun GetNavigationContextState(view: View) {
        Log.d("click", "ButtonClick  {$view}")
        startActivity(Intent(this, NavigationContextStateActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        })

    }

    /**
     * click listener locationActivityUpdate!
     * Base on Click your activity ActivityIdentificationActivity
     * will start
     */
    fun activityUpdate(view: View) {
        Log.d("click", "ButtonClick  {$view}")
        startActivity(Intent(this, ActivityIdentificationActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        })
    }

    fun checkSettings(view: View) {
        Log.d("click", "ButtonClick  {$view}")
        startActivity(Intent(this, CheckSettingsActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        })
    }

    /**
     * click listener setLogConfig
     * Base on Click your activity WriteLogActivity
     * will start
     */
    fun writeLog(view: View) {
        Log.d("click", "ButtonClick  {$view}")
        startActivity(Intent(this, WriteLogActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        })
    }

    fun coordinateConverter(view: View) {
        Log.d("click", "ButtonClick  {$view}")
        startActivity(Intent(this, CoordinateConverterActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.size > 1 && grantResults[0] == PERMISSION_GRANTED && grantResults[1] == PERMISSION_GRANTED) {
                Log.i(TAG, "onRequestPermissionsResult: apply LOCATION PERMISSION successful")
            } else {
                Log.i(TAG, "onRequestPermissionsResult: apply LOCATION PERMISSION  failed")
            }
        }
        if (requestCode == 2) {
            if (grantResults.size > 2 && grantResults[2] == PERMISSION_GRANTED && grantResults[0] == PERMISSION_GRANTED && grantResults[1] == PERMISSION_GRANTED
            ) {
                Log.i(
                    TAG,
                    "onRequestPermissionsResult: apply ACCESS_BACKGROUND_LOCATION successful"
                )
            } else {
                Log.i(TAG, "onRequestPermissionsResult: apply ACCESS_BACKGROUND_LOCATION  failed")
            }
        }
        if (requestCode == 3) {
            if (grantResults.isNotEmpty() && grantResults[0] == PERMISSION_GRANTED
                || grantResults[1] == PERMISSION_GRANTED
            ) {
                Log.i(
                    TAG,
                    "onRequestPermissionsResult: apply LOCATION PERMISSION successful"
                )
                RequestPermission.requestBackgroundPermission(this)
            } else {
                Log.i(
                    TAG,
                    "onRequestPermissionsResult: apply LOCATION PERMISSSION  failed"
                )
            }
        }
    }
}