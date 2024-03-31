package com.example.weatherapplication.remoteDataSource

import com.example.weatherapplication.model.Root
import com.example.weatherapplication.utility.Constant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response



class WeatherRemoteDataSource : IWeatherRemoteDataSource {

    private val weatherService: ApiService by lazy {
        RetrofitHelper.getInstance().create(ApiService::class.java)
    }

    override fun getWeathers(lat: Double, lon: Double,language: String): Flow<Response<Root>> {
        return flow {
            emit(
                weatherService.getWeatherDetails(
                    lat = lat,
                    lon = lon,
                    apiKey = Constant.API_KEY,
                    lang = language,
                    units = Constant.Units.CELSIUS
                )
            )
        }
    }

    companion object {
        private var instance: WeatherRemoteDataSource? = null
        fun getInstance(): WeatherRemoteDataSource {
            return instance ?: synchronized(this) {
                val temp = WeatherRemoteDataSource()
                instance = temp
                temp
            }
        }
    }

}

