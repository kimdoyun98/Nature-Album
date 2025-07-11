package com.and04.naturealbum.ui.mypage.friendsearch.contract

import androidx.compose.runtime.Immutable
import com.and04.naturealbum.data.dto.FirestoreUserWithStatus
import com.and04.naturealbum.utils.network.NetworkState
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Immutable
data class FriendSearchState(
    val networkState: Int = NetworkState.CONNECTED_DATA,
    val searchQuery: String = "",
    val expanded: Boolean = false,
    val searchResult: ImmutableMap<String, FirestoreUserWithStatus> = persistentMapOf(),
    val friendRequestStatus: StateFlow<Boolean?> = MutableStateFlow(null),
)
