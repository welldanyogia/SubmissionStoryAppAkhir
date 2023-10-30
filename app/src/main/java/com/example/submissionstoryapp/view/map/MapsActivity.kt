package com.example.submissionstoryapp.view.map

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewModelScope
import com.example.submissionstoryapp.R
import com.example.submissionstoryapp.data.api.ApiConfig
import com.example.submissionstoryapp.data.repo.StoryRepository
import com.example.submissionstoryapp.data.response.ListStoryItem

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.submissionstoryapp.databinding.ActivityMapsBinding
import com.example.submissionstoryapp.view.ViewModelFactory
import com.example.submissionstoryapp.view.main.MainActivity
import com.example.submissionstoryapp.view.main.MainViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLngBounds
import kotlinx.coroutines.launch

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val apiService = ApiConfig.getApiServiceWithToken("")
    private val storyRepository = StoryRepository(apiService)
    private val boundsBuilder = LatLngBounds.Builder()
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var token: String
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        token = intent.getStringExtra("token").toString()

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.map_option, menu)
        return true
    }
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.normal_type ->{
                mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                true
            }
            R.id.satellite_type ->{
                mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
                true
            }
            R.id.terrain_type ->{
                mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
                true
            }
            R.id.hybrid_type ->{
                mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
                true
            }
            else ->{
                super.onOptionsItemSelected(item)
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        val lat = intent.getFloatExtra("lat", 0.0f)
        val lon = intent.getFloatExtra("lon", 0.0f)
        val photo = LatLng(lat.toDouble(), lon.toDouble())
        mMap.addMarker(MarkerOptions().position(photo).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(photo))
        getMyLocation()
        fetchStories()
    }
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }
    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
    private fun fetchStories() {
        viewModel.getSession().observe(this) { user ->
            if (user.isLogin) {
                token = user.token

                viewModel.viewModelScope.launch {
                    try {
                        storyRepository.getStories(
                            token,
                            onSuccess = { storyList ->
                                addMarkersForStoryLocations(storyList)
                            },
                            onError = {
                            }
                        )
                    } catch (e: Exception) {
                        Log.d("Error", e.message.toString())
                    }
                }
            }
        }
    }
    private fun addMarkersForStoryLocations(storyLocations: List<ListStoryItem>) {
        for (story in storyLocations) {
            val lat = story.lat ?: 0.0
            val lon = story.lon ?: 0.0

            val latLng = LatLng(lat, lon)
            Log.e("latitude",lat.toString())
            mMap.addMarker(MarkerOptions().position(latLng).title(story.name ?: ""))
            boundsBuilder.include(latLng)
        }

        val bounds: LatLngBounds = boundsBuilder.build()
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds,
                resources.displayMetrics.widthPixels,
                resources.displayMetrics.heightPixels,
                300
            )
        )
    }

}