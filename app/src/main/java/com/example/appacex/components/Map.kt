package com.example.appacex.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapScreen(modifier: Modifier) {
    val torrelavega = LatLng(43.353, -4.064)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(torrelavega, 10f)
    }

    GoogleMap(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(0.4f)
            .padding(start = 16.dp, end = 16.dp)
            .shadow(8.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White),
        cameraPositionState = cameraPositionState
    ) {
        Marker(
            state = com.google.maps.android.compose.rememberMarkerState(position = torrelavega),
            title = "Torrelavega",
            snippet = "Cantabria"
        )
    }
}