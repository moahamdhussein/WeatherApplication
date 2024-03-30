package com.example.weatherapplication.DataSource

import com.example.weatherapplication.model.Root
import com.example.weatherapplication.remoteDataSource.IWeatherRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response

class FakeRemoteDataSource : IWeatherRemoteDataSource {
    override fun getWeathers(lat: Double, lon: Double, language: String): Flow<Response<Root>> {
        return flow { emit(Response.success(Root())) }
    }
}