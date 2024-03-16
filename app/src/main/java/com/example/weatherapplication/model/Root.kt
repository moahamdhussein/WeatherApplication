package com.example.weatherapplication.model

import com.google.gson.annotations.SerializedName

data class Root(

    @SerializedName("cod") var cod: String? = null,
    @SerializedName("message") var message: Int? = null,
    @SerializedName("cnt") var cnt: Int? = null,
    @SerializedName("list") var list: MutableList<WeatherProperty> = mutableListOf(),
    @SerializedName("city") var city: City? = City()

)

