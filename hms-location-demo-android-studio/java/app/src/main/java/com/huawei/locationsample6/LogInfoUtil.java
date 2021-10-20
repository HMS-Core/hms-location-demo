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
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.telephony.CellInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

/**
 * LogInfoUtil
 *
 * @since 2020-07-29
 */
public class LogInfoUtil {
    public static final String TAG = "LogInfoUtil";

    private static LocationManager locationManager;

    private static WifiManager wifiMgr;

    private static BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {
            boolean success = true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);
            }
            if (success) {
                List<ScanResult> scanResults = wifiMgr.getScanResults();
                if (scanResults == null || scanResults.isEmpty()) {
                    Log.e(TAG + "-WIFI", "wifi scan result null");
                    return;
                }
                WifiInfo info = wifiMgr.getConnectionInfo();
                StringBuilder currentWifiInfo = new StringBuilder();
                if (info != null && !TextUtils.isEmpty(info.getBSSID())) {
                    long mac = 0;
                    if (!TextUtils.isEmpty(info.getBSSID())) {
                        mac = Long.parseLong(new BigInteger(info.getBSSID().replace(":", ""), 16).toString());
                    }
                    currentWifiInfo.append("[Current Connect wifi: ")
                        .append(info.toString())
                        .append(", BSSID: ")
                        .append(info.getBSSID())
                        .append(", MAC: ")
                        .append(mac)
                        .append(".]")
                        .append("\n");
                } else {
                    currentWifiInfo.append("Current Connect wifi is null. ").append("\n");
                }
                Log.i(TAG + "-WIFI", currentWifiInfo.toString());
                for (ScanResult result : scanResults) {
                    long mac = 0;
                    if (!TextUtils.isEmpty(result.BSSID)) {
                        mac = Long.parseLong(new BigInteger(result.BSSID.replace(":", ""), 16).toString());
                    }
                    Log.i(TAG + "-WIFI", "[" + result.toString() + ", MAC: " + mac + "]" + "\n");
                }
            }
        }
    };

    private static BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Obtain the device from the intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Determine whether pairing has been performed.
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    String devicesStr = "[" + "Name: " + device.getName() + ", Address: " + device.getAddress()
                        + ", BondState: " + device.getBondState();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        devicesStr += ", Type: " + device.getType();
                    }
                    devicesStr += "]";
                    Log.i(TAG + "-Bluetooth", devicesStr);
                }
                // Search Complete.
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.i(TAG + "-Bluetooth", "end");
            } else {
                Log.i(TAG + "-Bluetooth", "receive");
            }
        }
    };

    public static synchronized void getLogInfo(final Context context) {
        Log.i(TAG, "getLogInfo start");
        if (!isLocationEnabled(context)) {
            Log.i(TAG, "isLocationEnabled is false");
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                try {
                    getWIFIInfo(context);
                    getCellInfo(context);
                    getGPSInfo(context);
                    getBluetoothInfo(context);
                    Looper.loop();
                } catch (Exception e) {
                    Log.e(TAG, "getLogInfo error : " + e.getMessage());
                }
            }
        }).start();
    }

    public static void removeLogInfo() {
        removeLocation();
    }

    private static boolean isRegisterReceiverBle = false;

    private static void getBluetoothInfo(Context context) {
        Log.i(TAG + "-Bluetooth", "getBluetoothInfo start");
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Log.i(TAG + "-Bluetooth", "Bluetooth is not found");
            return;
        }
        // Determine whether to enable Bluetooth.
        if (!mBluetoothAdapter.isEnabled()) {
            Log.i(TAG + "-Bluetooth", "Bluetooth is off");
            return;
        }
        // Obtaining Paired Devices.
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        // Check whether there are paired devices.
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                // Traverse to the list
                StringBuilder pairedDevicesStr = new StringBuilder("getBondedDevices: [");
                pairedDevicesStr.append("Name: ")
                    .append(device.getName())
                    .append(", Address: ")
                    .append(device.getAddress())
                    .append(", BondState: ")
                    .append(device.getBondState());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    pairedDevicesStr.append(", Type: ").append(device.getType());
                }
                pairedDevicesStr.append("]").append("\n");
                Log.i(TAG + "-Bluetooth", pairedDevicesStr.toString());
            }
        }
        IntentFilter intent = new IntentFilter();
        intent.addAction(BluetoothDevice.ACTION_FOUND); // Use BroadcastReceiver to get search results
        intent.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intent.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        if (isRegisterReceiverBle) {
            context.unregisterReceiver(broadcastReceiver);
        }
        context.registerReceiver(broadcastReceiver, intent);
        isRegisterReceiverBle = true;
        // Determines whether the search is in progress. If the search is in progress, cancels the search.
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        // Start Search
        mBluetoothAdapter.startDiscovery();
    }

    /**
     * Check whether the location service is enabled.
     *
     * @param context context
     * @return true enabled
     */
    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                return false;
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } else {
            locationProviders =
                Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    private static void getGPSInfo(final Context context) {
        Log.i(TAG + "-GPS", "getGPSInfo start");
        // Obtaining the Location Management Service
        if (ActivityCompat.checkSelfPermission(context,
            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "checkSelfPermission ACCESS_COARSE_LOCATION or ACCESS_FINE_LOCATION false：");
            return;
        }
        if (context.getSystemService(Context.LOCATION_SERVICE) instanceof LocationManager) {
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }
        // Set the minimum interval for automatic update of the listener to N seconds. (1 second is 1 x 1000, which is
        // mainly for convenience.) or the minimum displacement change exceeds N m
        // Obtains data from the location provider in real time and notifies the application locationListener once the
        // location changes.
        locationManager.requestLocationUpdates("gps", 10000, 0, locationListener);
        // Satellite listening. The statusListener is the response function.
        locationManager.addGpsStatusListener(new GpsStatus.Listener() {
            @Override
            public void onGpsStatusChanged(int event) {
                // Triggering event event
                switch (event) {
                    case GpsStatus.GPS_EVENT_STARTED:
                        break;
                    // First Positioning Time
                    case GpsStatus.GPS_EVENT_FIRST_FIX:
                        break;
                    // Receives satellite information, invokes the DrawMap() function, parses satellite signals, and
                    // displays them on the screen.
                    case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                        logGPSResult(context);
                        break;
                    case GpsStatus.GPS_EVENT_STOPPED:
                        break;
                }
            }
        });
        Log.i(TAG, "getGPSInfo end");
    }

    private static void removeLocation() {
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }
    }

    private static LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            // Obtain the location through GPS, place the new location information in location, and invoke the
            // updateToNewLocation function to display the location information.
            Log.i(TAG, "Longitude:" + location.getLongitude() + ",Latitude:" + location.getLatitude());
        }

        // Call the following function when the provider is unavailable
        @Override
        public void onProviderDisabled(String arg0) {
        }

        @Override
        public void onProviderEnabled(String arg0) {
        }

        @Override
        public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
        }
    };

    private static void logGPSResult(Context context) {
        // Obtains satellite information. gpsStatus is the satellite GpsStatus variable obtained in the satellite
        // response function.
        if (ActivityCompat.checkSelfPermission(context,
            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG + "-GPS", "checkSelfPermission ACCESS_FINE_LOCATION false ");
            return;
        }
        GpsStatus gpsStatus = locationManager.getGpsStatus(null);
        Iterable<GpsSatellite> allSatellites = gpsStatus.getSatellites();
        // Traversal to obtain information about each satellite
        for (GpsSatellite satellite : allSatellites) {
            Log.i(TAG + "-GPS",
                "[" + "mHasEphemeris=" + satellite.hasEphemeris() + ", mHasAlmanac=" + satellite.hasAlmanac()
                    + ", mPrn=" + satellite.getPrn() + ", mSnr=" + satellite.getSnr() + ", mElevation="
                    + satellite.getElevation() + ", mAzimuth=" + satellite.getAzimuth() + ']' + "\n");
        }
    }

    private static boolean isRegisterReceiver = false;

    /**
     * Obtains the Wi-Fi name of the current connection.
     *
     * @param context context
     */
    private static void getWIFIInfo(Context context) {
        Log.i(TAG, "getWIFIInfo start");
        if (context.getApplicationContext().getSystemService(Context.WIFI_SERVICE) instanceof WifiInfo) {
            wifiMgr = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        }
        if (wifiMgr == null) {
            Log.i(TAG + "-WIFI", "WifiManager is null");
            return;
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        if (isRegisterReceiver) {
            context.unregisterReceiver(wifiScanReceiver);
        }
        context.registerReceiver(wifiScanReceiver, intentFilter);
        isRegisterReceiver = true;
        boolean success = wifiMgr.startScan();
        Log.i(TAG + "-WIFI", "wifi scan result is " + success);
    }

    private static void getCellInfo(Context context) {
        Log.i(TAG + "-Cell", "getCellInfo start");
        if (ActivityCompat.checkSelfPermission(context,
            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG + "-Cell", "checkSelfPermission ACCESS_COARSE_LOCATION false ");
            return;
        }
        TelephonyManager tel = null;
        if (context.getSystemService(Context.TELEPHONY_SERVICE) instanceof TelephonyManager) {
            tel = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        }
        if (tel == null || tel.getAllCellInfo() == null) {
            Log.i(TAG + "-Cell", "getCellInfo fail TelephonyManager is null");
            return;
        }
        for (CellInfo cellInfo : tel.getAllCellInfo()) {
            Log.i(TAG + "-Cell", "[" + cellInfo.toString() + "]" + "\n");
        }
    }
}