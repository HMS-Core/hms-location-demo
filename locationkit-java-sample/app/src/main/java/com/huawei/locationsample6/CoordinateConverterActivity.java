/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.locationsample6;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.huawei.locationsample6.location.fusedlocation.LocationBaseActivity;
import com.huawei.locationsample6.util.CoordinateConverterUtil;

/**
 * 功能描述 坐标转换测试
 *
 * @since 2022-08-22
 */
public class CoordinateConverterActivity extends LocationBaseActivity implements View.OnClickListener {
    private static final String LAT_DEFAULT = "31.196055890098226";

    private static final String LON_DEFAULT = "121.31340512699686";

    private EditText etLon;

    private EditText etLat;

    private EditText etTransformType;

    private TextView tvResultShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coordinate_converter);
        findViewById(R.id.do_transform).setOnClickListener(this);
        etLon = (EditText) findViewById(R.id.et_lon);
        etLat = (EditText) findViewById(R.id.et_lat);
        etTransformType = (EditText) findViewById(R.id.et_transform_type);
        tvResultShow = (TextView) findViewById(R.id.tv_result_show);
        addLogFragment();
        etLat.setText(LAT_DEFAULT);
        etLon.setText(LON_DEFAULT);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.do_transform) {
            tvResultShow.setText("");
            change();
        }
    }

    private void change() {
        StringBuffer buffer = new StringBuffer(0);
        double lat = 0.0d;
        double lon = 0.0d;
        int type = 0;
        try {
            if (!TextUtils.isEmpty(etLat.getText().toString().trim())) {
                lat = Double.valueOf(etLat.getText().toString().trim());
            }
            if (!TextUtils.isEmpty(etLon.getText().toString().trim())) {
                lon = Double.valueOf(etLon.getText().toString().trim());
            }
            if (!TextUtils.isEmpty(etTransformType.getText().toString().trim())) {
                type = Integer.parseInt(etTransformType.getText().toString().trim());
            }
            String change = CoordinateConverterUtil.change(lat, lon, type);
            buffer.append(change);
            buffer.append(System.lineSeparator());
        } catch (NumberFormatException e) {
            buffer.append(e.getMessage());
            buffer.append(System.lineSeparator());
            buffer.append("Replace the parameter with the correct one.");
        }
        tvResultShow.setText(buffer);
    }
}
