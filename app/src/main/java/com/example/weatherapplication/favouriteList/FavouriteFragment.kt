package com.example.weatherapplication.favouriteList

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapplication.R
import com.example.weatherapplication.databinding.FragmentFavouriteBinding
import com.example.weatherapplication.home.HomeViewModel
import com.example.weatherapplication.localDataSource.WeatherLocalDataSource
import com.example.weatherapplication.model.WeatherRepository
import com.example.weatherapplication.remoteDataSource.WeatherRemoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

    private const val TAG = "FavouriteFragment"
class FavouriteFragment : Fragment() {

    private lateinit var binding: FragmentFavouriteBinding
    private lateinit var factory: FavouriteViewModelFactory
    private lateinit var viewModel: FavouriteViewModel
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var favouriteAdapter: FavouriteAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavouriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        factory = FavouriteViewModelFactory(
            repo = WeatherRepository.getInstance(
                WeatherRemoteDataSource.getInstance(),
                WeatherLocalDataSource(requireContext())
            )
        )
        initializeUi()
        viewModel = ViewModelProvider(this, factory)[FavouriteViewModel::class.java]


        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.weatherStatus.collectLatest {
                launch(Dispatchers.Main) {
                    Log.i(TAG, "onViewCreated: in view model scope $it")
                    favouriteAdapter.setList(it)
                }
            }
        }

    }

    private fun initializeUi() {
        layoutManager = LinearLayoutManager(requireContext())
        layoutManager.orientation=RecyclerView.VERTICAL
        binding.rvFavouriteList.layoutManager = layoutManager
        favouriteAdapter = FavouriteAdapter(listOf())
        binding.rvFavouriteList.adapter = favouriteAdapter
        binding.fabAddToFavourite.setOnClickListener {
            Log.i(TAG, "initializeUi: ")
            Navigation.findNavController(binding.root).navigate(FavouriteFragmentDirections.actionFavouriteFragmentToMapFragment3())
        }
    }


}