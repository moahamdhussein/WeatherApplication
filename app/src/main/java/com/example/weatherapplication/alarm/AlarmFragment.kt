package com.example.weatherapplication.alarm

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapplication.databinding.FragmentAlarmBinding
import com.example.weatherapplication.localDataSource.WeatherDatabase
import com.example.weatherapplication.localDataSource.WeatherLocalDataSource
import com.example.weatherapplication.model.FavouriteCountries
import com.example.weatherapplication.remoteDataSource.WeatherRemoteDataSource
import com.example.weatherapplication.repository.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar


class AlarmFragment : Fragment(), IAlarmFragment {
    private lateinit var binding: FragmentAlarmBinding
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var alarmAdapter: AlarmAdapter
    private lateinit var factory: AlarmViewModelFactory
    private lateinit var viewModel: AlarmViewModel
    private lateinit var calendar: Calendar
    private lateinit var connectivityManager: ConnectivityManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        calendar = Calendar.getInstance()
        binding = FragmentAlarmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        connectivityManager =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        factory = AlarmViewModelFactory(
            repo = WeatherRepository.getInstance(
                WeatherRemoteDataSource.getInstance(),
                WeatherLocalDataSource(
                    WeatherDatabase.getInstance(requireContext()).getCountriesDao()
                )
            )
        )
        initializeUi()
        viewModel = ViewModelProvider(this, factory)[AlarmViewModel::class.java]

        lifecycleScope.launch(Dispatchers.IO) {

            viewModel.weatherStatus.collectLatest {
                launch(Dispatchers.Main) {
                    it.let {
                        alarmAdapter.setData(it)
                    }
                }
            }
        }
    }

    private fun initializeUi() {
        linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        binding.rvAlarmList.layoutManager = linearLayoutManager
        alarmAdapter = AlarmAdapter(listOf(), this)
        binding.rvAlarmList.adapter = alarmAdapter
        binding.fabCreateAlarm.setOnClickListener {
            if (connectivityManager.activeNetworkInfo?.isConnected == true) {
                pickTimeAndDate()
            } else {
                Toast.makeText(
                    requireContext(),
                    "please open internet to view more details",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun pickTimeAndDate() {
        val year: Int = calendar.get(Calendar.YEAR)
        val month: Int = calendar.get(Calendar.MONTH)
        val dayOfMonth: Int = calendar.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, monthOfYear, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, monthOfYear)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                Navigation.findNavController(requireView()).navigate(
                    AlarmFragmentDirections.actionAlarmFragmentToMapFragment(
                        "Alarm",
                        "${calendar.timeInMillis}"
                    )
                )
            },
            year,
            month,
            dayOfMonth
        )
        val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _: TimePicker?, hourOfDay: Int, minute: Int ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute - 1)
                datePickerDialog.show()
            }, hourOfDay, minute, true
        )
        timePickerDialog.show()
    }

    override fun deleteAlarmItem(favouriteCountries: FavouriteCountries) {
        stopAlarm(favouriteCountries)
        viewModel.deleteAlarmItem(favouriteCountries)
    }

    override fun onItemClick(favouriteCountries: FavouriteCountries) {
        if (connectivityManager.activeNetworkInfo?.isConnected == true) {

            val action = AlarmFragmentDirections.actionAlarmFragmentToHomeFragment2(
                "${favouriteCountries.longitude}",
                "${favouriteCountries.latitude}"
            )
            Navigation.findNavController(binding.root)
                .navigate(
                    action
                )
        } else {
            Toast.makeText(
                requireContext(),
                "please open internet to view more details",
                Toast.LENGTH_SHORT
            ).show()

        }
    }

    private fun stopAlarm(favouriteCountries: FavouriteCountries) {
        val alarmManager =
            requireContext().getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(), favouriteCountries.alarmId, intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)

    }
}