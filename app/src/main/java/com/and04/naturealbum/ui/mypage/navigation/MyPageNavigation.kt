package com.and04.naturealbum.ui.mypage.navigation

import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.and04.naturealbum.background.service.FirebaseMessagingService.Companion.MY_PAGE_URI
import com.and04.naturealbum.ui.mypage.MyPageScreen
import com.and04.naturealbum.ui.mypage.MyPageViewModel
import com.and04.naturealbum.ui.mypage.contract.MyPageEffect
import com.and04.naturealbum.ui.navigation.NatureAlbumNavigator
import com.and04.naturealbum.ui.navigation.NavigateDestination
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

fun NavGraphBuilder.myPageNavigation(
    navigator: NatureAlbumNavigator
) {
    composable(
        route = NavigateDestination.MyPage.route,
        deepLinks = listOf(navDeepLink {
            uriPattern = MY_PAGE_URI
        })
    ) {
        val viewModel: MyPageViewModel = hiltViewModel()
        val state by viewModel.collectAsState()

        viewModel.collectSideEffect { effect ->
            when (effect) {
                is MyPageEffect.Back -> {
                    navigator.popupBackStack()
                }

                is MyPageEffect.FriendSearch -> {
                    navigator.navigateToFriendSearch()
                }
            }
        }

        MyPageScreen(
            state = { state },
            onIntent = viewModel::onIntent,
        )
    }
}
