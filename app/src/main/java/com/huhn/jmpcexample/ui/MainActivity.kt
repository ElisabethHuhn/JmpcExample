package com.huhn.jmpcexample.ui

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.huhn.jmpcexample.location.LocationUtils.fetchLastLocation
import com.huhn.jmpcexample.location.LocationUtils.fetchLocation
import com.huhn.jmpcexample.naviagation.MainNavGraph
import com.huhn.jmpcexample.repository.WeatherRepositoryImpl
import com.huhn.jmpcexample.ui.theme.JmpcExampleTheme
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repo: WeatherRepositoryImpl by inject<WeatherRepositoryImpl>()

        setContent {
            JmpcExampleTheme {
                // set up location stuff in Repo
//                var locationFromGps: Location? by remember { mutableStateOf(null) }
                var openDialog: String by remember { mutableStateOf("") }

                val locationState by repo.locationState.collectAsState()

                val locationPermissionsState = rememberMultiplePermissionsState(
                    listOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                    )
                )

                val context = LocalContext.current
                val fusedLocationProviderClient =
                    remember { LocationServices.getFusedLocationProviderClient(context) }
                val locationCallback = remember {
                    object : LocationCallback() {
                        override fun onLocationResult(locationResult: LocationResult) {
                            Log.d("onLocationResult", "locationResult.latitude: ${locationResult.lastLocation?.latitude}")
                            repo.onLocationChanged(locationResult.lastLocation)
                        }
                    }
                }


                val settingsLauncher =
                    rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.StartIntentSenderForResult(),
                        onResult = { activityResult ->
                            when (activityResult.resultCode) {
                                RESULT_OK -> {
                                    context.fetchLastLocation(
                                        fusedLocationClient = fusedLocationProviderClient,
                                        settingsLauncher = null,
                                        location = {
                                            Log.d("settingsLauncher", "location: ${it.latitude}")
                                            if (locationState.location == null && locationState.location != it) {
                                                repo.onLocationChanged(it)
                                            }
                                        },
                                        locationCallback = locationCallback
                                    )
                                }
                                RESULT_CANCELED -> {
                                    Toast.makeText(context, "Activity.RESULT_CANCELED", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    )

                LaunchedEffect(
                    key1 = locationPermissionsState.revokedPermissions.size,
                    key2 = locationPermissionsState.shouldShowRationale)
                {
                    fetchLocation(
                        locationPermissionsState,
                        context,
                        settingsLauncher,
                        fusedLocationProviderClient,
                        locationCallback,
                        openDialog = {
                            openDialog = it
                        }
                    )
                }

                LaunchedEffect(
                    key1 = locationState.location
                ) {
                    Log.d("LaunchedEffect", "locationFromGps: ${locationState.location}")
                    // TODO: setup GeoCoder

                }

                DisposableEffect(
                    key1 = true
                ) {
                    onDispose {
                        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
                    }
                }


                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainNavGraph()
//                    Greeting(name = "Elisabeth")
                }
            }
        }
    }



}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    JmpcExampleTheme {
        Greeting("Android")
    }
}