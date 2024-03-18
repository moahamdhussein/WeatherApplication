package com.example.weatherapplication

object Constant {
    const val API_KEY: String = "c90255713cc0502875ed0e60b3e30d4d"
    const val LANGUAGE_KEY:String = "language"
    const val LOCATION_KEY:String = "location"
    const val TEMPERATURE_Unit:String = "temperatureUnit"
    const val WIND_SPEED_UNIT:String="windSpeedUnit"
    object Units {
        const val CELSIUS: String = "metric"
        const val FAHRENHEIT: String = "imperial"
        const val KELVIN: String = "standard"
    }

    object Language {
        const val ARABIC = "ar"
        const val ENGLISH = "en"
    }

}
