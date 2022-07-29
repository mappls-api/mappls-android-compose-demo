package com.mappls.sdk.demo.sample.activities.camera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.mappls.sdk.demo.MapplsMap
import com.mappls.sdk.demo.R
import com.mappls.sdk.maps.MapplsMap
import com.mappls.sdk.maps.Style
import com.mappls.sdk.maps.location.LocationComponent
import com.mappls.sdk.maps.location.LocationComponentActivationOptions
import com.mappls.sdk.maps.location.LocationComponentOptions
import com.mappls.sdk.maps.location.OnCameraTrackingChangedListener
import com.mappls.sdk.maps.location.engine.LocationEngine
import com.mappls.sdk.maps.location.engine.LocationEngineCallback
import com.mappls.sdk.maps.location.engine.LocationEngineRequest
import com.mappls.sdk.maps.location.engine.LocationEngineResult
import com.mappls.sdk.maps.location.modes.CameraMode
import com.mappls.sdk.maps.location.modes.RenderMode


var locationEngine: LocationEngine? = null
var locationComponent: LocationComponent? = null

class LocationCameraOptionActivity : ComponentActivity() {

    private val locationEngineResult = object : LocationEngineCallback<LocationEngineResult> {
        override fun onSuccess(p0: LocationEngineResult?) {
            Log.e("TAG", "onSuccess: ")
//                                    if (p0 != null) {
//                                        val location = p0.lastLocation
//                                        location?.let {
//                                            mapplsMap.animateCamera(
//                                                CameraUpdateFactory.newLatLngZoom(
//                                                    LatLng(location.latitude, location.longitude),
//                                                    16.0
//                                                )
//                                            )
//                                        }
//
//                                    }
        }

        override fun onFailure(p0: Exception) {
//                                    p0.stackTrace
            Log.e("TAG", "Exception: ")

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LocationCameraOptionScreen(locationEngineResult)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        locationEngine?.removeLocationUpdates(locationEngineResult)
        locationEngine = null
    }

}

@Composable
fun LocationCameraOptionScreen(locationEngineResult: LocationEngineCallback<LocationEngineResult>) {
    val context = LocalContext.current
    val selectedMode = remember { mutableStateOf("Normal") }
    val selectedTracking = remember { mutableStateOf("None") }
    val trackingDialog = remember { mutableStateOf(false) }



    Scaffold(topBar = {
        TopAppBar(title = { Text(text = "Camera Features") })
    }) {
        val modeDialog = remember { mutableStateOf(false) }


        Column {

            MapplsMap(
                modifier = Modifier.weight(1f),
                onSuccess = { map, mapplsMap ->
                    mapplsMap.uiSettings?.setLogoMargins(0, 0, 0, 100)
                    mapplsMap.getStyle {
                        enableLocation(
                            mapplsMap,
                            it,
                            context,
                            locationEngineResult,
                            object : OnCameraTrackingChangedListener {
                                override fun onCameraTrackingDismissed() {
                                    selectedTracking.value = "None"
                                    Log.e("TAG", "onCameraTrackingDismissed: ")

                                }

                                override fun onCameraTrackingChanged(currentMode: Int) {
                                    Log.e("TAG", "onCameraTrackingChanged: ")

                                    when (currentMode) {
                                        CameraMode.NONE -> {
                                            selectedTracking.value = "None"
                                        }
                                        CameraMode.TRACKING -> {
                                            selectedTracking.value = "Tracking"
                                        }
                                        CameraMode.TRACKING_COMPASS -> {
                                            selectedTracking.value = "Tracking Compass"
                                        }
                                        CameraMode.TRACKING_GPS -> {
                                            selectedTracking.value = "Tracking GPS"
                                        }
                                        CameraMode.TRACKING_GPS_NORTH -> {
                                            selectedTracking.value = "Tracking GPS North"
                                        }
                                        CameraMode.NONE_COMPASS -> {
                                            selectedTracking.value = "None Compass"
                                        }
                                        CameraMode.NONE_GPS -> {
                                            selectedTracking.value = "None GPS"
                                        }
                                    }
                                }
                            })
                    }
                },

                onError = { _, _ ->
                }
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colorResource(id = R.color.purple_500))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Text(
                    text = "Mode:", color = Color.White,
                    modifier = Modifier
                )
                Text(
                    text = selectedMode.value, color = Color.White,
                    modifier = Modifier
                        .clickable(enabled = true) {

                            modeDialog.value = true
                        },
                )
                Text(
                    text = "Tracking:", color = Color.White,
                )
                Text(
                    text = selectedTracking.value, color = Color.White,
                    modifier = Modifier
                        .clickable(enabled = true) {
                            trackingDialog.value = true
                        },
                )
            }
            if (modeDialog.value) {
                SingleSelectDialog(title = "Camera Move options",
                    optionsList = listOf("Normal", "Compasss", "GPS"),
                    onSubmitButtonClick = {
                        selectedMode.value = it
                        setRenderMode(it)

                    },
                    onDismissRequest = { modeDialog.value = false })
            }

            if (trackingDialog.value) {
                SingleSelectDialog(title = "Camera Move options",
                    optionsList = listOf(
                        "None",
                        "None Compass",
                        "Non GPS",
                        "Tracking",
                        "Tracking Compass",
                        "Tracking GPS",
                        "Tracking GPS North"
                    ),

                    onSubmitButtonClick = {
                        setCameraMode(it)
                    },
                    onDismissRequest = { trackingDialog.value = false })
            }
        }
    }


}



@Composable
fun SingleSelectDialog(
    title: String,
    optionsList: List<String>,
    onSubmitButtonClick: (String) -> Unit,
    onDismissRequest: () -> Unit
) {


    Dialog(onDismissRequest = { onDismissRequest.invoke() }) {
        Surface(
            shape = RoundedCornerShape(10.dp)
        ) {

            Column(modifier = Modifier.padding(10.dp)) {

                Text(text = title)

                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(verticalArrangement = Arrangement.Center) {
                    items(optionsList) { item ->
                        Text(
                            text = item,
                            modifier = Modifier
                                .padding(16.dp)
                                .clickable(enabled = true) {
                                    onSubmitButtonClick.invoke(item)
                                    onDismissRequest.invoke()
                                },
                        )
                    }

                }

            }

        }
    }
}


fun enableLocation(
    mapplsMap: MapplsMap,
    style: Style,
    context: Context,
    locationEngineCallback: LocationEngineCallback<LocationEngineResult>,
    cameraTrackingCallback: OnCameraTrackingChangedListener
) {
    val options: LocationComponentOptions = LocationComponentOptions.builder(context)
        .trackingGesturesManagement(true)
        .accuracyColor(ContextCompat.getColor(context, R.color.purple_700))
        .build()
    locationComponent = mapplsMap.locationComponent
    val locationComponentActivationOptions =
        LocationComponentActivationOptions.builder(context, style)
            .locationComponentOptions(options)
            .build()
    locationComponent?.activateLocationComponent(locationComponentActivationOptions)
    locationComponent?.addOnCameraTrackingChangedListener(cameraTrackingCallback)
    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        // TODO: Consider calling
        //    ActivityCompat#requestPermissions
        // here to request the missing permissions, and then overriding
        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
        //                                          int[] grantResults)
        // to handle the case where the user grants the permission. See the documentation
        // for ActivityCompat#requestPermissions for more details.
        return
    }
    locationComponent?.isLocationComponentEnabled = true
    locationEngine = locationComponent?.locationEngine!!
    val request = LocationEngineRequest.Builder(1000)
        .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
        .build()
    locationEngine?.requestLocationUpdates(
        request,
        locationEngineCallback,
        Looper.getMainLooper()
    )
    locationEngine?.getLastLocation(locationEngineCallback)
    locationComponent?.cameraMode = CameraMode.TRACKING
    locationComponent?.renderMode = RenderMode.COMPASS
}

fun setRenderMode(mode: String) {
    if (mode.equals("normal", ignoreCase = true)) {
        locationComponent?.renderMode = RenderMode.NORMAL
    } else if (mode.equals("compass", ignoreCase = true)) {
        locationComponent?.renderMode = RenderMode.COMPASS
    } else if (mode.equals("GPS", ignoreCase = true)) {
        locationComponent?.renderMode = RenderMode.GPS
    } else
        locationComponent?.renderMode = RenderMode.NORMAL

}

private fun setCameraMode(mode: String) {
    if (mode.equals("None", ignoreCase = true)) {
        locationComponent?.cameraMode = CameraMode.NONE
    } else if (mode.equals("None compass", ignoreCase = true)) {
        locationComponent?.cameraMode = CameraMode.NONE_COMPASS
    } else if (mode.equals("None gps", ignoreCase = true)) {
        locationComponent?.cameraMode = CameraMode.NONE_GPS
    } else if (mode.equals("Tracking", ignoreCase = true)) {
        locationComponent?.cameraMode = CameraMode.TRACKING
    } else if (mode.equals("Tracking Compass", ignoreCase = true)) {
        locationComponent?.cameraMode = CameraMode.TRACKING_COMPASS
    } else if (mode.equals("Tracking GPS", ignoreCase = true)) {
        locationComponent?.cameraMode = CameraMode.TRACKING_GPS
    } else if (mode.equals("Tracking GPS North", ignoreCase = true)) {
        locationComponent?.cameraMode = CameraMode.TRACKING_GPS_NORTH
    } else locationComponent?.cameraMode = CameraMode.TRACKING
}

