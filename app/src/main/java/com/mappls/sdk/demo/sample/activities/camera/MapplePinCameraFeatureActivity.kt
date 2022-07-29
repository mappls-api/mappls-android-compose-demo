package com.mappls.sdk.demo.sample.activities.camera

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.mappls.sdk.demo.MapplsMap
import com.mappls.sdk.demo.R
import com.mappls.sdk.maps.MapplsMap
import com.mappls.sdk.maps.camera.CameraMapplsPinUpdateFactory
import com.mappls.sdk.maps.camera.CameraUpdateFactory
import com.mappls.sdk.maps.geometry.LatLng

class MapplePinCameraFeatureActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MapplePinCameraFeatureScreen()
        }
    }
}

@Composable
fun MapplePinCameraFeatureScreen() {
    var mapplsMap1: MapplsMap? = null
    val context = LocalContext.current

    Scaffold(topBar = {
        TopAppBar(title = { Text(text = "Mappls Pin Camera Features") })
    }) {

        Column(

        ) {

            MapplsMap(
                modifier = Modifier.weight(1f),
                onSuccess = { mapView, mapplsMap ->
                    mapplsMap1 = mapplsMap
                    mapplsMap.setPadding(20, 20, 20, 20)

                    mapplsMap.animateCamera(
                        CameraMapplsPinUpdateFactory.newMapplsPinZoom(
                            "MMI000",
                            14.0
                        )
                    )
                },

                onError = { _, _ ->

                })


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colorResource(id = R.color.purple_500))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Text(
                    text = "Move Camera", color = Color.White,
                    modifier = Modifier
                        .clickable(enabled = true) {
                            mapplsMap1?.moveCamera(CameraMapplsPinUpdateFactory.newMapplsPinZoom("2T7S17", 14.0))
                        },
                )
                Text(
                    text = "Ease Camera", color = Color.White,
                    modifier = Modifier
                        .clickable(enabled = true) {
                            mapplsMap1?.easeCamera(CameraMapplsPinUpdateFactory.newMapplsPinZoom("5EU4EZ", 14.0))
                        },
                )
                Text(
                    text = "Animate Camera", color = Color.White,
                    modifier = Modifier
                        .clickable(enabled = true) {
                            mapplsMap1?.animateCamera(CameraMapplsPinUpdateFactory.newMapplsPinZoom("IB3BR9", 14.0))
                        },
                )
            }


        }
    }


}

