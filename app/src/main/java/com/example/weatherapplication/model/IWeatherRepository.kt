package com.example.weatherapplication.model

import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface IWeatherRepository {

         suspend fun getWeatherDetails(lat: Double, lon: Double): Flow<Response<Root>>
}