package com.example.weatherapplication.remoteDataSource

import com.example.weatherapplication.model.Root
import kotlinx.coroutines.flow.Flow
import retrofit2.Response


interface IWeatherRemoteDataSource {

    fun getWeathers(lat:Double,lon:Double,language:String):Flow<Response<Root>>
}