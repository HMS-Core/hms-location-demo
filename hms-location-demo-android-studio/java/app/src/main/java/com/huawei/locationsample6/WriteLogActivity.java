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
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import androidx.core.app.ActivityCompat;

import com.huawei.hms.location.LocationServices;
import com.huawei.hms.location.LogConfig;
import com.huawei.hms.location.SettingsClient;
import com.huawei.locationsample6.location.fusedlocation.LocationBaseActivity;
import com.huawei.logger.LocationLog;

import java.io.File;

public class WriteLogActivity extends LocationBaseActivity {
    private static final String TAG = "WriteLogActivity";

    private SettingsClient settingsClient;

    private EditText etFilePath;

    private EditText etFileCount;

    private EditText etFileSize;

    private EditText etFileExpire;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wirte_log);
        settingsClient = LocationServices.getSettingsClient(this);

        etFileCount = (EditText) findViewById(R.id.et_file_count);
        etFileSize = (EditText) findViewById(R.id.et_file_size);
        etFilePath = (EditText) findViewById(R.id.et_file_path);
        etFileExpire = (EditText) findViewById(R.id.et_file_expire);
        addLogFragment();
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            String[] strings = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(this, strings, 1);
        }
        String absolutePath = getSDPath(this);
        etFilePath.setText(absolutePath + "/log");
    }


    public void writeLog(View view) {
        LogConfig logConfig = new LogConfig();
        String fileSizeString = etFileSize.getText().toString().trim();
        String fileCountString = etFileCount.getText().toString().trim();
        String fileExpireString = etFileExpire.getText().toString().trim();
        String filePath = etFilePath.getText().toString().trim();
        int fileSize = 0;
        int fileCount = 0;
        int fileExpire = 0;
        try {
            if (!TextUtils.isEmpty(fileSizeString)) {
                fileSize = Integer.parseInt(fileSizeString);
            }
            if (!TextUtils.isEmpty(fileCountString)) {
                fileCount = Integer.parseInt(fileCountString);
            }
            if (!TextUtils.isEmpty(fileExpireString)) {
                fileExpire = Integer.parseInt(fileExpireString);
            }
            logConfig.setFileSize(fileSize);
            logConfig.setFileNum(fileCount);
            logConfig.setFileExpiredTime(fileExpire);
            logConfig.setLogPath(filePath);
            settingsClient.setLogConfig(logConfig).addOnFailureListener(e -> LocationLog.i(TAG, "setLogConfig onFailure:" + e.getMessage()));
            if (isLogFilePath(filePath)) {
                LocationLog.i(TAG, "setLogConfig success");
            }
        } catch (NumberFormatException e) {
            LocationLog.e(TAG, "setLogConfig onFailure:" + e.getMessage());
        }
    }

    private boolean isLogFilePath(String logPath) {
        File folder = new File(logPath);
        return folder.exists();
    }

    public static String getSDPath(Context context) {
        File sdDir = null;
        // Check whether the SD card exists.
        boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            if (Build.VERSION.SDK_INT >= 29) {
                // Android10
                sdDir = context.getExternalFilesDir(null);
            } else {
                sdDir = Environment.getExternalStorageDirectory();
            }
        } else {
            sdDir = Environment.getRootDirectory();
        }
        return sdDir.toString();
    }
}
