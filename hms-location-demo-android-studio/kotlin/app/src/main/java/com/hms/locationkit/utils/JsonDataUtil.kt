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

package com.hms.locationkit.utils

import android.content.Context
import com.hms.locationkit.logger.LocationLog
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.Charset

object JsonDataUtil {

        fun getJson(context: Context?, fileName: String?, isBr: Boolean?): String {
            val stringBuilder = StringBuilder()
            try {
                val assetManager = context?.assets
                val bf = BufferedReader(
                    InputStreamReader(
                        assetManager!!.open(fileName!!),
                        Charset.forName("UTF-8")
                    )
                )
                var line: String?
                while (bf.readLine().also { line = it } != null) {
                    stringBuilder.append(line)
                    if (isBr!!) {
                        stringBuilder.append("\n")
                    }
                }
            } catch (e: IOException) {
                LocationLog.e("JsonDataUtil", "getJson exception:${e.message}")
            }
            return stringBuilder.toString()
        }
    }
