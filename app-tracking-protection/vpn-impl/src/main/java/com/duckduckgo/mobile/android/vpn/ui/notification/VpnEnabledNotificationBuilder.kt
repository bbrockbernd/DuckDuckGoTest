/*
 * Copyright (c) 2020 DuckDuckGo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.duckduckgo.mobile.android.vpn.ui.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.duckduckgo.mobile.android.vpn.R
import com.duckduckgo.mobile.android.vpn.service.VpnActionReceiver
import com.duckduckgo.mobile.android.vpn.service.VpnEnabledNotificationContentPlugin
import com.duckduckgo.mobile.android.vpn.service.VpnEnabledNotificationContentPlugin.NotificationActions
import com.duckduckgo.mobile.android.vpn.service.VpnEnabledNotificationContentPlugin.NotificationActions.VPNActions
import com.duckduckgo.mobile.android.vpn.service.VpnEnabledNotificationContentPlugin.NotificationActions.VPNFeatureActions

class VpnEnabledNotificationBuilder {

    companion object {

        private const val VPN_FOREGROUND_SERVICE_NOTIFICATION_CHANNEL_ID = "com.duckduckgo.mobile.android.vpn.notification.ongoing"
        private const val VPN_FOREGROUND_SERVICE_NOTIFICATION_CHANNEL_NAME = "App Tracking Protection Status"
        private const val VPN_FOREGROUND_SERVICE_NOTIFICATION_CHANNEL_DESCRIPTION = "Ongoing state of App Tracking Protection"

        private fun registerOngoingNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel =
                    NotificationChannel(
                        VPN_FOREGROUND_SERVICE_NOTIFICATION_CHANNEL_ID,
                        VPN_FOREGROUND_SERVICE_NOTIFICATION_CHANNEL_NAME,
                        IMPORTANCE_LOW,
                    )
                channel.description = VPN_FOREGROUND_SERVICE_NOTIFICATION_CHANNEL_DESCRIPTION
                val notificationManager = NotificationManagerCompat.from(context)
                notificationManager.createNotificationChannel(channel)
            }
        }

        fun buildVpnEnabledNotification(
            context: Context,
            vpnEnabledNotificationContent: VpnEnabledNotificationContentPlugin.VpnEnabledNotificationContent,
        ): Notification {
            registerOngoingNotificationChannel(context)

            val notificationLayout = RemoteViews(context.packageName, R.layout.notification_vpn_enabled)
            notificationLayout.setTextViewText(R.id.deviceShieldNotificationHeader, vpnEnabledNotificationContent.title)

            return NotificationCompat.Builder(context, VPN_FOREGROUND_SERVICE_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(com.duckduckgo.mobile.android.R.drawable.notification_logo)
                .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                .setContentIntent(vpnEnabledNotificationContent.onNotificationPressIntent)
                .setCustomContentView(notificationLayout)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setChannelId(VPN_FOREGROUND_SERVICE_NOTIFICATION_CHANNEL_ID)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .build()
        }

        fun buildVpnEnabledUpdateNotification(
            context: Context,
            vpnNotification: VpnEnabledNotificationContentPlugin.VpnEnabledNotificationContent,
        ): Notification {
            registerOngoingNotificationChannel(context)

            val notificationLayout = RemoteViews(context.packageName, R.layout.notification_vpn_enabled)
            notificationLayout.setTextViewText(R.id.deviceShieldNotificationHeader, vpnNotification.title)

            return NotificationCompat.Builder(context, VPN_FOREGROUND_SERVICE_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(com.duckduckgo.mobile.android.R.drawable.notification_logo)
                .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                .setContentIntent(vpnNotification.onNotificationPressIntent)
                .setCustomContentView(notificationLayout)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setSilent(true)
                .setOngoing(true)
                .addNotificationActions(context, vpnNotification.notificationActions)
                .setChannelId(VPN_FOREGROUND_SERVICE_NOTIFICATION_CHANNEL_ID)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .build()
        }

        private fun NotificationCompat.Builder.addNotificationActions(
            context: Context,
            notificationActions: NotificationActions,
        ): NotificationCompat.Builder {
            when (notificationActions) {
                is VPNActions -> getVpnActions(context)
                is VPNFeatureActions -> notificationActions.actions.take(2)
            }.onEach { action ->
                addAction(action)
            }
            return this
        }

        private fun getVpnActions(
            context: Context,
        ): List<NotificationCompat.Action> {
            return listOf(
                NotificationCompat.Action(
                    R.drawable.ic_baseline_feedback_24,
                    context.getString(R.string.vpn_NotificationCTASnoozeVpn),
                    PendingIntent.getBroadcast(
                        context,
                        0,
                        Intent(context, VpnActionReceiver::class.java).apply {
                            action = VpnActionReceiver.ACTION_VPN_SNOOZE
                        },
                        PendingIntent.FLAG_IMMUTABLE,
                    ),
                ),
                NotificationCompat.Action(
                    R.drawable.ic_baseline_feedback_24,
                    context.getString(R.string.vpn_NotificationCTADisableVpn),
                    PendingIntent.getBroadcast(
                        context,
                        1,
                        Intent(context, VpnActionReceiver::class.java).apply {
                            action = VpnActionReceiver.ACTION_VPN_DISABLE
                        },
                        PendingIntent.FLAG_IMMUTABLE,
                    ),
                ),
            )
        }
    }
}
