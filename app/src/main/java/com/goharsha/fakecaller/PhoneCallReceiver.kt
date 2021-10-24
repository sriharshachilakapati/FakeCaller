package com.goharsha.fakecaller

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Log

private const val TAG = "PhoneCallReceiver"

class PhoneCallReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "Receiving call")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(context)) {
            return
        }

        if (intent == null || intent.action != TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
            return
        }

        try {
            when (intent.getStringExtra(TelephonyManager.EXTRA_STATE)) {
                TelephonyManager.EXTRA_STATE_RINGING -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context?.startForegroundService(Intent(context, FakeCallerWindowService::class.java))
                    } else {
                        context?.startService(Intent(context, FakeCallerWindowService::class.java))
                    }
                }

                TelephonyManager.EXTRA_STATE_IDLE -> {
                    context?.stopService(Intent(context, FakeCallerWindowService::class.java))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}