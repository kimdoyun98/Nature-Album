package com.and04.naturealbum.ui.mypage.friendsearch.contract

import androidx.annotation.StringRes

sealed interface FriendSearchEffect {
    data class Toast(@StringRes val message: Int) : FriendSearchEffect

    data object Back : FriendSearchEffect
}
