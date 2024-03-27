package com.example.weatherapplication.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapplication.model.Root
import com.example.weatherapplication.repository.WeatherRepository
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

private const val TAG = "HomeViewModel"

class HomeViewModel(private val repository: WeatherRepository) : ViewModel() {

    private var _weatherStatus: MutableStateFlow<Root> = MutableStateFlow(Root())
    val weatherStatus: StateFlow<Root> = _weatherStatus


    fun getWeatherStatus(lat: Double, lon: Double, unit: String, language: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getWeatherDetails(lat = lat, lon = lon,  language = language)
                .take(10)
                .takeIf { (it.first().body()?.list?.size ?: 0) > 0 }
                ?.catch {
                    Log.i(TAG, "getWeatherStatus: ${it.printStackTrace()}")
                }
                ?.collectLatest {
                    _weatherStatus.value = it.body() ?: Root()
                }
        }
    }

    fun writeDataToFile(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val file = File(context.cacheDir, "responseData")
                val fos = FileOutputStream(file)
                val jsonString = Gson().toJson(_weatherStatus.value)
                fos.write(jsonString.toByteArray())
            } catch (e: IOException) {
                Log.i(TAG, "writeDataToFile: ${e.printStackTrace()}")
            }
        }
    }

    fun readDataFromFile(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val file = File(context.cacheDir, "responseData")
                val fileContent = FileInputStream(file).bufferedReader().use { it.readText() }
                val data = Gson().fromJson(fileContent, Root::class.java)
                _weatherStatus.value = data
            } catch (e: IOException) {
                Log.i(TAG, "readDataFromFile: ${e.printStackTrace()}")
            }
        }
    }


}