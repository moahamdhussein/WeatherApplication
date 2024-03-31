package com.example.weatherapplication.favouriteList

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapplication.databinding.FragmentFavouriteBinding
import com.example.weatherapplication.localDataSource.WeatherDatabase
import com.example.weatherapplication.localDataSource.WeatherLocalDataSource
import com.example.weatherapplication.model.FavouriteCountries
import com.example.weatherapplication.remoteDataSource.WeatherRemoteDataSource
import com.example.weatherapplication.repository.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class FavouriteFragment : Fragment(), IFavouriteFragment {

    private lateinit var binding: FragmentFavouriteBinding
    private lateinit var factory: FavouriteViewModelFactory
    private lateinit var viewModel: FavouriteViewModel
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var favouriteAdapter: FavouriteAdapter
    private lateinit var connectivityManager: ConnectivityManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavouriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        connectivityManager =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        factory = FavouriteViewModelFactory(
            repo = WeatherRepository.getInstance(
                WeatherRemoteDataSource.getInstance(),
                WeatherLocalDataSource(
                    WeatherDatabase.getInstance(requireContext()).getCountriesDao()
                )
            )
        )
        initializeUi()
        viewModel = ViewModelProvider(this, factory)[FavouriteViewModel::class.java]


        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.weatherStatus.collectLatest {
                launch(Dispatchers.Main) {
                    favouriteAdapter.setList(it)
                }
            }
        }

    }

    private fun initializeUi() {
        layoutManager = LinearLayoutManager(requireContext())
        layoutManager.orientation = RecyclerView.VERTICAL
        binding.rvFavouriteList.layoutManager = layoutManager
        favouriteAdapter = FavouriteAdapter(listOf(), this)
        binding.rvFavouriteList.adapter = favouriteAdapter
        binding.fabAddToFavourite.setOnClickListener {
            if (connectivityManager.activeNetworkInfo?.isConnected == true) {
                Navigation.findNavController(binding.root)
                    .navigate(
                        FavouriteFragmentDirections.actionFavouriteFragmentToMapFragment3(
                            "Favourite",
                            "-"
                        )
                    )
            } else {
                Toast.makeText(
                    requireContext(),
                    "please open internet to view more details",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun deleteItem(favouriteCountries: FavouriteCountries) {
        viewModel.deleteFavouriteCountry(favouriteCountries)
    }

    override fun onItemClick(favouriteCountries: FavouriteCountries) {
        if (connectivityManager.activeNetworkInfo?.isConnected == true) {
            val action = FavouriteFragmentDirections.actionFavouriteFragmentToHomeFragment2(
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
}