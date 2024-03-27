package com.example.weatherapplication.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherapplication.model.FavouriteCountries
import com.example.weatherapplication.repository.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AlarmViewModel(private val repository: WeatherRepository) : ViewModel() {
    private var _weatherStatus: MutableStateFlow<List<FavouriteCountries>> =
        MutableStateFlow(listOf())
    val weatherStatus: StateFlow<List<FavouriteCountries>> = _weatherStatus


    init {
        getAllAlarm()
    }

    private fun getAllAlarm() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getAllAlarm().collectLatest {
                _weatherStatus.value = it
            }
        }
    }



}
