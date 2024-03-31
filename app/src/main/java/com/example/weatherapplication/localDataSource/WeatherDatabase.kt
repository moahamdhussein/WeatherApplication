package com.example.weatherapplication.localDataSource

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.weatherapplication.model.FavouriteCountries

@Database(entities = [FavouriteCountries::class], version = 1)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun getCountriesDao(): CountriesDao
    companion object {
        @Volatile
        private var INSTANCE: WeatherDatabase? = null
        fun getInstance(context: Context): WeatherDatabase {
            return INSTANCE ?: synchronized(context) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, WeatherDatabase::class.java, "weather_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}