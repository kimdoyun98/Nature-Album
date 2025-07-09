package com.and04.naturealbum.ui.maps.navigation

import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.and04.naturealbum.ui.maps.MapScreen
import com.and04.naturealbum.ui.maps.MapScreenViewModel
import com.and04.naturealbum.ui.navigation.NatureAlbumNavigator
import com.and04.naturealbum.ui.navigation.NavigateDestination
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

fun NavGraphBuilder.mapNavigation(
    navigator: NatureAlbumNavigator
) {
    composable(NavigateDestination.Map.route) {
        val viewModel: MapScreenViewModel = hiltViewModel()
        val state by viewModel.collectAsState()

        MapScreen(
            state = { state },
            sideEffect = { func -> viewModel.collectSideEffect(sideEffect = func) },
            onIntent = viewModel::onIntent,
            navigateToHome = { navigator.popupBackStack() }
        )
    }
}
