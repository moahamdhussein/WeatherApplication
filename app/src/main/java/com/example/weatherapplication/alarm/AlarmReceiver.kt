package com.example.weatherapplication.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


private const val TAG = "AlarmReceiver"
class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val lat = intent.getDoubleExtra("lat",0.0)
        val lon = intent.getDoubleExtra("lon",0.0)
        val intentService  = Intent(context,AlarmIntentService::class.java)
        intentService.putExtra("lat",lat)
        intentService.putExtra("lon",lon)
        context.startService(intentService)


    }
}