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

import android.util.Log

class LogCatWrapper : LogNode {
    private var mNext: LogNode? = null
    fun setNext(node: LogNode?) {
        mNext = node
    }

    override fun println(
        priority: Int,
        tag: String?,
        msg: String?,
        tr: Throwable?
    ) {
        var useMsg = msg
        if (useMsg == null) {
            useMsg = ""
        }
        if (tr != null) {
            useMsg += """ ${Log.getStackTraceString(tr)} """.trimIndent()
        }
        Log.println(priority, tag, useMsg)
        if (mNext != null) {
            mNext?.println(priority, tag, msg, tr)
        }
    }
}
