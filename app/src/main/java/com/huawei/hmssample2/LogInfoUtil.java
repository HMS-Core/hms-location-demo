
package com.huawei.hmssample2;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
import android.support.v4.app.ActivityCompat;
import android.telephony.CellInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

/**
 * 功能描述
 *
 * @author wWX863864
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
                // 从intent中获取设备
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // 判断是否配对过
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    String devicesStr = "[" + "Name: " + device.getName() + ", Address: " + device.getAddress()
                        + ", BondState: " + device.getBondState();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        devicesStr += ", Type: " + device.getType();
                    }
                    devicesStr += "]";
                    Log.i(TAG + "-Bluetooth", devicesStr);
                }
                // 搜索完成
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.i(TAG + "-Bluetooth", "end");
            }
        }
    };

    public synchronized static void getLogInfo(final Context context) {
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
        Log.i(TAG+ "-Bluetooth", "getBluetoothInfo start");
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Log.i(TAG + "-Bluetooth", "Bluetooth is not found");
            return;
        }
        // 判断是否打开蓝牙
        if (!mBluetoothAdapter.isEnabled()) {
            Log.i(TAG + "-Bluetooth", "Bluetooth is off");
            return;
        }
        // 获取已经配对的设备
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        // 判断是否有配对过的设备
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                // 遍历到列表中
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
        intent.addAction(BluetoothDevice.ACTION_FOUND); // 用BroadcastReceiver来取得搜索结果
        intent.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intent.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        if (isRegisterReceiverBle) {
            context.unregisterReceiver(broadcastReceiver);
        }
        context.registerReceiver(broadcastReceiver, intent);
        isRegisterReceiverBle = true;
        // 判断是否在搜索,如果在搜索，就取消搜索
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        // 开始搜索
        mBluetoothAdapter.startDiscovery();
    }

    /**
     * 判断定位服务是否开启
     *
     * @param context 上下文
     * @return true 表示开启
     */
    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
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
        // 获取位置管理服务
        if (ActivityCompat.checkSelfPermission(context,
            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "checkSelfPermission ACCESS_COARSE_LOCATION or ACCESS_FINE_LOCATION false：");
            return;
        }
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // 设置监听器，自动更新的最小时间为间隔N秒(1秒为1*1000，这样写主要为了方便)或最小位移变化超过N米
        // 实时获取位置提供者provider中的数据，一旦发生位置变化 立即通知应用程序locationListener
        locationManager.requestLocationUpdates("gps", 10000, 0, locationListener);
        // 监听卫星，statusListener为响应函数
        locationManager.addGpsStatusListener(new GpsStatus.Listener() {
            @Override
            public void onGpsStatusChanged(int event) {
                // 触发事件event
                switch (event) {
                    case GpsStatus.GPS_EVENT_STARTED:
                        break;
                    // 第一次定位时间
                    case GpsStatus.GPS_EVENT_FIRST_FIX:
                        break;
                    // 收到卫星信息，并调用DrawMap()函数，进行卫星信号解析并显示到屏幕上
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
            // 通过GPS获取位置，新的位置信息放在location中，调用updateToNewLocation函数显示位置信息
            Log.i(TAG, "Longitude:" + location.getLongitude() + ",Latitude:" + location.getLatitude());
        }

        // 当Provider不可用时调用下面的函数
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
        // 获取卫星信息，gpsStatus为卫星响应函数中获得的卫星GpsStatus类型变量
        if (ActivityCompat.checkSelfPermission(context,
            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG + "-GPS", "checkSelfPermission ACCESS_FINE_LOCATION false ");
            return;
        }
        GpsStatus gpsStatus = locationManager.getGpsStatus(null);
        Iterable<GpsSatellite> allSatellites = gpsStatus.getSatellites();
        // 遍历获取每颗卫星的信息
        for (GpsSatellite satellite : allSatellites) {
            Log.i(TAG + "-GPS",
                "[" + "mHasEphemeris=" + satellite.hasEphemeris() + ", mHasAlmanac=" + satellite.hasAlmanac()
                    + ", mPrn=" + satellite.getPrn() + ", mSnr=" + satellite.getSnr() + ", mElevation="
                    + satellite.getElevation() + ", mAzimuth=" + satellite.getAzimuth() + ']' + "\n");
        }
    }

    private static boolean isRegisterReceiver = false;

    /**
     * 获取当前连接的wifi名称
     *
     * @param context
     */
    private static void getWIFIInfo(Context context) {
        Log.i(TAG, "getWIFIInfo start");
        wifiMgr = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
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
        TelephonyManager tel = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (tel == null || tel.getAllCellInfo() == null) {
            Log.i(TAG + "-Cell", "getCellInfo fail TelephonyManager is null");
            return;
        }
        for (CellInfo cellInfo : tel.getAllCellInfo()) {
            Log.i(TAG + "-Cell", "[" + cellInfo.toString() + "]" + "\n");
        }
    }
}
