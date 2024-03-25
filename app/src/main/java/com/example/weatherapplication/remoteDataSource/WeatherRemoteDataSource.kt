package com.example.weatherapplication.remoteDataSource

import android.util.Log
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

    override fun getWeathers(lat: Double, lon: Double,unit: String,language: String): Flow<Response<Root>> {
        return flow {
            Log.i(TAG, "getWeathers: flow send $language")
            emit(
                weatherService.getWeatherDetails(
                    lat = lat,
                    lon = lon,
                    apiKey = Constant.API_KEY,
                    lang = language,
                    units = unit
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

