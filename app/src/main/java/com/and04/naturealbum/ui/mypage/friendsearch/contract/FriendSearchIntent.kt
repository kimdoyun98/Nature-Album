package com.and04.naturealbum.ui.mypage.friendsearch.contract

sealed interface FriendSearchIntent {
    data class QueryChanged(val query: String): FriendSearchIntent

    data class ExpandedChanged(val expanded: Boolean): FriendSearchIntent

    data class FriendRequestSend(val id: String): FriendSearchIntent

    data object BackButtonClicked: FriendSearchIntent
}
