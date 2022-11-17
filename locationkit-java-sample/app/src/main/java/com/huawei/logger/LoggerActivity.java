/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.huawei.logger;

import android.app.Activity;

import com.huawei.locationsample6.R;

/**
 * the activity is used to attach LogFragment
 *
 * @since 2020-5-11
 */
public class LoggerActivity extends Activity {
    @Override
    protected void onStart() {
        super.onStart();
        initializeLogging();
    }

    /**
     * initialize log info
     */
    private void initializeLogging() {
        LogFragment logFragment = null;
        if (getFragmentManager().findFragmentById(R.id.framelog) instanceof LogFragment) {
            logFragment = (LogFragment) getFragmentManager().findFragmentById(R.id.framelog);
        }

        LogCatWrapper logcat = new LogCatWrapper();
        if (logFragment != null) {
            logcat.setNext(logFragment.getLogView());
        }
        LocationLog.setLogNode(logcat);
    }
}
