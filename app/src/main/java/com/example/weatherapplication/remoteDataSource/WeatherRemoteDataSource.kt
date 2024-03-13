package com.example.weatherapplication.remoteDataSource

import android.util.Log
import com.example.weatherapplication.Constant
import com.example.weatherapplication.model.Root
import retrofit2.Response

private const val TAG = "WeatherRemoteDataSource"
class WeatherRemoteDataSource :IWeatherRemoteDataSource {

    private val weatherService:ApiService by lazy {
        RetrofitHelper.getInstance().create(ApiService::class.java)
    }
    override suspend fun getWeathers(): Response<Root> {
        val response =weatherService.getWeatherDetails(
            lat = 55.7522,
            lon = 37.6156,
            apiKey = Constant.API_KEY,
            units = Constant.Units.CELSIUS
        )
        Log.i(TAG, "getWeathers: "+response.body()?.city.toString())
        Log.i(TAG, "getWeathers: "+response.body()?.list?.size)
        return response
    }

    companion object{
        private var instance:WeatherRemoteDataSource? = null
        fun getInstance():WeatherRemoteDataSource{
            return instance ?: synchronized(this){
                val temp = WeatherRemoteDataSource()
                instance =temp
                temp
            }
        }
    }

}