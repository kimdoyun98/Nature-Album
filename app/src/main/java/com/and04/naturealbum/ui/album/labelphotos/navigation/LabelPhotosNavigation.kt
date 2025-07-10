package com.and04.naturealbum.ui.album.labelphotos.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.and04.naturealbum.ui.album.labelphotos.LabelPhotosScreen
import com.and04.naturealbum.ui.navigation.NatureAlbumNavigator
import com.and04.naturealbum.ui.navigation.NavigateDestination

fun NavGraphBuilder.labelPhotosNavigation(
    navigator: NatureAlbumNavigator,
) {
    composable("${NavigateDestination.LabelPhotos.route}/{labelId}") { backStackEntry ->
        val labelId = backStackEntry.arguments?.getString("labelId")?.toInt()!!

        LabelPhotosScreen(
            selectedAlbumLabel = labelId,
            onPhotoClick = { photoDetailId -> navigator.navigateToAlbumInfo(photoDetailId) },
            onNavigateToMyPage = { navigator.navigateToMyPage() },
            navigateToBackScreen = { navigator.popupBackStack() },
            onNavigateToAlbum = {
                navigator.popupBackStack()
            }
        )
    }
}
