package com.example.util

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
import com.example.MainActivity
import com.example.R
import com.example.data.model.CarHealthSummary
import com.example.data.model.StatusLevel

object NotificationHelper {

    private const val CHANNEL_ID = "autokeep_maintenance_channel"
    private const val CHANNEL_NAME = "تنبيهات صيانات السيارة AutoKeep"
    private const val CHANNEL_DESC = "تنبيهات المواعيد والعدادات الحرجة لصيانة سيارتك"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESC
                enableVibration(true)
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun sendNotification(
        context: Context,
        title: String,
        message: String,
        notificationId: Int = (System.currentTimeMillis() % 10000).toInt()
    ) {
        createNotificationChannel(context)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(notificationId, builder.build())
        }
    }

    fun checkAndNotifyMaintenance(context: Context, summary: CarHealthSummary?, carName: String) {
        if (summary == null) return

        val urgentAlerts = summary.urgentAlerts
        if (urgentAlerts.isEmpty()) {
            sendNotification(
                context = context,
                title = "🟢 سيارتك $carName بحالة ممتازة",
                message = "جميع الفلاتر والزيوت وقطع الغيار تعمل وفق الجدول الزمني المحدد دون أي متأخرات.",
                notificationId = 1001
            )
            return
        }

        val redItems = urgentAlerts.filter { it.statusLevel == StatusLevel.RED }
        val yellowItems = urgentAlerts.filter { it.statusLevel == StatusLevel.YELLOW }

        if (redItems.isNotEmpty()) {
            val topRed = redItems.first()
            val overdueKm = if (topRed.remainingKm < 0) -topRed.remainingKm else 0
            val msg = if (overdueKm > 0) {
                "تحذير حرج لـ $carName: تجاوزت صيانة (${topRed.itemNameAr}) بـ $overdueKm كم! يرجى الاستبدال فوراً للحفاظ على المحرك."
            } else {
                "تنبيه صيانة حرج لـ $carName: حان موعد صيانة (${topRed.itemNameAr})."
            }
            sendNotification(
                context = context,
                title = "🔴 صيانة متأخرة عاجلة - $carName",
                message = msg,
                notificationId = 2001
            )
        } else if (yellowItems.isNotEmpty()) {
            val topYellow = yellowItems.first()
            sendNotification(
                context = context,
                title = "🟡 اقتراب موعد صيانة - $carName",
                message = "متبقي ${topYellow.remainingKm} كم فقط أو ${topYellow.remainingDays} يوم على صيانة (${topYellow.itemNameAr}).",
                notificationId = 3001
            )
        }
    }
}
