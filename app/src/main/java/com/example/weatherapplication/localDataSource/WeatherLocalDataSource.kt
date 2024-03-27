package com.example.weatherapplication.localDataSource

import android.content.Context
import android.util.Log
import com.example.weatherapplication.model.FavouriteCountries
import kotlinx.coroutines.flow.Flow


private const val TAG = "WeatherLocalDataSource"
class WeatherLocalDataSource(context: Context) : IWeatherLocalDataSource {
    private val dao:FavouriteDao by lazy {
        val weatherDatabase = WeatherDatabase.getInstance(context)
        weatherDatabase.getFavouriteDao()
    }


    override suspend fun insertFavouriteCountry(favouriteCountry: FavouriteCountries){
        dao.insertProduct(favouriteCountry)
        Log.i(TAG, "insert $favouriteCountry")
    }


    override suspend fun deleteFavouriteCountry(favouriteCountry: FavouriteCountries){
        dao.deleteProduct(favouriteCountry)
    }
    override fun getStoredFavouriteCountries() : Flow<List<FavouriteCountries>> {
        return dao.getAllFavouriteCountries()
    }

    override fun getAllAlarm(): Flow<List<FavouriteCountries>> {
        return dao.getAllAlarm()
    }
}