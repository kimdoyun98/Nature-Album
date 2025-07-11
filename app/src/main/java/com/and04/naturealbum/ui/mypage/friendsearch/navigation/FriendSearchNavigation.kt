package com.and04.naturealbum.ui.mypage.friendsearch.navigation

import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.and04.naturealbum.NatureAlbum
import com.and04.naturealbum.ui.mypage.friendsearch.FriendSearchScreen
import com.and04.naturealbum.ui.mypage.friendsearch.FriendSearchViewModel
import com.and04.naturealbum.ui.mypage.friendsearch.contract.FriendSearchEffect
import com.and04.naturealbum.ui.navigation.NatureAlbumNavigator
import com.and04.naturealbum.ui.navigation.NavigateDestination
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

fun NavGraphBuilder.friendSearchNavigation(
    navigator: NatureAlbumNavigator
) {
    composable(NavigateDestination.FriendSearch.route) { backStackEntry ->
        val backStackEntryForMyPage = remember(backStackEntry) {
            navigator.getNavBackStackEntry(NavigateDestination.MyPage.route)
        }

        val viewModel: FriendSearchViewModel = hiltViewModel(backStackEntryForMyPage)
        val state by viewModel.collectAsState()

        viewModel.collectSideEffect { effect ->
            when (effect) {
                is FriendSearchEffect.Back -> {
                    navigator.popupBackStack()
                }

                is FriendSearchEffect.Toast -> {
                    Toast.makeText(NatureAlbum.getInstance(), effect.message, Toast.LENGTH_LONG)
                        .show()
                }
            }
        }

        FriendSearchScreen(
            state = { state },
            onIntent = viewModel::onIntent,
        )
    }
}
