package com.and04.naturealbum.ui.album.photoinfo.navigation

import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.and04.naturealbum.NatureAlbum
import com.and04.naturealbum.ui.album.photoinfo.PhotoInfoScreen
import com.and04.naturealbum.ui.album.photoinfo.PhotoInfoViewModel
import com.and04.naturealbum.ui.album.photoinfo.contract.PhotoInfoEffect
import com.and04.naturealbum.ui.navigation.NatureAlbumNavigator
import com.and04.naturealbum.ui.navigation.NavigateDestination
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

fun NavGraphBuilder.photoInfoNavigation(
    navigator: NatureAlbumNavigator
) {
    composable("${NavigateDestination.PhotoInfo.route}/{photoDetailId}") { backStackEntry ->
        val photoId = backStackEntry.arguments?.getString("photoDetailId")?.toInt()!!
        val viewModel: PhotoInfoViewModel = hiltViewModel()
        val state by viewModel.collectAsState()

        viewModel.collectSideEffect { effect ->
            when (effect) {
                is PhotoInfoEffect.InitPhotoInfo -> {
                    viewModel.loadPhotoDetail(photoId)
                }

                is PhotoInfoEffect.MyPage -> {
                    navigator.navigateToMyPage()
                }

                is PhotoInfoEffect.Back -> {
                    navigator.popupBackStack()
                }

                is PhotoInfoEffect.Toast -> {
                    Toast.makeText(NatureAlbum.getInstance(), effect.message, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        PhotoInfoScreen(
            state = state,
            onIntent = viewModel::onIntent,
        )
    }
}
