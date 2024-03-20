package com.example.weatherapplication.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherapplication.R
import com.example.weatherapplication.localDataSource.WeatherLocalDataSource
import com.example.weatherapplication.model.Root
import com.example.weatherapplication.model.WeatherProperty
import com.example.weatherapplication.model.WeatherRepository
import com.example.weatherapplication.remoteDataSource.WeatherRemoteDataSource
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private const val TAG = "HomeFragment"

class HomeFragment : Fragment() {
    private lateinit var viewModel: HomeViewModel
    private lateinit var factory: HomeViewModelFactory
    private lateinit var rvDaily: RecyclerView
    private lateinit var rvNextDays: RecyclerView
    private lateinit var nextDaysManager: LinearLayoutManager
    private lateinit var dailyManager: LinearLayoutManager
    private lateinit var homeAdapter: DailyForecastAdapter
    private lateinit var nextDaysAdapter: NextDaysAdapter
    private lateinit var tvTodayDate: TextView
    private lateinit var tvCity: TextView
    private lateinit var tvDegree: TextView
    private lateinit var mainWeatherIcon: ImageView
    private lateinit var tvWeatherDescription: TextView
    private lateinit var tvCurrentTime: TextView
    private lateinit var tvHumidityPercentage: TextView
    private lateinit var tvWindSpeed: TextView
    private lateinit var tvCloudPercentage: TextView
    private lateinit var tvPressure: TextView
    private lateinit var calendar: Calendar
    private lateinit var fusedLocationProvider: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getRefreshLocation()

        factory =
            HomeViewModelFactory(
                repo = WeatherRepository.getInstance(
                    WeatherRemoteDataSource.getInstance(),
                    WeatherLocalDataSource(requireContext())
                )
            )
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
        initializeUi(view)
        lifecycleScope.launch {
            viewModel.weatherStatus.collectLatest {
                launch(Dispatchers.Main) {
                    if (it.list.size > 0) {
                        updateUi(it)
                    }
                }
            }
        }
    }

    private fun setDailyListData(list: MutableList<WeatherProperty>) {
        homeAdapter.setList(list)
    }

    private fun setNextDaysData(list: MutableList<WeatherProperty>) {
        nextDaysAdapter.setList(list)
    }

    private fun initializeUi(view: View) {

        rvDaily = view.findViewById(R.id.rv_daily_forecast)
        tvTodayDate = view.findViewById(R.id.tv_today_date)
        tvCity = view.findViewById(R.id.tv_city)
        tvDegree = view.findViewById(R.id.fl_tv_degree)
        mainWeatherIcon = view.findViewById(R.id.fl_iv_main_weather_icon)
        tvWeatherDescription = view.findViewById(R.id.fl_tv_weather_description)
        tvCurrentTime = view.findViewById(R.id.fl_tv_current_time)
        tvHumidityPercentage = view.findViewById(R.id.tv_humidity)
        tvWindSpeed = view.findViewById(R.id.tv_wind)
        tvCloudPercentage = view.findViewById(R.id.tv_cloud)
        tvPressure = view.findViewById(R.id.tv_pressure)
        rvNextDays = view.findViewById(R.id.rv_next_days)

        dailyManager = LinearLayoutManager(context)
        dailyManager.orientation = RecyclerView.HORIZONTAL
        homeAdapter = DailyForecastAdapter(mutableListOf(), requireContext())
        rvDaily.layoutManager = dailyManager
        rvDaily.adapter = homeAdapter



        nextDaysManager = LinearLayoutManager(context)
        nextDaysManager.orientation = RecyclerView.VERTICAL
        nextDaysAdapter = NextDaysAdapter(mutableListOf(), requireContext())
        rvNextDays.layoutManager = nextDaysManager
        rvNextDays.adapter = nextDaysAdapter

        calendar = Calendar.getInstance()

    }

    private fun updateUi(root: Root) {
        setDailyListData(root.list)
        setNextDaysData(root.list)
        val dateFormat = SimpleDateFormat("EE, dd MMM", Locale.getDefault())
        val formattedDate = dateFormat.format(calendar.time)
        tvTodayDate.text = formattedDate
        tvCity.text = root.city?.name ?: "not found"
        tvDegree.text = "${(root.list.get(0).main?.temp)?.toInt()} \u00B0"
        Glide.with(this)
            .load("https://openweathermap.org/img/wn/${root.list[0].weather[0].icon}@2x.png")
            .into(mainWeatherIcon)



        tvWeatherDescription.text = root.list[0].weather[0].description
        val hours = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        if (hours > 12) {
            tvCurrentTime.text = "${hours - 12}:${minute} pm"
        } else if (hours < 12 && hours != 0) {
            tvCurrentTime.text = "${hours}:${minute} am"
        } else {
            tvCurrentTime.text = "12:${minute} am"
        }
        tvHumidityPercentage.text = "${root.list[0].main?.humidity}%"
        tvWindSpeed.text = "${root.list[0].wind?.speed}m/s"
        tvCloudPercentage.text = "${root.list[0].clouds?.all}%"
        tvPressure.text = "${root.list[0].main?.pressure}pha"

    }

    @SuppressLint("MissingPermission")
    private fun getRefreshLocation() {
        fusedLocationProvider = LocationServices.getFusedLocationProviderClient(requireActivity())
        fusedLocationProvider.requestLocationUpdates(

            LocationRequest.Builder(1000).apply {
                setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            }.build(),
            object : LocationCallback() {

                @SuppressLint("SetTextI18n")
                override fun onLocationResult(locatioResult: LocationResult) {
                    super.onLocationResult(locatioResult)
                    val lastLocation = locatioResult.lastLocation
                    val latitudeValue: Double = lastLocation?.latitude ?:0.0
                    val longitudeValue: Double = lastLocation?.longitude ?:0.0
                    viewModel.getWeatherStatus(latitudeValue,longitudeValue)
                }
            },
            Looper.myLooper()
        )
    }


}