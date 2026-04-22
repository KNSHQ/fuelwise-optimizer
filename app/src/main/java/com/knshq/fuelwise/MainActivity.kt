package com.knshq.fuelwise

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.knshq.fuelwise.ui.theme.FuelWiseTheme
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.FusedLocationProviderClient

class MainActivity : ComponentActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var latitude by mutableStateOf("Fetching...")
    private var longitude by mutableStateOf("Fetching...")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        requestLocationPermission()
        enableEdgeToEdge()
        setContent {
            FuelWiseTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LocationScreen(
                        latitude = latitude,
                        longitude = longitude,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getLocation()
        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) return

        val request = com.google.android.gms.location.LocationRequest.Builder(
            com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY,
            1000
        ).build()

        fusedLocationClient.requestLocationUpdates(
            request,
            object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    val location = locationResult.lastLocation
                    if (location != null) {
                        latitude = location.latitude.toString()
                        longitude = location.longitude.toString()
                        android.util.Log.d(
                            "FuelWise",
                            "Latitude: ${location.latitude}, Longitude: ${location.longitude}"
                        )

                        fusedLocationClient.removeLocationUpdates(this)
                    }
                }
            },
            mainLooper
        )
    }
    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                getLocation()
            }
        }
}

@Composable
fun LocationScreen(
    latitude: String,
    longitude: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = "Lat: $latitude\nLon: $longitude",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun LocationPreview() {
    FuelWiseTheme {
        LocationScreen(
            latitude = "-37.8136",
            longitude = "144.9631"
        )
    }
}