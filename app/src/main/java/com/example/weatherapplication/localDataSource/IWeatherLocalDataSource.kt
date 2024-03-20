package com.example.weatherapplication.localDataSource

import com.example.weatherapplication.model.FavouriteCountries
import kotlinx.coroutines.flow.Flow

interface IWeatherLocalDataSource {
    suspend fun insertFavouriteCountry(favouriteCountry: FavouriteCountries)
    suspend fun deleteFavouriteCountry(favouriteCountry: FavouriteCountries)
    fun getStoredFavouriteCountries(): Flow<List<FavouriteCountries>>
}