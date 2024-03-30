package com.example.weatherapplication.mapFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapplication.model.FavouriteCountries
import com.example.weatherapplication.repository.IWeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MapViewModel (private val repository: IWeatherRepository) : ViewModel() {

    fun insertFavourite(favouriteCountries: FavouriteCountries){
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertFavouriteCountry(favouriteCountries)
        }
    }
}