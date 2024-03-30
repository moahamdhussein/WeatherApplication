package com.example.weatherapplication.favouriteList

import com.example.weatherapplication.model.FavouriteCountries

interface IFavouriteFragment{
    fun deleteItem(favouriteCountries: FavouriteCountries)

    fun onItemClick(favouriteCountries: FavouriteCountries)
}