package com.example.weatherapplication.repoTests

import android.util.Log
import com.example.weatherapplication.model.FavouriteCountries
import com.example.weatherapplication.model.Root
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
    ): Flow<Response<Root>>  = flow {  emit(Response.success(Root()))}


    override suspend fun getFavouriteCountries(): Flow<List<FavouriteCountries>> {
        return favouriteLocalData
    }

    override suspend fun getAllAlarm(): Flow<List<FavouriteCountries>> {

        return favouriteLocalData
    }

    override suspend fun insertFavouriteCountry(favouriteCountry: FavouriteCountries) {
        data.add(favouriteCountry)
        favouriteLocalData.value = data
    }

    override suspend fun deleteFavouriteCountry(favouriteCountry: FavouriteCountries) {
        data.remove(favouriteCountry)
        favouriteLocalData.value = data
    }
}