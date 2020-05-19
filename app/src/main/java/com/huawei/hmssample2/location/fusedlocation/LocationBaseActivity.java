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
package com.huawei.hmssample2.location.fusedlocation;

import com.huawei.hmssample2.R;
import com.huawei.logger.LocationLog;
import com.huawei.logger.LogFragment;
import com.huawei.logger.LoggerActivity;

import android.app.FragmentTransaction;

/**
 * add addLogFragment() method, if you want to show log on the screen
 * @author lwx877867
 * @since 2020-5-11
 */
public class LocationBaseActivity extends LoggerActivity {
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * print log on the screen
     */
    protected void addLogFragment() {
        final FragmentTransaction transaction = getFragmentManager().beginTransaction();
        final LogFragment fragment = new LogFragment();
        if(transaction != null) {
            transaction.replace(R.id.framelog, fragment);
            transaction.commit();
        }else {
            LocationLog.e("LocationBaseActivity", "addLogFragment error !");
        }
    }
}
