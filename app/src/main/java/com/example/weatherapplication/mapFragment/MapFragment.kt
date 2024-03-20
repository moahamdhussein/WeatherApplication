package com.example.weatherapplication.mapFragment

import android.annotation.SuppressLint
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.example.weatherapplication.R
import com.example.weatherapplication.databinding.FragmentMapBinding
import com.example.weatherapplication.localDataSource.WeatherLocalDataSource
import com.example.weatherapplication.model.FavouriteCountries
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
import kotlin.math.log

private const val TAG = "MapFragment"

class MapFragment : Fragment() {

    private lateinit var binding: FragmentMapBinding
    private lateinit var mapController: IMapController
    private lateinit var locationOverlay: MyLocationNewOverlay
    private lateinit var selectedLocationMarker: Marker


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeUi()
        setUpLocationOverLay()
        animateToUserLocation()
        updateLocationOverlay()
        binding.btnSaveLocation.setOnClickListener { onSaveClick() }



    }



    private fun initializeUi() {
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

    private fun updateLocationOverlay(){
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

            val localDataSource = WeatherLocalDataSource(requireContext())
            val cityName = getAddressFromLocation(lat,long)
            lifecycleScope.launch(Dispatchers.IO) {
                localDataSource.insertFavouriteCountry(FavouriteCountries(long,lat,cityName))
            }
            Navigation.findNavController(binding.root).navigate(MapFragmentDirections.actionMapFragmentToFavouriteFragment())
        } else {

            Log.i(TAG, "setListeners: not selected")
        }
    }
    fun getAddressFromLocation(latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(requireContext())
        val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
        Log.i(TAG, "getAddressFromLocation: "+addresses.toString())
        val cityName = addresses?.get(0)?.adminArea
        val countryName = addresses?.get(0)?.countryName

        return "${cityName?:""} $countryName"
    }


}
