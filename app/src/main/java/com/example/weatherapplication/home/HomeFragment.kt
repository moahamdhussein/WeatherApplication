package com.example.weatherapplication.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.weatherapplication.R
import com.example.weatherapplication.model.WeatherRepository
import com.example.weatherapplication.remoteDataSource.WeatherRemoteDataSource
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val TAG = "HomeFragment"
class HomeFragment : Fragment() {
    lateinit var viewModel: HomeViewModel
    lateinit var factory: HomeViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        factory = HomeViewModelFactory(repo = WeatherRepository.getInstance(WeatherRemoteDataSource.getInstance()))
        viewModel = ViewModelProvider(this,factory).get(HomeViewModel::class.java)

        lifecycleScope.launch {
            viewModel.weatherStatus.collectLatest {
                Log.i(TAG, "onViewCreated: $it")
            }
        }
    }


}