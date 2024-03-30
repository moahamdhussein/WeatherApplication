package com.example.weatherapplication.alarm


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherapplication.model.FavouriteCountries
import com.example.weatherapplication.repository.IWeatherRepository
import com.example.weatherapplication.repository.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AlarmViewModel(private val repository: IWeatherRepository) : ViewModel() {
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

    fun deleteAlarmItem(favouriteCountries: FavouriteCountries) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteFavouriteCountry(favouriteCountries)
        }
    }
}
