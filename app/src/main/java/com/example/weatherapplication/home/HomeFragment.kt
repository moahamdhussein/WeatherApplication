package com.example.weatherapplication.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherapplication.Constant
import com.example.weatherapplication.databinding.FragmentHomeBinding
import com.example.weatherapplication.localDataSource.WeatherLocalDataSource
import com.example.weatherapplication.model.Root
import com.example.weatherapplication.model.WeatherProperty
import com.example.weatherapplication.remoteDataSource.WeatherRemoteDataSource
import com.example.weatherapplication.repository.WeatherRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.material.snackbar.Snackbar
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = requireContext().getSharedPreferences("Setting", Context.MODE_PRIVATE)
        isEnglish = sharedPreferences.getBoolean(Constant.LANGUAGE_KEY, true)
        if (!isEnglish) {
            language = Constant.Language.ARABIC
        }
        unit = (sharedPreferences.getString(Constant.TEMPERATURE_Unit, Constant.Units.CELSIUS))
            ?: Constant.Units.CELSIUS
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeUi()

        if (connectivityManager.activeNetworkInfo?.isConnected == true) {
            getRefreshLocation()
            lifecycleScope.launch {
                viewModel.weatherStatus.collectLatest {
                    if (it.list.size > 0) {
                        viewModel.writeDataToFile(requireContext())
                        updateUi(it)
                    }
                }
            }
        } else {
            Snackbar.make(
                view,
                "Open Internet to get up to date Weather Status",
                Snackbar.LENGTH_SHORT
            ).show()
            viewModel.readDataFromFile(requireContext())
            lifecycleScope.launch{
                viewModel.weatherStatus.collectLatest {
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
                WeatherLocalDataSource(requireContext())
            )
        )
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
        connectivityManager =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    }

    @SuppressLint("SetTextI18n")
    private fun updateUi(root: Root) {

        setDailyListData(root.list)
        setNextDaysData(root.list)
        val dateFormat = SimpleDateFormat("EE, dd MMM", Locale.getDefault())
        val formattedDate = dateFormat.format(calendar.time)
        binding.tvTodayDate.text = formattedDate
        binding.tvCity.text = root.city?.name ?: "not found"
        binding.flTvDegree.text = "${(root.list[0].main?.temp)?.toInt()} \u00B0"
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
        binding.tvWind.text = "${root.list[0].wind?.speed}m/s"
        binding.tvCloud.text = "${root.list[0].clouds?.all}%"
        binding.tvPressure.text = "${root.list[0].main?.pressure}pha"
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
                    viewModel.getWeatherStatus(latitudeValue, longitudeValue, unit, language)
                    fusedLocationProvider.removeLocationUpdates(this)
                }
            },
            Looper.myLooper()

        )
    }
}