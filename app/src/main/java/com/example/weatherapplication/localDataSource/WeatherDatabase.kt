package com.example.weatherapplication.localDataSource

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.weatherapplication.model.FavouriteCountries

private const val TAG = "WeatherDatabase"
@Database(entities = [FavouriteCountries::class], version = 1)
abstract class WeatherDatabase : RoomDatabase() {

    abstract fun getFavouriteDao(): FavouriteDao
    companion object {
        @Volatile
        private var INSTANCE: WeatherDatabase? = null
        fun getInstance(context: Context): WeatherDatabase {
            return INSTANCE ?: synchronized(context) {
                Log.i(TAG, "getInstance: ")

                val instance = Room.databaseBuilder(
                    context.applicationContext, WeatherDatabase::class.java, "weather_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}