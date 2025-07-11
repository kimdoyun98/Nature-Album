package com.and04.naturealbum.ui.mypage.contract

import android.content.Context

sealed interface MyPageIntent {
    data object BackButtonClicked: MyPageIntent

    data object FriendSearchClicked: MyPageIntent

    data class LoginClicked(val context: Context): MyPageIntent

    data object SyncButtonClicked: MyPageIntent
}
