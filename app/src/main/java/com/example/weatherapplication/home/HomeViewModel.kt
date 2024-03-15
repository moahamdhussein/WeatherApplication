package com.example.weatherapplication.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapplication.model.Root
import com.example.weatherapplication.model.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: WeatherRepository): ViewModel() {

    private var _weatherStatus:MutableStateFlow<Root> = MutableStateFlow(Root())
    val  weatherStatus : StateFlow<Root> = _weatherStatus

    init {
        getWeatherStatus(55.7522,37.6156)
    }

    fun getWeatherStatus(lat:Double,lon:Double) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getWeatherDetails(lat, lon).collectLatest {
                _weatherStatus.value = it.body() ?: Root()
            }
        }
    }
}