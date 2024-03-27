package com.example.weatherapplication.remoteDataSource

import com.example.weatherapplication.Constant
import com.example.weatherapplication.model.Root
import kotlinx.coroutines.flow.Flow
import org.intellij.lang.annotations.Language
import retrofit2.Response


interface IWeatherRemoteDataSource {

    fun getWeathers(lat:Double,lon:Double,language:String):Flow<Response<Root>>
}