package com.and04.naturealbum.ui.mypage.mypage.contract

import androidx.annotation.StringRes

sealed interface MyPageEffect {
    data object Back : MyPageEffect

    data object FriendSearch : MyPageEffect

    data class Toast(@StringRes val message: Int) : MyPageEffect
}
