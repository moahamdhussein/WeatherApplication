package com.example.weatherapplication.utility

import com.example.weatherapplication.model.Root


sealed class ApiState{
    class Success(val root: Root):ApiState()
    class Failure(val msg:String):ApiState()
    object Loading:ApiState()
}