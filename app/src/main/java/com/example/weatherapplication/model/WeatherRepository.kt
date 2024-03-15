package com.example.weatherapplication.model

import com.example.weatherapplication.remoteDataSource.IWeatherRemoteDataSource
import kotlinx.coroutines.flow.Flow
import retrofit2.Response


class WeatherRepository private constructor(
    private var weatherRemoteDataSource: IWeatherRemoteDataSource
) : IWeatherRepository {
    companion object {
        private var instance: WeatherRepository? = null

        fun getInstance(
            weatherRemoteDataSource: IWeatherRemoteDataSource
        ): WeatherRepository {
            return instance ?: synchronized(this) {
                val temp = WeatherRepository(weatherRemoteDataSource)
                instance = temp
                temp
            }
        }
    }

    override suspend fun getWeatherDetails(lat: Double, lon: Double): Flow<Response<Root>> {
        return weatherRemoteDataSource.getWeathers(
            lat = lat,
            lon = lon
        )
    }
}