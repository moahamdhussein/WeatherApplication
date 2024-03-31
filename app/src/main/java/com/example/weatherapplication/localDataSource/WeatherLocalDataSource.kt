package com.example.weatherapplication.localDataSource

import com.example.weatherapplication.model.FavouriteCountries
import kotlinx.coroutines.flow.Flow


class WeatherLocalDataSource(val dao:CountriesDao) : IWeatherLocalDataSource {

    override suspend fun insertCountry(favouriteCountry: FavouriteCountries){
        dao.insertCountry(favouriteCountry)

    }

    override suspend fun deleteCountry(favouriteCountry: FavouriteCountries){
        dao.deleteCountry(favouriteCountry)
    }
    override fun getStoredFavouriteCountries() : Flow<List<FavouriteCountries>> {
        return dao.getAllFavouriteCountries()
    }

    override fun getAllAlarm(): Flow<List<FavouriteCountries>> {
        return dao.getAllAlarm()
    }
}