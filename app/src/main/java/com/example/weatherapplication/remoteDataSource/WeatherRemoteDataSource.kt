package com.example.weatherapplication.remoteDataSource

import com.example.weatherapplication.Constant
import com.example.weatherapplication.model.Root
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response

private const val TAG = "WeatherRemoteDataSource"

class WeatherRemoteDataSource : IWeatherRemoteDataSource {

    private val weatherService: ApiService by lazy {
        RetrofitHelper.getInstance().create(ApiService::class.java)
    }

    override fun getWeathers(lat: Double, lon: Double): Flow<Response<Root>> {
        return flow {
            emit(
                weatherService.getWeatherDetails(
                    lat = lat,
                    lon = lon,
                    apiKey = Constant.API_KEY,
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

//lat=55.7522&lon=37.6156