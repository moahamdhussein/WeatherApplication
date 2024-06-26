package com.example.weatherapplication.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherapplication.databinding.FragmentHomeBinding
import com.example.weatherapplication.localDataSource.WeatherDatabase
import com.example.weatherapplication.localDataSource.WeatherLocalDataSource
import com.example.weatherapplication.model.Root
import com.example.weatherapplication.model.WeatherProperty
import com.example.weatherapplication.remoteDataSource.WeatherRemoteDataSource
import com.example.weatherapplication.repository.WeatherRepository
import com.example.weatherapplication.utility.ApiState
import com.example.weatherapplication.utility.Constant
import com.github.matteobattilana.weather.PrecipType
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

class HomeFragment : Fragment() {
    private lateinit var viewModel: HomeViewModel
    private lateinit var factory: HomeViewModelFactory
    private lateinit var nextDaysManager: LinearLayoutManager
    private lateinit var dailyManager: LinearLayoutManager
    private lateinit var homeAdapter: DailyForecastAdapter
    private lateinit var nextDaysAdapter: NextDaysAdapter
    private lateinit var calendar: Calendar
    private lateinit var fusedLocationProvider: FusedLocationProviderClient
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var binding: FragmentHomeBinding
    private lateinit var sharedPreferences: SharedPreferences
    private var isEnglish: Boolean = false
    private var language: String = Constant.Language.ENGLISH
    private lateinit var unit: String
    private var convertCelsiusMultiplayer = 1.0
    private var convertCelsiusAddition = 0.0
    private var isMeter: Boolean? = null
    private var lat: String? = null
    private var longtuide: String? = null
    private var isGps: Boolean? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = requireContext().getSharedPreferences("Setting", Context.MODE_PRIVATE)
        isEnglish = sharedPreferences.getBoolean(Constant.LANGUAGE_KEY, true)
        if (!isEnglish) {
            language = Constant.Language.ARABIC
        }
        unit = (sharedPreferences.getString(Constant.TEMPERATURE_Unit, Constant.Units.CELSIUS))
            ?: Constant.Units.CELSIUS

        isMeter = sharedPreferences.getBoolean(Constant.WIND_SPEED_UNIT, true)


        when (unit) {
            Constant.Units.FAHRENHEIT -> {
                convertCelsiusMultiplayer = 9.0 / 5.0
                convertCelsiusAddition = 32.0
            }

            Constant.Units.KELVIN -> convertCelsiusAddition = 273.15
        }

        isGps = sharedPreferences.getBoolean(Constant.LOCATION_KEY,true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        lat = HomeFragmentArgs.fromBundle(requireArguments()).lat
        longtuide = HomeFragmentArgs.fromBundle(requireArguments()).longtude
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        changeVisibility(View.GONE)
        initializeUi()

        if (connectivityManager.activeNetworkInfo?.isConnected == true) {
            if (longtuide.equals("notFound", true) && lat.equals("notFound", true)) {
                if (isGps == true){
                    if (isLocationEnabled()){
                    getRefreshLocation()
                    }else{
                        enableLocationServes()
                    }
                }else{
                    val lat = sharedPreferences.getString("mainLat","30.00000")?.toDouble() ?:0.0
                    val lon =sharedPreferences.getString("mainLon","32.0000")?.toDouble() ?: 0.0
                    viewModel.getWeatherStatus(lat,lon,language)
                }

                lifecycleScope.launch(Dispatchers.IO) {
                    viewModel.weatherStatus.collectLatest {
                        launch(Dispatchers.Main) {
                            when (it) {
                                is ApiState.Success -> {
                                    viewModel.writeDataToFile(requireContext())
                                    updateUi(it.root)
                                }

                                is ApiState.Failure -> {

                                }

                                is ApiState.Loading -> {

                                }
                            }
                        }
                    }
                }
            } else {
                viewModel.getWeatherStatus(lat?.toDouble(), longtuide?.toDouble(), language)
                lifecycleScope.launch(Dispatchers.IO) {
                    viewModel.weatherStatus.collectLatest {
                        launch(Dispatchers.Main) {
                            when (it) {
                                is ApiState.Success -> {
                                    updateUi(it.root)
                                }

                                is ApiState.Failure -> {

                                }

                                is ApiState.Loading -> {

                                }
                            }
                        }
                    }
                }
            }
        } else {
            Toast.makeText(
                requireContext(),
                "Open Internet to get up to date Weather Status ",
                Toast.LENGTH_SHORT
            ).show()
            viewModel.readDataFromFile(requireContext())
            lifecycleScope.launch(Dispatchers.IO) {
                viewModel.weatherStatus.collectLatest {
                    launch(Dispatchers.Main) {
                        when (it) {
                            is ApiState.Success -> {
                                updateUi(it.root)
                            }

                            is ApiState.Failure -> {

                            }

                            is ApiState.Loading -> {

                            }
                        }
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

    private fun initializeUi() {
        dailyManager = LinearLayoutManager(context)
        dailyManager.orientation = RecyclerView.HORIZONTAL
        homeAdapter = DailyForecastAdapter(mutableListOf(), requireContext())
        binding.rvDailyForecast.layoutManager = dailyManager
        binding.rvDailyForecast.adapter = homeAdapter

        nextDaysManager = LinearLayoutManager(context)
        nextDaysManager.orientation = RecyclerView.VERTICAL
        nextDaysAdapter = NextDaysAdapter(mutableListOf(), requireContext())
        binding.rvNextDays.layoutManager = nextDaysManager
        binding.rvNextDays.adapter = nextDaysAdapter

        calendar = Calendar.getInstance()

        factory = HomeViewModelFactory(
            repo = WeatherRepository.getInstance(
                WeatherRemoteDataSource.getInstance(),
                WeatherLocalDataSource(
                    WeatherDatabase.getInstance(requireContext()).getCountriesDao()
                )
            )
        )
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]

        connectivityManager =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    @SuppressLint("SetTextI18n")
    private fun updateUi(root: Root) {
        binding.loadingAnimation.visibility = View.GONE
        changeVisibility(View.VISIBLE)
        setDailyListData(root.list)
        setNextDaysData(root.list)
        val dateFormat = SimpleDateFormat("EE, dd MMM", Locale.getDefault())
        val formattedDate = dateFormat.format(calendar.time)
        binding.tvTodayDate.text = formattedDate
        binding.tvCity.text = root.city?.name ?: "not found"


        val temperature = root.list[0].main?.temp ?: 0.0
        binding.flTvDegree.text =
            "${((temperature * convertCelsiusMultiplayer) + convertCelsiusAddition).toInt()}°"
        Glide.with(this)
            .load("https://openweathermap.org/img/wn/${root.list[0].weather[0].icon}@2x.png")
            .into(binding.flIvMainWeatherIcon)

        binding.flTvWeatherDescription.text = root.list[0].weather[0].description
        val hours = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        if (hours > 12) {
            binding.flTvCurrentTime.text = "${hours - 12}:${minute} pm"
        } else if (hours < 12 && hours != 0) {
            binding.flTvCurrentTime.text = "${hours}:${minute} am"
        } else {
            binding.flTvCurrentTime.text = "12:${minute} am"
        }
        binding.tvHumidity.text = "${root.list[0].main?.humidity}%"

        if (isMeter != false) {
            binding.tvWind.text = "${root.list[0].wind?.speed}m/s"
        } else {
            binding.tvWind.text = "${(root.list[0].wind?.speed?.times(2.23694)?.toInt())}ml/h"
        }
        if (root.list[0].weather[0].main?.equals("Clear",ignoreCase = true) ==true){
            binding.weatherBackground.setWeatherData(PrecipType.CLEAR)
        }else if (root.list[0].weather[0].main?.equals("snow",ignoreCase = true) ==true){
            binding.weatherBackground.setWeatherData(PrecipType.SNOW)
        }else if (root.list[0].weather[0].main?.equals("rain",ignoreCase = true) ==true){
            binding.weatherBackground.setWeatherData(PrecipType.RAIN)
        }else{
            binding.weatherBackground.setWeatherData(PrecipType.CLEAR)
        }


        binding.tvCloud.text = "${root.list[0].clouds?.all}%"
        binding.tvPressure.text = "${root.list[0].main?.pressure}pha"
    }


    private fun changeVisibility(visibilityState: Int) {

        binding.tvTodayDate.visibility = visibilityState
        binding.locationIcon.visibility = visibilityState
        binding.tvCity.visibility = visibilityState
        binding.flMainView.visibility = visibilityState
        binding.humidityIcon.visibility = visibilityState
        binding.tvHumidity.visibility = visibilityState
        binding.windIcon.visibility = visibilityState
        binding.tvWind.visibility = visibilityState
        binding.cloudIcon.visibility = visibilityState
        binding.tvCloud.visibility = visibilityState
        binding.pressureIcon.visibility = visibilityState
        binding.tvPressure.visibility = visibilityState
        binding.tvDailyForecastTitle.visibility = visibilityState
        binding.rvDailyForecast.visibility = visibilityState
        binding.rvNextDays.visibility = visibilityState


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
                    val latitudeValue: Double = lastLocation?.latitude ?: 0.0
                    val longitudeValue: Double = lastLocation?.longitude ?: 0.0
                    viewModel.getWeatherStatus(latitudeValue, longitudeValue, language)
                    fusedLocationProvider.removeLocationUpdates(this)
                }
            },
            Looper.myLooper()

        )
    }

    
    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
    private fun enableLocationServes() {
        Toast.makeText(requireContext(), "open location", Toast.LENGTH_SHORT).show()
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
        getRefreshLocation()
    }
}