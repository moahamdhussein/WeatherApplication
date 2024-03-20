package com.example.weatherapplication.home

import android.annotation.SuppressLint
import android.os.Looper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapplication.model.Root
import com.example.weatherapplication.model.WeatherRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: WeatherRepository): ViewModel() {



    private var _weatherStatus:MutableStateFlow<Root> = MutableStateFlow(Root())
    val  weatherStatus : StateFlow<Root> = _weatherStatus

    init {


    }

    fun getWeatherStatus(lat:Double,lon:Double) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getWeatherDetails(lat, lon).collectLatest {
                _weatherStatus.value = it.body() ?: Root()
            }
        }
    }



}