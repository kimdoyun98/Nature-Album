package com.and04.naturealbum.ui.maps.contract

sealed interface MapEffect {
    data object CameraPivotChanged: MapEffect

    data object PickChanged: MapEffect

    data object PhotosByUidChanged: MapEffect

    data object NavigateHome: MapEffect
}
