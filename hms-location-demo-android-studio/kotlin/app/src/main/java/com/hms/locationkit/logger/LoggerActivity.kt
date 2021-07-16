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

package com.hms.locationkit.logger

import androidx.appcompat.app.AppCompatActivity
import com.hms.locationkit.R

open class LoggerActivity : AppCompatActivity() {

    override fun onStart() {
        super.onStart()
        initializeLogging()
    }

    private fun initializeLogging() {
        val logFragment: LogFragment =
            supportFragmentManager.findFragmentById(R.id.framelog) as LogFragment

        val logcat = LogCatWrapper()
        logcat.setNext(logFragment.getLogView())
        LocationLog.setLogNode(logcat)
    }
}