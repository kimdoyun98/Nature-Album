package com.and04.naturealbum.ui.mypage.mypage.contract

import android.content.Context
import androidx.annotation.StringRes
import com.and04.naturealbum.R

sealed interface MyPageIntent {
    data object BackButtonClicked : MyPageIntent

    data object FriendSearchClicked : MyPageIntent

    data class LoginClicked(
        val context: Context,
        @StringRes val massage: Int = R.string.my_page_login_no_network_message,
    ) : MyPageIntent

    data object SyncButtonClicked : MyPageIntent

    data class FriendRequestAccept(val friendUid: String) : MyPageIntent

    data class FriendRequestReject(val friendUid: String) : MyPageIntent
}
