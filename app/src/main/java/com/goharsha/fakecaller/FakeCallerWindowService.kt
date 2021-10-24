package com.goharsha.fakecaller

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.goharsha.fakecaller.databinding.LayoutFakeCallerBinding

class FakeCallerWindowService : Service() {
    private lateinit var windowManager: WindowManager
    private lateinit var binding: LayoutFakeCallerBinding
    private lateinit var container: FrameLayout

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        val ctx = ContextThemeWrapper(this, R.style.Theme_MaterialComponents_DayNight_Dialog)
        binding = LayoutFakeCallerBinding.inflate(LayoutInflater.from(ctx))

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
            },

            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.TOP
        params.x = 0
        params.y = 500

        container = FrameLayout(ctx)
        container.layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )

        container.addView(binding.root)
        windowManager.addView(container, params)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            goToForeground()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun goToForeground() {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val serviceChannel = NotificationChannel(
            "serviceNotificationChannelId",
            "Notification Service",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(serviceChannel)

        val notification: Notification =
            NotificationCompat.Builder(this, "serviceNotificationChannelId")
                .setContentTitle("Incoming call")
                .build()

        startForeground(-1, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        windowManager.removeView(container)
    }
}