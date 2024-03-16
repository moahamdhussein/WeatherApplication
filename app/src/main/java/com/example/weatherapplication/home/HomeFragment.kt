package com.example.weatherapplication.home

import android.os.Bundle
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
import com.example.weatherapplication.R
import com.example.weatherapplication.model.Root
import com.example.weatherapplication.model.WeatherProperty
import com.example.weatherapplication.model.WeatherRepository
import com.example.weatherapplication.remoteDataSource.WeatherRemoteDataSource
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
    private lateinit var rvNextDays:RecyclerView
    private lateinit var nextDaysManager:LinearLayoutManager
    private lateinit var dailyManager: LinearLayoutManager
    private lateinit var homeAdapter: DailyForecastAdapter
    private lateinit var nextDaysAdapter:NextDaysAdapter
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
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        calendar = Calendar.getInstance()
        factory =
            HomeViewModelFactory(repo = WeatherRepository.getInstance(WeatherRemoteDataSource.getInstance()))
        viewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)
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
        var time = (list[0].dtTxt?.split(" ")?.get(1)?.split(":")?.get(0))?.toInt()
        Log.i(TAG, "addData: ${(24 - time!!) / 3}")
        homeAdapter.setList(list.subList(0, (24 - time!!) / 3))
        setNextDaysData(list)

    }

    private fun setNextDaysData(list: MutableList<WeatherProperty>){
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



        nextDaysManager=LinearLayoutManager(context)
        nextDaysManager.orientation=RecyclerView.VERTICAL
        nextDaysAdapter= NextDaysAdapter(mutableListOf(),requireContext())
        rvNextDays.layoutManager=nextDaysManager
        rvNextDays.adapter=nextDaysAdapter

    }

    private fun updateUi(root: Root) {
        setDailyListData(root.list)
        val dateFormat = SimpleDateFormat("EE, dd MMM", Locale.getDefault())
        val formattedDate = dateFormat.format(calendar.time)
        tvTodayDate.text = formattedDate
        tvCity.text = root.city?.name ?: "not found"
        tvDegree.text = "${(root.list.get(0).main?.temp)?.toInt()} \u00B0"
        mainWeatherIcon.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.sunny
            )
        )
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


}