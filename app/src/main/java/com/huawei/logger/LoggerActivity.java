/*
*       Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.

        Licensed under the Apache License, Version 2.0 (the "License");
        you may not use this file except in compliance with the License.
        You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

        Unless required by applicable law or agreed to in writing, software
        distributed under the License is distributed on an "AS IS" BASIS,
        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        See the License for the specific language governing permissions and
        limitations under the License.
*/

package com.huawei.logger;

import com.huawei.hmssample2.R;

import android.app.Activity;

/**
 * the activity is used to attach LogFragment
 * @author xxx888888
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
        LogFragment logFragment = (LogFragment) getFragmentManager().findFragmentById(R.id.framelog);
        LogCatWrapper logcat = new LogCatWrapper();
        logcat.setNext(logFragment.getLogView());
        LocationLog.setLogNode(logcat);
    }
}
