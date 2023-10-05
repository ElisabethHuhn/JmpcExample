package com.huhn.jmpcexample.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*

object LocationUtils {

    @OptIn(ExperimentalPermissionsApi::class)
    fun fetchLocation(
        locationPermissionsState: MultiplePermissionsState,
        context: Context,
        settingsLauncher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>,
        fusedLocationProviderClient: FusedLocationProviderClient,
        locationCallback: LocationCallback,
        openDialog: (String) -> Unit
    ) {
        when {
            locationPermissionsState.revokedPermissions.size <= 1 -> {
                // Has permission at least one permission [coarse or fine]
                context.createLocationRequest(
                    settingsLauncher = settingsLauncher,
                    fusedLocationClient = fusedLocationProviderClient,
                    locationCallback = locationCallback
                )
                Log.d("LaunchedEffect", "revokedPermissions.size <= 1")
            }
            locationPermissionsState.shouldShowRationale -> {
                openDialog("Should show rationale")
                Log.d("LaunchedEffect", "shouldShowRationale")
            }
            locationPermissionsState.revokedPermissions.size == 2 -> {
                locationPermissionsState.launchMultiplePermissionRequest()
                Log.d("LaunchedEffect", "revokedPermissions.size == 2")
            }
            else -> {
                openDialog("This app requires location permission")
                Log.d("LaunchedEffect", "else")
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun Context.fetchLastLocation(
        fusedLocationClient: FusedLocationProviderClient,
        settingsLauncher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>?,
        location: (Location) -> Unit,
        locationCallback: LocationCallback
    ) {
        fusedLocationClient.lastLocation.addOnSuccessListener {
            if (it != null) {
                location(it)
            } else {
                this.createLocationRequest(
                    settingsLauncher = settingsLauncher,
                    fusedLocationClient = fusedLocationClient,
                    locationCallback = locationCallback
                )
            }
        }
    }

    @SuppressLint("MissingPermission", "LongLogTag")
    fun Context.createLocationRequest(
        settingsLauncher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>?,
        fusedLocationClient: FusedLocationProviderClient,
        locationCallback: LocationCallback
    ) {

        val locationRequest = LocationRequest.create().apply {
            priority = Priority.PRIORITY_HIGH_ACCURACY
            interval = 1 * 1000
            isWaitForAccurateLocation = true

        }
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val client = LocationServices.getSettingsClient(this)
        val task = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                mainLooper
            )
        }

        task.addOnFailureListener { exception ->
            Log.e("LocationUtil.createLocationRequest", exception.toString())
            if (exception is ResolvableApiException) {
                try {
                    settingsLauncher?.launch(
                        IntentSenderRequest.Builder(exception.resolution).build()
                    )
                } catch (e: Exception) {
                    // Ignore the error.
                }
            }
        }
    }

}

//val context = LocalContext.current
//val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
//fusedLocationClient.lastLocation
//.addOnSuccessListener { location : Location? ->
//    // Got last known location. In some rare situations this can be null.
//    onUserEvent(WeatherUserEvent.OnFetchLocation(location))
//}
//
//val locationPermissionsAlreadyGranted = ContextCompat.checkSelfPermission(
//    context,
//    Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
//
//
//val locationPermissionLauncher =
//    rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.RequestPermission(),
//        onResult = { isPermissionGranted ->
//
//            if (isPermissionGranted) {
//                //Logic when the permissions were granted by the user
//            } else {
//                Toast.makeText(
//                    context,
//                    "Need locations permission to show weather at current location",
//                    Toast.LENGTH_SHORT)
//                    .show()
//            }
//        })
//
//LaunchedEffect(key1 = "firstTimeOnly") {
//    locationPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
//}

//
//
//private fun getCurrentLocation(context: Context, client: FusedLocationProviderClient) {
//    if (/* ActivityCompat.checkSelfPermission(
//            context,
//            Manifest.permission.ACCESS_FINE_LOCATION
//        ) != PackageManager.PERMISSION_GRANTED && */
//        ActivityCompat.checkSelfPermission(
//            context,
//            Manifest.permission.ACCESS_COARSE_LOCATION
//        ) != PackageManager.PERMISSION_GRANTED
//    ) {
//        // TODO: Consider calling
//        //    ActivityCompat#requestPermissions
//        // here to request the missing permissions, and then overriding
//        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//        //                                          int[] grantResults)
//        // to handle the case where the user grants the permission. See the documentation
//        // for ActivityCompat#requestPermissions for more details.
//        return
//    }
//
//    client.getCurrentLocation(
//        Priority.PRIORITY_BALANCED_POWER_ACCURACY,
//        null
//    ).addOnSuccessListener {
//        val location: Location = it
//    }
//}
