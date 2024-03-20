package com.example.weatherapplication.favouriteList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapplication.model.FavouriteCountries
import com.example.weatherapplication.model.Root
import com.example.weatherapplication.model.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FavouriteViewModel(private val repository: WeatherRepository) : ViewModel() {
    private var _weatherStatus: MutableStateFlow<List<FavouriteCountries>> = MutableStateFlow(listOf())
    val weatherStatus: StateFlow<List<FavouriteCountries>> = _weatherStatus


    init {
        getFavouriteCountries()
    }


    fun getFavouriteCountries(){
        viewModelScope.launch(Dispatchers.IO) {
            repository.getFavouriteCountries().collectLatest {
                _weatherStatus.value = it
            }
        }
    }
}