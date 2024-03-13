package com.example.weatherapplication.remoteDataSource

import com.example.weatherapplication.model.Root
import retrofit2.Response


interface IWeatherRemoteDataSource {

    suspend fun getWeathers():Response<Root>
}