package com.and04.naturealbum.ui.album.labelphotos.navigation

import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.and04.naturealbum.ui.album.labelphotos.LabelPhotosScreen
import com.and04.naturealbum.ui.album.labelphotos.LabelPhotosViewModel
import com.and04.naturealbum.ui.album.labelphotos.contract.LabelPhotosEffect
import com.and04.naturealbum.ui.navigation.NatureAlbumNavigator
import com.and04.naturealbum.ui.navigation.NavigateDestination
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

fun NavGraphBuilder.labelPhotosNavigation(
    navigator: NatureAlbumNavigator,
) {
    composable("${NavigateDestination.LabelPhotos.route}/{labelId}") { backStackEntry ->
        val viewModel: LabelPhotosViewModel = hiltViewModel()
        val state by viewModel.collectAsState()
        val labelId = backStackEntry.arguments?.getString("labelId")?.toInt()!!

        viewModel.collectSideEffect { effect ->
            when (effect) {
                is LabelPhotosEffect.Error -> {
                    navigator.popupBackStack()
                }

                is LabelPhotosEffect.PhotoInfo -> {
                    navigator.navigateToAlbumInfo(effect.id)
                }

                is LabelPhotosEffect.Back -> {
                    navigator.popupBackStack()
                }

                is LabelPhotosEffect.MyPage -> {
                    navigator.navigateToMyPage()
                }
            }
        }

        LabelPhotosScreen(
            state = { state },
            labelId = labelId,
            onIntent = viewModel::onIntent,
            loadFolderData = viewModel::loadFolderData,
        )
    }
}
