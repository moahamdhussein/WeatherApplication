package com.example.weatherapplication.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapplication.model.Root
import com.example.weatherapplication.repository.IWeatherRepository
import com.example.weatherapplication.utility.ApiState
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

private const val TAG = "HomeViewModel"

class HomeViewModel(private val repository: IWeatherRepository) : ViewModel() {

    private var _weatherStatus: MutableStateFlow<ApiState> = MutableStateFlow(ApiState.Loading)
    val weatherStatus: StateFlow<ApiState> = _weatherStatus

    init {
        getWeatherStatus(null, null, null)
    }

    fun getWeatherStatus(lat: Double?, lon: Double?, language: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            if (lat != null && lon != null && language != null) {
                repository.getWeatherDetails(lat = lat, lon = lon, language = language)
                    .takeIf { (it.first().body()?.list?.size ?: 0) > 0 }
                    ?.catch {
                        _weatherStatus.value = ApiState.Failure(it.message.toString())
                    }
                    ?.collectLatest {
                        _weatherStatus.value = ApiState.Success(it.body() ?: Root())
                    }
            }
        }
    }

    fun writeDataToFile(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val file = File(context.cacheDir, "responseData")
                val fos = FileOutputStream(file)
                val jsonString = Gson().toJson((_weatherStatus.value as ApiState.Success).root)
                fos.write(jsonString.toByteArray())
            } catch (e: IOException) {
                Log.i(TAG, "writeDataToFile: ${e.printStackTrace()}")
            }
        }
    }

    fun readDataFromFile(context: Context) {
        viewModelScope.launch {
            try {
                val file = File(context.cacheDir, "responseData")
                launch(Dispatchers.IO) {

                    val fileContent = FileInputStream(file).bufferedReader().use { it.readText() }
                    val data = Gson().fromJson(fileContent, Root::class.java)
                    _weatherStatus.value = ApiState.Success(data)
                }
            } catch (e: IOException) {
                Log.i(TAG, "readDataFromFile: ${e.printStackTrace()}")
            }
        }
    }
}