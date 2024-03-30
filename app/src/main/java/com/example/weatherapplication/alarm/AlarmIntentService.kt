package com.example.weatherapplication.alarm

import android.Manifest
import android.annotation.SuppressLint
import android.app.IntentService
import android.app.PendingIntent
import android.content.Intent
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.weatherapplication.utility.Constant
import com.example.weatherapplication.MainActivity
import com.example.weatherapplication.R
import com.example.weatherapplication.localDataSource.WeatherDatabase
import com.example.weatherapplication.localDataSource.WeatherLocalDataSource
import com.example.weatherapplication.remoteDataSource.WeatherRemoteDataSource
import com.example.weatherapplication.repository.WeatherRepository
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
            WeatherLocalDataSource(WeatherDatabase.getInstance(this).getFavouriteDao())
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

private fun showNotification(
    context: Context,
    title: String,
    message: String
) /*: NotificationCompat.Builder*/ {

    val activityIntent = Intent(context, MainActivity::class.java)
    val pendingIntent =
        PendingIntent.getActivity(context, 0, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT)
    val builder: NotificationCompat.Builder = NotificationCompat.Builder(context, "channel_id")
        .setSmallIcon(R.drawable.alarm_add)
        .setContentTitle(title)
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        .setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE)


    val notificationManager = NotificationManagerCompat.from(context)
    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        return /*NotificationCompat.Builder(context)*/
    }

    notificationManager.notify(0, builder.build())
    Log.i(TAG, "showNotification: dsdsds")
    /*return builder*/
}