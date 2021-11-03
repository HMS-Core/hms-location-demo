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

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.core.app.ActivityCompat
import com.hms.locationsample6.R
import com.hms.locationsample6.logger.LocationLog
import com.huawei.hms.location.LocationServices
import com.huawei.hms.location.LogConfig
import com.huawei.hms.location.SettingsClient
import kotlinx.android.synthetic.main.activity_wirte_log.*
import java.io.File

/**
 * WriteLogActivity
 *
 * @since 2021/10/15
 */
class WriteLogActivity : BaseActivity(), View.OnClickListener {
    private val TAG = "WriteLogActivity"

    private var settingsClient: SettingsClient? = null

    private var etFilePath: EditText? = null

    private var etFileCount: EditText? = null

    private var etFileSize: EditText? = null

    private var etFileExpire: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wirte_log)
        set_log_config?.setOnClickListener(this)
        settingsClient = LocationServices.getSettingsClient(this)
        etFileCount = findViewById<EditText>(R.id.et_file_count)
        etFileSize = findViewById<EditText>(R.id.et_file_size)
        etFilePath = findViewById<EditText>(R.id.et_file_path)
        etFileExpire = findViewById<EditText>(R.id.et_file_expire)
        addLogFragment()
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val strings = arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            ActivityCompat.requestPermissions(this, strings, 1)
        }
        val absolutePath = getSDPath(this)
        etFilePath!!.setText("$absolutePath/log")
    }

    override fun onClick(v: View?) {
        try {
            when (v?.id) {
                R.id.set_log_config -> writeLog()
            }
        } catch (e: Exception) {
            Log.e(TAG, "setMockLocation Exception:$e")
        }
    }

    private fun writeLog() {
        val logConfig = LogConfig()
        val fileSizeString = etFileSize!!.text.toString().trim { it <= ' ' }
        val fileCountString = etFileCount!!.text.toString().trim { it <= ' ' }
        val fileExpireString = etFileExpire!!.text.toString().trim { it <= ' ' }
        val filePath = etFilePath!!.text.toString().trim { it <= ' ' }
        var fileSize = 0
        var fileCount = 0
        var fileExpire = 0
        try {
            if (!TextUtils.isEmpty(fileSizeString)) {
                fileSize = fileSizeString.toInt()
            }
            if (!TextUtils.isEmpty(fileCountString)) {
                fileCount = fileCountString.toInt()
            }
            if (!TextUtils.isEmpty(fileExpireString)) {
                fileExpire = fileExpireString.toInt()
            }
            logConfig.fileSize = fileSize
            logConfig.fileNum = fileCount
            logConfig.fileExpiredTime = fileExpire
            logConfig.logPath = filePath
            settingsClient!!.setLogConfig(logConfig).addOnFailureListener { e ->
                LocationLog.i(
                    TAG,
                    "setLogConfig onFailure:" + e.message
                )
            }
            if (isLogFilePath(filePath)) {
                LocationLog.i(TAG, "setLogConfig success")
            }
        } catch (e: NumberFormatException) {
            LocationLog.e(TAG, "setLogConfig onFailure:" + e.message)
        }
    }

    private fun isLogFilePath(logPath: String): Boolean {
        val folder = File(logPath)
        return folder.exists()
    }

    fun getSDPath(context: Context): String? {
        var sdDir: File? = null
        // Check whether the SD card exists.
        val sdCardExist =
            Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
        sdDir = if (sdCardExist) {
            if (Build.VERSION.SDK_INT >= 29) {
                // Android10
                context.getExternalFilesDir(null)
            } else {
                Environment.getExternalStorageDirectory()
            }
        } else {
            Environment.getRootDirectory()
        }
        return sdDir.toString()
    }


}