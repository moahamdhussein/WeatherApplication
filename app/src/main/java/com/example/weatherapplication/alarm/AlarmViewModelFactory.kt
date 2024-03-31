package com.example.weatherapplication.alarm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapplication.repository.IWeatherRepository

class AlarmViewModelFactory(private val repo: IWeatherRepository) : ViewModelProvider.Factory{

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(AlarmViewModel::class.java)){
            AlarmViewModel(repo) as T
        }else{
            throw IllegalArgumentException("View model class not found")
        }
    }
}