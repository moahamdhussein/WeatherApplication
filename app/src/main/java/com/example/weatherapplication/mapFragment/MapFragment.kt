package com.example.weatherapplication.mapFragment

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.net.ConnectivityManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.example.weatherapplication.R
import com.example.weatherapplication.alarm.AlarmReceiver
import com.example.weatherapplication.databinding.FragmentMapBinding
import com.example.weatherapplication.localDataSource.WeatherDatabase
import com.example.weatherapplication.localDataSource.WeatherLocalDataSource
import com.example.weatherapplication.model.FavouriteCountries
import com.example.weatherapplication.remoteDataSource.WeatherRemoteDataSource
import com.example.weatherapplication.repository.WeatherRepository
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.library.BuildConfig
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.util.Calendar
import kotlin.math.log

private const val TAG = "MapFragment"

class MapFragment : Fragment() {

    private lateinit var binding: FragmentMapBinding
    private lateinit var mapController: IMapController
    private lateinit var locationOverlay: MyLocationNewOverlay
    private lateinit var selectedLocationMarker: Marker
    private lateinit var type: String
    private lateinit var date: String
    private lateinit var factory: MapViewModelFactory
    private lateinit var viewModel: MapViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        type = MapFragmentArgs.fromBundle(requireArguments()).type
        date = MapFragmentArgs.fromBundle(requireArguments()).date
        initializeUi()
        setUpLocationOverLay()
        animateToUserLocation()
        updateLocationOverlay()
        binding.btnSaveLocation.setOnClickListener { onSaveClick() }
    }


    private fun initializeUi() {
        factory = MapViewModelFactory(
            WeatherRepository.getInstance(
                WeatherRemoteDataSource.getInstance(),
                WeatherLocalDataSource(
                    WeatherDatabase.getInstance(requireContext()).getFavouriteDao()
                )
            )
        )
        viewModel = ViewModelProvider(this, factory)[MapViewModel::class.java]
        Configuration.getInstance()
            .load(requireContext(), PreferenceManager.getDefaultSharedPreferences(requireContext()))
        Configuration.getInstance().userAgentValue = BuildConfig.LIBRARY_PACKAGE_NAME
        binding.mapView.apply {
            setUseDataConnection(true)
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            mapController = controller
            mapController.setZoom(3.0)
        }

    }

    private fun setUpLocationOverLay() {
        val gpsMyLocationProvider = GpsMyLocationProvider(requireContext())
        locationOverlay = MyLocationNewOverlay(gpsMyLocationProvider, binding.mapView)
        locationOverlay.apply {
            enableMyLocation()
            enableFollowLocation()
        }
        binding.mapView.overlays.add(locationOverlay)
    }

    private fun animateToUserLocation() {
        locationOverlay.runOnFirstFix {
            requireActivity().runOnUiThread {
                mapController.animateTo(locationOverlay.myLocation)
            }
        }

    }


    private fun updateSelectedLocationMarker(location: GeoPoint) {
        if (!::selectedLocationMarker.isInitialized) {
            selectedLocationMarker = Marker(binding.mapView).apply {
                icon = resources.getDrawable(R.drawable.location_icon_red)
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            }
            binding.mapView.overlays.add(selectedLocationMarker)
        }

        selectedLocationMarker.position = location
        selectedLocationMarker.setInfoWindow(null)
        selectedLocationMarker.title = "Selected Location"
        selectedLocationMarker.snippet = "${location.latitude}, ${location.longitude}"
        selectedLocationMarker.showInfoWindow()
        binding.mapView.invalidate()
    }

    private fun updateLocationOverlay() {
        binding.mapView.overlays.add(MapEventsOverlay(object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                return true
            }

            override fun longPressHelper(p: GeoPoint?): Boolean {
                p?.let {
                    updateSelectedLocationMarker(it)
                }
                return false
            }
        }))
    }

    private fun onSaveClick() {
        if (::selectedLocationMarker.isInitialized) {
            val selectedLocation = selectedLocationMarker.position
            val lat = selectedLocation.latitude
            val long = selectedLocation.longitude
            if (type == "changeMainLocation") {
                val sharedPreferences =
                    requireContext().getSharedPreferences("Setting", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("mainLat", "$lat")
                editor.putString("mainLon", "$long")
                editor.apply()
            } else {
                val cityName = getAddressFromLocation(lat, long)
                var alarmId = Int.MIN_VALUE
                if (type == "Alarm") {
                    alarmId = (1..250).random()
                    scheduleAlarm(requireContext(), date.toLong(), alarmId, lat, long)
                }
                viewModel.insertFavourite(
                    FavouriteCountries(
                        long,
                        lat,
                        cityName,
                        type,
                        date,
                        alarmId
                    )
                )
            }
            Navigation.findNavController(binding.root).popBackStack()
        } else {

            view?.let {
                Snackbar.make(
                    it,
                    "please pickUp location by press and hold in any location you need",
                    Snackbar.ANIMATION_MODE_SLIDE
                ).show()
            }
        }
    }

    fun getAddressFromLocation(latitude: Double, longitude: Double): String {
        val connectivityManager =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager.activeNetworkInfo?.isConnected == false) {
            return "no internet to get city name"
        }
        val geocoder = Geocoder(requireContext())
        val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
        val cityName = addresses?.get(0)?.adminArea
        val countryName = addresses?.get(0)?.countryName
        return "${cityName?.split("G", "g")?.get(0) ?: ""} $countryName"
    }

    fun scheduleAlarm(
        context: Context,
        triggerAtMillis: Long,
        alarmId: Int,
        lat: Double,
        lon: Double
    ) {
        val alarmManager = context.getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra("lat", lat)
        intent.putExtra("lon", lon)
        val pendingIntent = PendingIntent.getBroadcast(
            context, alarmId, intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
    }
}
