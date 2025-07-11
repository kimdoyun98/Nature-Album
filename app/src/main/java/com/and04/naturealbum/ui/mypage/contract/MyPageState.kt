package com.and04.naturealbum.ui.mypage.contract

import androidx.compose.runtime.Immutable
import com.and04.naturealbum.data.dto.FirebaseFriend
import com.and04.naturealbum.data.dto.FirebaseFriendRequest
import com.and04.naturealbum.data.localdata.datastore.DataStoreManager.Companion.NEVER_SYNC
import com.and04.naturealbum.ui.mypage.utils.LoginState
import com.and04.naturealbum.ui.utils.UserManager
import com.and04.naturealbum.utils.network.NetworkState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class MyPageState(
    val loginState: LoginState = LoginState.initState(UserManager),
    val networkState: Int = NetworkState.CONNECTED_DATA,
    val friends: ImmutableList<FirebaseFriend> = persistentListOf(),
    val receivedFriendRequests: ImmutableList<FirebaseFriendRequest> = persistentListOf(),
    val recentSyncTime: String = NEVER_SYNC,
    val isSyncWorking: Boolean = false
)
