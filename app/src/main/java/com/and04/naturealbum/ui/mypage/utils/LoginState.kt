package com.and04.naturealbum.ui.mypage.utils

import com.and04.naturealbum.data.model.UserInfo
import com.and04.naturealbum.ui.utils.UserManager

sealed interface LoginState {

    data class Login(
        val userInfo: UserInfo
    ) : LoginState

    data object Logout : LoginState

    data object LoginLoading: LoginState

    companion object {
        fun initState(manager: UserManager): LoginState{
            return if (manager.isSignIn()) {
                getUserInfoUiState()
            } else {
                Logout
            }
        }

        private fun getUserInfoUiState(): LoginState {
            val user = UserManager.getUser()
            return Login(
                UserInfo(
                    userEmail = user?.email,
                    userPhotoUri = user?.photoUrl.toString(),
                    userDisplayName = user?.displayName,
                    userUid = user?.uid
                )
            )
        }
    }
}
