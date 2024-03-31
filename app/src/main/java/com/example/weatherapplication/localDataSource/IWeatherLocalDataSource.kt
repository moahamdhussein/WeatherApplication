package com.example.weatherapplication.localDataSource

import com.example.weatherapplication.model.FavouriteCountries
import kotlinx.coroutines.flow.Flow

interface IWeatherLocalDataSource {
    suspend fun insertCountry(favouriteCountry: FavouriteCountries)
    suspend fun deleteCountry(favouriteCountry: FavouriteCountries)
    fun getStoredFavouriteCountries(): Flow<List<FavouriteCountries>>

    fun getAllAlarm():Flow<List<FavouriteCountries>>
}