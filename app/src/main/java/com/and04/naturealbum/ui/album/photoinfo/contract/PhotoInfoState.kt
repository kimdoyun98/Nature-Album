package com.and04.naturealbum.ui.album.photoinfo.contract

import androidx.compose.runtime.Immutable
import com.and04.naturealbum.data.localdata.room.Label
import com.and04.naturealbum.data.localdata.room.PhotoDetail
import com.and04.naturealbum.ui.album.photoinfo.utils.PhotoInfoUiState

@Immutable
data class PhotoInfoState(
    val state: PhotoInfoUiState = PhotoInfoUiState.Idle,
    val label: Label = Label.emptyLabel(),
    val photo: PhotoDetail = PhotoDetail.emptyPhotoDetail(),
    val address: String = "",
)
