package com.and04.naturealbum.ui.maps.contract

import android.graphics.PointF
import androidx.compose.runtime.Immutable
import com.and04.naturealbum.data.dto.FirebaseFriend
import com.and04.naturealbum.ui.maps.PhotoItem
import com.and04.naturealbum.utils.network.NetworkState

@Immutable
data class MapState(
    val networkState: Int = NetworkState.CONNECTED_WIFI,
    val friends: List<FirebaseFriend> = emptyList(),
    val photosByUid: Map<String, List<PhotoItem>> = emptyMap(),
    val showPhotoContent: Boolean = false,
    val openDialog: Boolean = false,
    val pick: PhotoItem? = null,
    val bottomSheetPhotos: List<PhotoItem> = emptyList(),
    val selectedFriends: List<FirebaseFriend> = emptyList(),
    val cameraPivot: PointF = PointF(0.5f, 0.5f),
)
