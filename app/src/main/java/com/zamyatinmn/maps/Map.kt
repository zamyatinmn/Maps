package com.zamyatinmn.maps

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.zamyatinmn.maps.ui.theme.MapsTheme

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Map(viewModel: MapViewModel) {
    Box(modifier = Modifier.fillMaxSize()) {
        var isMapLoaded by remember { mutableStateOf(false) }
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(
                viewModel.markers.firstOrNull()?.coordinates ?: LatLng(
                    1.35,
                    103.87
                ), 10f
            )
        }
        val mapProperties by remember { mutableStateOf(MapProperties(isMyLocationEnabled = true)) }
        val uiSettings by remember { mutableStateOf(MapUiSettings(myLocationButtonEnabled = true)) }
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = mapProperties,
            uiSettings = uiSettings,
            onMapLoaded = {
                isMapLoaded = true
            },
            onMapLongClick = {
                viewModel.markers.add(Marker(coordinates = it))
            }
        ) {
            viewModel.markers.forEach {
                Marker(
                    position = it.coordinates,
                    title = it.title,
                    snippet = it.snippet
                )
            }
        }
        if (!isMapLoaded) {
            AnimatedVisibility(
                modifier = Modifier
                    .matchParentSize(),
                visible = !isMapLoaded,
                enter = EnterTransition.None,
                exit = fadeOut()
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .background(MaterialTheme.colors.background)
                        .wrapContentSize()
                )
            }
        }
        Button(
            modifier = Modifier.padding(start = 14.dp, top = 14.dp),
            onClick = { viewModel.changeScreen(Screen.Markers) }) {
            Text(text = stringResource(R.string.to_markers_btn))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MapsTheme {
        Map(MapViewModel())
    }
}