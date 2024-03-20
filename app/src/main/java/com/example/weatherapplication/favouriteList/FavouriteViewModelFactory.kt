package com.example.weatherapplication.favouriteList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapplication.home.HomeViewModel
import com.example.weatherapplication.model.WeatherRepository

class FavouriteViewModelFactory(private val repo: WeatherRepository) : ViewModelProvider.Factory{

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(FavouriteViewModel::class.java)){
            FavouriteViewModel(repo) as T
        }else{
            throw IllegalArgumentException("View model class not found")
        }
    }
}