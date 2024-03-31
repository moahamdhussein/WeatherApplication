package com.example.weatherapplication.alarm

import android.Manifest
import android.annotation.SuppressLint
import android.app.IntentService
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.weatherapplication.R
import com.example.weatherapplication.alarm.name.CHANNEL_ID
import com.example.weatherapplication.alarm.name.NOTIFICATION_ID
import com.example.weatherapplication.localDataSource.WeatherDatabase
import com.example.weatherapplication.localDataSource.WeatherLocalDataSource
import com.example.weatherapplication.remoteDataSource.WeatherRemoteDataSource
import com.example.weatherapplication.repository.WeatherRepository
import com.example.weatherapplication.utility.Constant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val TAG = "AlarmIntentService"

@SuppressLint("ForegroundServiceType")
class AlarmIntentService : IntentService("AlarmIntentService"), CoroutineScope by MainScope() {

    private val repo by lazy {
        WeatherRepository.getInstance(
            WeatherRemoteDataSource.getInstance(),
            WeatherLocalDataSource(WeatherDatabase.getInstance(this).getCountriesDao())
        )
    }

    @Deprecated("Deprecated in Java")
    override fun onHandleIntent(intent: Intent?) {
        val isEnglish = getSharedPreferences("Setting", Context.MODE_PRIVATE)
            .getBoolean(Constant.LANGUAGE_KEY, true)
        val language: String = if (isEnglish) "en" else "ar"
        Log.i(TAG, "onHandleIntent: test")
        val lat: Double = intent?.getDoubleExtra("lat", 0.0) ?: 0.0
        val lon: Double = intent?.getDoubleExtra("lon", 0.0) ?: 0.0
        launch(Dispatchers.IO) {
            repo.getWeatherDetails(lat, lon, language).collectLatest {
                it.body().let { root ->
                    showNotification(
                        this@AlarmIntentService,
                        "city name${root?.city?.name}",
                        "weather description : ${root?.list?.get(0)?.weather?.get(0)?.description ?: " not found"} and temperature = " +
                                "${root?.list?.get(0)?.main?.temp ?: " not found"}"
                    )
                }
            }
        }
    }

}


fun showNotification(context: Context?, countryName:String ,weatherState: String) {
    if (context == null) {
        return
    }

    if (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.INTERNET
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        return
    }

    // Create notification channel if necessary (for Android 8.0 and higher)<<<<<
    createNotificationChannel(context)

    val notification = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.alarm_add)
        .setContentTitle(countryName)
        .setContentText(weatherState)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .build()

    val notificationManager = NotificationManagerCompat.from(context)
    notificationManager.notify(NOTIFICATION_ID, notification)
}

private fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Weather Alerts",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Channel for weather alerts"
        }
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

object name{
    const val CHANNEL_ID = "weather_alerts_channel"
    const val NOTIFICATION_ID = 1
}
