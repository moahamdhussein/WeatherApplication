package com.example.weatherapplication.repoTests

import android.util.Log
import com.example.weatherapplication.model.City
import com.example.weatherapplication.model.FavouriteCountries
import com.example.weatherapplication.model.Root
import com.example.weatherapplication.model.Sys
import com.example.weatherapplication.model.Weather
import com.example.weatherapplication.model.WeatherProperty
import com.example.weatherapplication.model.Wind
import com.example.weatherapplication.repository.IWeatherRepository
import com.example.weatherapplication.utility.ApiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import retrofit2.Response

private const val TAG = "FakeRepo"

class FakeRepo : IWeatherRepository {
    val data: MutableList<FavouriteCountries> = mutableListOf()
    val favouriteLocalData: MutableStateFlow<List<FavouriteCountries>> = MutableStateFlow(listOf())
    val remoteData :MutableStateFlow<ApiState> = MutableStateFlow(ApiState.Loading)
    override suspend fun getWeatherDetails(
        lat: Double,
        lon: Double,
        language: String

    ): Flow<Response<Root>> {
        val root = Root()
        root.city.let {
            it?.id=12332
            it?.name = "alex"
            it?.coord?.lat = 0.0
            it?.coord?.lon = 0.0
            it?.country="Egypt"
            it?.population=100
            it?.timezone=100
            it?.sunrise=100
            it?.sunset=100
        }
        root.cod = "code"
        root.message = 200
        root.cnt = 200
        val weatherProperty = WeatherProperty()
        weatherProperty.let {
            it.weather = arrayListOf()
            it.visibility=1
            it.wind= Wind()
            it.sys = Sys()

        }
        root.list = mutableListOf(WeatherProperty())
        return flow {  emit(Response.success(root))}
    }


    override suspend fun getFavouriteCountries(): Flow<List<FavouriteCountries>> {
        return favouriteLocalData
    }

    override suspend fun getAllAlarm(): Flow<List<FavouriteCountries>> {

        return favouriteLocalData
    }

    override suspend fun insertCountry(favouriteCountry: FavouriteCountries) {
        data.add(favouriteCountry)
        favouriteLocalData.value = data
    }

    override suspend fun deleteCountry(favouriteCountry: FavouriteCountries) {
        data.remove(favouriteCountry)
        favouriteLocalData.value = data
    }
}