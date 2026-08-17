package com.hima.alwarsha.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.hima.alwarsha.MainActivity
import com.hima.alwarsha.R
import com.hima.alwarsha.data.model.CarHealthSummary
import com.hima.alwarsha.data.model.StatusLevel

object NotificationHelper {

    private const val CHANNEL_ID = "alwarsha_maintenance_channel"
    private const val CHANNEL_NAME = "تنبيهات صيانة الورشة"
    private const val CHANNEL_DESC = "تنبيهات المواعيد والعدادات الحرجة لصيانة سيارتك"

    const val TRACKING_CHANNEL_ID = "alwarsha_tracking_channel"
    private const val TRACKING_CHANNEL_NAME = "تتبع القيادة التلقائي"
    private const val TRACKING_CHANNEL_DESC = "إشعار دائم أثناء تشغيل التتبع التلقائي للكيلومترات"

    fun createNotificationChannel(context: Context) {
        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH).apply {
            description = CHANNEL_DESC
            enableVibration(true)
        }
        notificationManager(context).createNotificationChannel(channel)
    }

    fun createTrackingChannel(context: Context) {
        val channel = NotificationChannel(TRACKING_CHANNEL_ID, TRACKING_CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW).apply {
            description = TRACKING_CHANNEL_DESC
            setShowBadge(false)
        }
        notificationManager(context).createNotificationChannel(channel)
    }

    private fun notificationManager(context: Context) =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun sendNotification(context: Context, title: String, message: String, notificationId: Int) {
        createNotificationChannel(context)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        NotificationManagerCompat.from(context).notify(notificationId, builder.build())
    }

    fun checkAndNotifyMaintenance(context: Context, summary: CarHealthSummary?, carName: String) {
        if (summary == null) return
        val redItems = summary.urgentAlerts.filter { it.statusLevel == StatusLevel.RED }
        val yellowItems = summary.urgentAlerts.filter { it.statusLevel == StatusLevel.YELLOW }

        when {
            redItems.isNotEmpty() -> {
                val top = redItems.first()
                val overdueKm = if (top.remainingKm < 0) -top.remainingKm else 0
                val msg = if (overdueKm > 0) {
                    "تجاوزت صيانة (${top.itemNameAr}) بـ $overdueKm كم لسيارتك $carName! يرجى الاستبدال فورًا."
                } else {
                    "حان موعد صيانة (${top.itemNameAr}) لسيارتك $carName."
                }
                sendNotification(context, "⚠️ صيانة متأخرة عاجلة", msg, notificationId = 2001)
            }
            yellowItems.isNotEmpty() -> {
                val top = yellowItems.first()
                sendNotification(
                    context,
                    "🟡 اقتراب موعد صيانة",
                    "متبقي ${top.remainingKm} كم أو ${top.remainingDays} يوم على صيانة (${top.itemNameAr}) لسيارتك $carName.",
                    notificationId = 3001
                )
            }
        }
    }
}
