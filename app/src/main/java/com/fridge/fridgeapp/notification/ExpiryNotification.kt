package com.fridge.fridgeapp.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.fridge.fridgeapp.MainActivity
import com.fridge.fridgeapp.R
import com.fridge.fridgeapp.database.Food
import com.fridge.fridgeapp.util.ExpiryUtils

object ExpiryNotification {
    const val CHANNEL_ID = "expiry_reminders"
    private const val NOTIFICATION_ID = 1001

    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.expiry_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = context.getString(R.string.expiry_channel_description)
        }
        context.getSystemService(NotificationManager::class.java)
            .createNotificationChannel(channel)
    }

    fun canPost(context: Context): Boolean {
        val permissionGranted = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        return permissionGranted && NotificationManagerCompat.from(context).areNotificationsEnabled()
    }

    fun show(context: Context, foods: List<Food>) {
        if (!canPost(context)) return

        val expired = foods.filter { ExpiryUtils.isExpired(it.expireDate) }
        val expiring = foods.filter { ExpiryUtils.isExpiringSoon(it.expireDate) }
        if (expired.isEmpty() && expiring.isEmpty()) return

        val details = buildList {
            if (expired.isNotEmpty()) {
                add(
                    context.getString(
                        R.string.expiry_notification_expired,
                        expired.joinToString("、") { it.name }
                    )
                )
            }
            if (expiring.isNotEmpty()) {
                add(
                    context.getString(
                        R.string.expiry_notification_expiring,
                        expiring.joinToString("、") { it.name }
                    )
                )
            }
        }.joinToString("\n")

        val contentIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(context.getString(R.string.expiry_notification_title))
            .setContentText(
                context.getString(
                    R.string.expiry_notification_summary,
                    expired.size,
                    expiring.size
                )
            )
            .setStyle(NotificationCompat.BigTextStyle().bigText(details))
            .setContentIntent(contentIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
    }
}
