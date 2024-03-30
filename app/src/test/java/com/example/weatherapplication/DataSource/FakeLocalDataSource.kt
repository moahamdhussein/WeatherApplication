package com.example.weatherapplication.DataSource

import com.example.weatherapplication.localDataSource.IWeatherLocalDataSource
import com.example.weatherapplication.model.FavouriteCountries
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeLocalDataSource() :IWeatherLocalDataSource{

    private val localDataSource :MutableList<FavouriteCountries> = mutableListOf()
    private val stateData:MutableStateFlow<List<FavouriteCountries>> =  MutableStateFlow(listOf())
    override suspend fun insertFavouriteCountry(favouriteCountry: FavouriteCountries) {
        localDataSource.add(favouriteCountry)
        stateData.value = localDataSource
    }

    override suspend fun deleteFavouriteCountry(favouriteCountry: FavouriteCountries) {
        localDataSource.remove(favouriteCountry)
        stateData.value = localDataSource
    }

    override fun getStoredFavouriteCountries(): Flow<List<FavouriteCountries>> {
        return stateData
    }

    override fun getAllAlarm(): Flow<List<FavouriteCountries>> {
        return  stateData
    }
}