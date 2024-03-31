package com.example.weatherapplication.favouriteList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapplication.repository.IWeatherRepository

class FavouriteViewModelFactory(private val repo: IWeatherRepository) : ViewModelProvider.Factory{

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(FavouriteViewModel::class.java)){
            FavouriteViewModel(repo) as T
        }else{
            throw IllegalArgumentException("View model class not found")
        }
    }
}