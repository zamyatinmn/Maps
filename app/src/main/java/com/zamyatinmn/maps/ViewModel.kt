package com.zamyatinmn.maps

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng

class MapViewModel : ViewModel() {
    var permissionGranted by mutableStateOf(false)
        private set
    var markers = mutableStateListOf<Marker>()
        private set
    var activeScreen by mutableStateOf<Screen>(Screen.Map)
        private set

    fun changePermissionState(state: Boolean) {
        permissionGranted = state
    }

    fun changeScreen(screen: Screen) {
        activeScreen = screen
    }

    fun changeMarkerState(index: Int, title: String? = null, snippet: String? = null) {
        markers[index].let { marker ->
            marker.copy(
                title = title ?: marker.title,
                snippet = snippet ?: marker.snippet
            ).also { markers[index] = it }
        }
    }
}

sealed class Screen {
    object Map : Screen()
    object Markers : Screen()
}

data class Marker(
    val coordinates: LatLng,
    val title: String = "",
    val snippet: String = ""
)