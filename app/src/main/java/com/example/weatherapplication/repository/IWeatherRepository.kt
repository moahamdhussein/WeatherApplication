package com.example.weatherapplication.repository

import com.example.weatherapplication.Constant
import com.example.weatherapplication.model.FavouriteCountries
import com.example.weatherapplication.model.Root
import kotlinx.coroutines.flow.Flow
import org.intellij.lang.annotations.Language
import retrofit2.Response

interface IWeatherRepository {

    suspend fun getWeatherDetails(
        lat: Double,
        lon: Double,
        language: String
    ): Flow<Response<Root>>

    suspend fun getFavouriteCountries(): Flow<List<FavouriteCountries>>
    suspend fun getAllAlarm(): Flow<List<FavouriteCountries>>

    suspend fun insertFavouriteCountry(favouriteCountry: FavouriteCountries)
    suspend fun deleteFavouriteCountry(favouriteCountry: FavouriteCountries)

}