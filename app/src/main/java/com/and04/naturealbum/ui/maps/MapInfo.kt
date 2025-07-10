package com.and04.naturealbum.ui.maps

import androidx.compose.runtime.Stable
import com.naver.maps.map.MapView
import com.naver.maps.map.overlay.Marker

@Stable
class MapInfo(val mapView: MapView) {
    lateinit var marker: Marker
    lateinit var imageMarker: ImageMarker

    fun setMarker(marker: Marker, imageMarker: ImageMarker) {
        this.marker = marker
        this.imageMarker = imageMarker
    }
}
