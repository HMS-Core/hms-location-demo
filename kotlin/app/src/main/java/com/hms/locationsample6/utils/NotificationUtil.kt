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

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.hms.locationsample6.R

object NotificationUtil {
    const val NOTIFICATION_ID = 1

    fun getNotification(context: Context): Notification? {
        val builder: Notification.Builder
        val notification: Notification
        builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channelId = context.packageName
            val notificationChannel =
                NotificationChannel(channelId, "LOCATION", NotificationManager.IMPORTANCE_LOW)
            notificationManager.createNotificationChannel(notificationChannel)
            Notification.Builder(context, channelId)
        } else {
            Notification.Builder(context)
        }
        builder.setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle("Location SDK")
            .setContentText("Running in the background")
            .setWhen(System.currentTimeMillis())
        notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            builder.build()
        } else {
            builder.notification
        }
        return notification
    }
}