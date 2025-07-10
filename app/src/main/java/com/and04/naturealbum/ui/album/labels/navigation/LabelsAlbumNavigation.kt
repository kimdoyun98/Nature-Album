package com.and04.naturealbum.ui.album.labels.navigation

import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.and04.naturealbum.ui.album.labels.LabelsScreen
import com.and04.naturealbum.ui.album.labels.LabelsViewModel
import com.and04.naturealbum.ui.album.labels.contract.LabelsEffect
import com.and04.naturealbum.ui.navigation.NatureAlbumNavigator
import com.and04.naturealbum.ui.navigation.NavigateDestination
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

fun NavGraphBuilder.labelsAlbumNavigation(
    navigator: NatureAlbumNavigator
) {
    composable(NavigateDestination.Album.route) {
        val viewModel: LabelsViewModel = hiltViewModel()
        val state by viewModel.collectAsState()

        viewModel.collectSideEffect { effect ->
            when (effect) {
                is LabelsEffect.LabelPhotos -> {
                    navigator.navigateToAlbumFolder(effect.id)
                }

                is LabelsEffect.MyPage -> {
                    navigator.navigateToMyPage()
                }

                is LabelsEffect.Back -> {
                    navigator.popupBackStack()
                }
            }
        }

        LabelsScreen(
            state = state,
            onIntent = viewModel::onIntent,
        )
    }
}
