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

class LocationLog {

    companion object {
        const val DEBUG = Log.DEBUG
        const val INFO = Log.INFO
        const val WARN = Log.WARN
        const val ERROR = Log.ERROR
        private var mLogNode: LogNode? = null
        fun setLogNode(node: LogNode?) {
            mLogNode = node
        }

        fun d(tag: String?, msg: String?, tr: Throwable?) {
            println(DEBUG, tag, msg, tr)
        }

        fun d(tag: String?, msg: String?) {
            d(tag, msg, null)
        }

        fun i(tag: String?, msg: String?, tr: Throwable?) {
            println(INFO, tag, msg, tr)
        }

        fun i(tag: String?, msg: String?) {
            i(tag, msg, null)
        }

        fun w(tag: String?, msg: String?, tr: Throwable?) {
            println(WARN, tag, msg, tr)
        }

        fun w(tag: String?, msg: String?) {
            w(tag, msg, null)
        }

        fun e(tag: String?, msg: String?, tr: Throwable?) {
            println(ERROR, tag, msg, tr)
        }

        fun e(tag: String?, msg: String?) {
            e(tag, msg, null)
        }

        private fun println(
            priority: Int,
            tag: String?,
            msg: String?,
            tr: Throwable?
        ) {
            if (mLogNode != null) {
                mLogNode!!.println(priority, tag, msg, tr)
            }
        }


    }
}