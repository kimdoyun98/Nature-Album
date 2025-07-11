package com.and04.naturealbum.ui.mypage.mypage.contract

sealed interface MyPageEffect {
    data object Back : MyPageEffect

    data object FriendSearch : MyPageEffect
}
