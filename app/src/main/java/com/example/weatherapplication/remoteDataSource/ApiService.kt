package com.example.weatherapplication.remoteDataSource

import com.example.weatherapplication.Constant
import com.example.weatherapplication.model.Root
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("forecast")
    suspend fun getWeatherDetails(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String ,
        @Query("lang") lang:String
    ): Response<Root>
}