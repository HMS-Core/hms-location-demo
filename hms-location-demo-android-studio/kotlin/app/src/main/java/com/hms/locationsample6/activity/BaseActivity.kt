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

import android.graphics.Color
import android.text.InputType
import android.text.TextUtils
import android.util.Log
import android.widget.EditText
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import com.hms.locationsample6.R
import com.hms.locationsample6.logger.LocationLog
import com.hms.locationsample6.logger.LogFragment
import com.hms.locationsample6.logger.LoggerActivity
import org.json.JSONException
import org.json.JSONObject
import java.lang.reflect.InvocationTargetException
import java.util.*

open class BaseActivity : LoggerActivity() {
    companion object {
        private const val TAG = "BaseActivity"
    }

    protected fun addLogFragment() {
        val fragment = LogFragment()
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.framelog, fragment)
            commit()
        }
    }

    protected open fun initDataDisplayView(tableLayout: TableLayout, json: String) {
        try {
            val jsonObject = JSONObject(json)
            val iterator: Iterator<*> = jsonObject.keys()
            while (iterator.hasNext()) {
                val key = iterator.next() as String
                val value = jsonObject.getString(key)
                val textView = TextView(baseContext).apply {
                    text = key
                    setTextColor(Color.GRAY)
                    id = baseContext.resources.getIdentifier(
                        key + "_key",
                        "id",
                        baseContext.packageName
                    )
                }

                val editText = EditText(baseContext).apply {
                    setText(value)
                    id = baseContext.resources.getIdentifier(
                        key + "_value",
                        "id",
                        baseContext.packageName
                    )
                    setTextColor(Color.DKGRAY)
                    if (key != "isFastestIntervalExplicitlySet" && key != "needAddress"
                        && key != "language" && key != "countryCode" && key != "alwaysShow" && key != "needBle"
                    ) {
                        inputType = InputType.TYPE_CLASS_NUMBER
                    }

                }
                val tableRow = TableRow(baseContext).apply {
                    addView(textView)
                    addView(editText)
                }
                tableLayout.addView(tableRow)
            }
        } catch (e: JSONException) {
            Log.e(TAG, "initDataDisplayView JSONException:" + e.message)
        }
    }


    fun handleData(request: Any, json: String) {
        try {
            val classObject: Class<*> = request.javaClass
            val methods = classObject.methods
            val jsonObject = JSONObject(json)
            val iterator: Iterator<*> = jsonObject.keys()
            while (iterator.hasNext()) {
                val key = iterator.next() as String
                for (method in methods) {
                    if (("set" + key.substring(0, 1)
                            .toUpperCase(Locale.ROOT) + key.substring(1)) == method.name
                    ) {
                        val paramTypes: Array<Class<*>> = method.parameterTypes
                        var paramType = ""
                        for (class2 in paramTypes) {
                            paramType = class2.name
                        }
                        val editText = findViewById<EditText>(
                            baseContext.resources.getIdentifier(
                                key + "_value",
                                "id",
                                baseContext.packageName
                            )
                        )
                        val editTextValue = editText.text.toString()
                        if (TextUtils.isEmpty(editTextValue)) {
                            break
                        }
                        var invokeValue: Any?
                        when (paramType) {
                            "int" -> {
                                invokeValue = editTextValue.toInt()
                            }
                            "long" -> {
                                invokeValue = editTextValue.toLong()
                            }
                            "float" -> {
                                invokeValue = editTextValue.toFloat()
                            }
                            "double" -> {
                                invokeValue = editTextValue.toDouble()
                            }
                            "boolean" -> {
                                invokeValue = editTextValue.toBoolean()
                            }
                            else -> {
                                invokeValue = editTextValue
                            }
                        }
                        method.invoke(request, invokeValue)
                    }

                }
            }
        } catch (e: JSONException) {
            LocationLog.e(TAG, "handleData JSONException:" + e.message)
        } catch (e: IllegalAccessException) {
            LocationLog.e(TAG, "handleData IllegalAccessException:" + e.message)
        } catch (e: InvocationTargetException) {
            LocationLog.e(TAG, "handleData InvocationTargetException:" + e.message)
        }
    }


}