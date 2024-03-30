package com.example.weatherapplication.utility

import java.text.SimpleDateFormat
import java.util.Date


private const val TAG = "Utils"


fun getDayName(dateString: String): String {

    val inFormat = SimpleDateFormat("yyyy-MM-dd")
    val date: Date? = inFormat.parse(dateString)
    val outFormat = SimpleDateFormat("EEEE")
    return outFormat.format(date?: Date("2024-03-30"))

}