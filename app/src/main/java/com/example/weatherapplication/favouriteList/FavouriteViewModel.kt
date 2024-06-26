package com.example.weatherapplication.favouriteList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapplication.model.FavouriteCountries
import com.example.weatherapplication.repository.IWeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FavouriteViewModel(private val repository: IWeatherRepository) : ViewModel() {
    private var _weatherStatus: MutableStateFlow<List<FavouriteCountries>> =
        MutableStateFlow(listOf())
    val weatherStatus: StateFlow<List<FavouriteCountries>> = _weatherStatus

    init {
        getFavouriteCountries()
    }

    private fun getFavouriteCountries() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getFavouriteCountries().collectLatest {
                _weatherStatus.value = it
            }
        }
    }

    fun deleteFavouriteCountry(favouriteCountries: FavouriteCountries) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteCountry(favouriteCountries)
        }
    }

}