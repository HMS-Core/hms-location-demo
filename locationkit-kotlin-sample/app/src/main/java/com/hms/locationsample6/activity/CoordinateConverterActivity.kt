/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */
package com.hms.locationsample6.activity

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.hms.locationsample6.R
import com.hms.locationsample6.utils.CoordinateConverterUtil.change

/**
 * 功能描述 坐标转换测试
 *
 * @since 2022-08-22
 */
class CoordinateConverterActivity : BaseActivity(),
    View.OnClickListener {
    private var etLon: EditText? = null
    private var etLat: EditText? = null
    private var etTransformType: EditText? = null
    private var tvResultShow: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coordinate_converter)
        findViewById<View>(R.id.do_transform).setOnClickListener(
            this
        )
        etLon = findViewById<View>(R.id.et_lon) as EditText
        etLat = findViewById<View>(R.id.et_lat) as EditText
        etTransformType =
            findViewById<View>(R.id.et_transform_type) as EditText
        tvResultShow =
            findViewById<View>(R.id.tv_result_show) as TextView
        addLogFragment()
        etLat!!.setText(LAT_DEFAULT)
        etLon!!.setText(LON_DEFAULT)
    }

    override fun onClick(view: View) {
        if (view.id == R.id.do_transform) {
            tvResultShow!!.text = ""
            change()
        }
    }

    private fun change() {
        val buffer = StringBuffer(0)
        var lat = 0.0
        var lon = 0.0
        var type = 0
        try {
            if (!TextUtils.isEmpty(etLat!!.text.toString().trim { it <= ' ' })) {
                lat = java.lang.Double.valueOf(etLat!!.text.toString().trim { it <= ' ' })
            }
            if (!TextUtils.isEmpty(etLon!!.text.toString().trim { it <= ' ' })) {
                lon = java.lang.Double.valueOf(etLon!!.text.toString().trim { it <= ' ' })
            }
            if (!TextUtils.isEmpty(etTransformType!!.text.toString().trim { it <= ' ' })) {
                type = etTransformType!!.text.toString().trim { it <= ' ' }.toInt()
            }
            val change = change(lat, lon, type)
            buffer.append(change)
            buffer.append(System.lineSeparator())
        } catch (e: NumberFormatException) {
            buffer.append(e.message)
            buffer.append(System.lineSeparator())
            buffer.append("Replace the parameter with the correct one.")
        }
        tvResultShow!!.text = buffer
    }

    companion object {
        private const val LAT_DEFAULT = "31.196055890098226"
        private const val LON_DEFAULT = "121.31340512699686"
    }
}