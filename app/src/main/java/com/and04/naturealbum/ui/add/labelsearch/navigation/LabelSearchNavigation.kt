package com.and04.naturealbum.ui.add.labelsearch.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.and04.naturealbum.ui.add.labelsearch.LabelSearchScreen
import com.and04.naturealbum.ui.add.labelsearch.LabelSearchViewModel
import com.and04.naturealbum.ui.navigation.NatureAlbumNavigator
import com.and04.naturealbum.ui.navigation.NatureAlbumState
import com.and04.naturealbum.ui.navigation.NavigateDestination
import org.orbitmvi.orbit.compose.collectAsState

fun NavGraphBuilder.labelSearchNavigation(
    state: NatureAlbumState,
    navigator: NatureAlbumNavigator
) {
    composable(NavigateDestination.SearchLabel.route) { backStackEntry ->
        val savePhotoBackStackEntryForSearchLabel = remember(backStackEntry) {
            navigator.getNavBackStackEntry(NavigateDestination.SavePhoto.route)
        }
        val viewModel: LabelSearchViewModel = hiltViewModel()
        val labelSearchState by viewModel.collectAsState()

        LabelSearchScreen(
            state = { labelSearchState },
            onSelected = { label ->
                state.selectedLabel.value = label
                navigator.popupBackStack()
            },
            viewModel = viewModel,
            savePhotoViewModel = hiltViewModel(savePhotoBackStackEntryForSearchLabel),
        )
    }
}
