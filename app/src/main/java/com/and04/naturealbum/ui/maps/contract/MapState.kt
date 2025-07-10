package com.and04.naturealbum.ui.maps.contract

import android.graphics.PointF
import androidx.compose.runtime.Immutable
import com.and04.naturealbum.data.dto.FirebaseFriend
import com.and04.naturealbum.ui.maps.PhotoItem
import com.and04.naturealbum.utils.network.NetworkState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf

@Immutable
data class MapState(
    val networkState: Int = NetworkState.CONNECTED_WIFI,
    val friends: ImmutableList<FirebaseFriend> = persistentListOf(),
    val photosByUid: ImmutableMap<String, ImmutableList<PhotoItem>> = persistentMapOf(),
    val showPhotoContent: Boolean = false,
    val openDialog: Boolean = false,
    val pick: PhotoItem? = null,
    val bottomSheetPhotos: ImmutableList<PhotoItem> = persistentListOf(),
    val selectedFriends: ImmutableList<FirebaseFriend> = persistentListOf(),
    val cameraPivot: PointF = PointF(0.5f, 0.5f),
)
