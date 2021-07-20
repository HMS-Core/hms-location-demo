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

package com.hms.locationsample6.utils

object Utils {

        const val ACTION_PROCESS_LOCATION =
            "com.hms.locationkit.activity.location.ACTION_PROCESS_LOCATION"

        const val TAG = "LocationReceiver"

        var isListenActivityIdentification = false

        var isListenActivityConversion = false

        fun addConversionListener() {
            this.isListenActivityConversion = true
        }
        fun removeConversionListener() {
            this.isListenActivityConversion = false
        }

        fun addIdentificationListener() {
            this.isListenActivityIdentification = true
        }

        fun removeIdentificationListener() {
            this.isListenActivityIdentification = false
        }
}