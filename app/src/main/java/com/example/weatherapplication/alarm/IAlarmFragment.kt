package com.example.weatherapplication.alarm

import com.example.weatherapplication.model.FavouriteCountries

interface IAlarmFragment {
    fun deleteAlarmItem(favouriteCountries: FavouriteCountries)

    fun onItemClick(favouriteCountries: FavouriteCountries)
}