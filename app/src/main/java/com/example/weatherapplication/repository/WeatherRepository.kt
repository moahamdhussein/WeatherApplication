package com.example.weatherapplication.repository

import com.example.weatherapplication.Constant
import com.example.weatherapplication.localDataSource.IWeatherLocalDataSource
import com.example.weatherapplication.model.FavouriteCountries
import com.example.weatherapplication.model.Root
import com.example.weatherapplication.remoteDataSource.IWeatherRemoteDataSource
import kotlinx.coroutines.flow.Flow
import retrofit2.Response


class WeatherRepository private constructor(
    private var weatherRemoteDataSource: IWeatherRemoteDataSource,
    private var weatherLocalDataSource: IWeatherLocalDataSource
) : IWeatherRepository {
    companion object {
        private var instance: WeatherRepository? = null

        fun getInstance(
            weatherRemoteDataSource: IWeatherRemoteDataSource,
            weatherLocalDataSource: IWeatherLocalDataSource
        ): WeatherRepository {
            return instance ?: synchronized(this) {
                val temp = WeatherRepository(weatherRemoteDataSource, weatherLocalDataSource)
                instance = temp
                temp
            }
        }
    }

    override suspend fun getWeatherDetails(
        lat: Double,
        lon: Double,
        unit: String,
        language:String
    ): Flow<Response<Root>> {
        return weatherRemoteDataSource.getWeathers(
            lat = lat,
            lon = lon,
            unit = unit,
            language = language
        )
    }

    override suspend fun getFavouriteCountries(): Flow<List<FavouriteCountries>> {
        return weatherLocalDataSource.getStoredFavouriteCountries()
    }

    override suspend fun insertFavouriteCountry(favouriteCountry: FavouriteCountries) {
        weatherLocalDataSource.insertFavouriteCountry(favouriteCountry)
    }

    override suspend fun deleteFavouriteCountry(favouriteCountry: FavouriteCountries) {
        weatherLocalDataSource.deleteFavouriteCountry(favouriteCountry)
    }
}