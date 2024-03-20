package com.example.weatherapplication.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favourite_countries", primaryKeys = ["longitude", "latitude"])
data class FavouriteCountries(

    val longitude: Double,

    val latitude: Double,

    val cityName: String,

    val type: String
)
