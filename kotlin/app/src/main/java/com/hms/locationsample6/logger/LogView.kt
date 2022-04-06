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

package com.hms.locationsample6.logger

import android.app.Activity
import android.content.Context
import androidx.appcompat.widget.AppCompatEditText
import java.text.SimpleDateFormat
import java.util.*

class LogView(context: Context?) : AppCompatEditText(context!!), LogNode {
    private var mNext: LogNode? = null
    private var priorityStr: String? = null


    override fun println(priority: Int, tag: String?, msg: String?, tr: Throwable?) {

        when (priority) {
            LocationLog.DEBUG -> priorityStr = "D"
            LocationLog.INFO -> priorityStr = "I"
            LocationLog.WARN -> priorityStr = "W"
            LocationLog.ERROR -> priorityStr = "E"

        }
        val outputBuilder = StringBuilder()

        val formatter = SimpleDateFormat("HH:mm:ss", Locale.US)
        val curDate = Date(System.currentTimeMillis())
        val str = formatter.format(curDate)
        outputBuilder.append(str)
        outputBuilder.append(" ")
        outputBuilder.append(msg)
        outputBuilder.append("\r\n\n")
        (context as Activity).runOnUiThread(Thread(Runnable { appendToLog(outputBuilder.toString()) }))
        if (mNext != null) {
            mNext!!.println(priority, tag, msg, tr)
        }
    }

    private fun appendToLog(s: String) {
        append(""" $s """.trimIndent())
    }


}
