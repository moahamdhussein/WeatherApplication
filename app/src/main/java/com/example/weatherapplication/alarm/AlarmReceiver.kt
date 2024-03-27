package com.example.weatherapplication.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

private const val TAG = "AlarmReceiver"
class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val intentService  = Intent(context,AlarmIntentService::class.java)
        context.startService(intentService)
        Log.i(TAG, "onReceive: ")
    }
}