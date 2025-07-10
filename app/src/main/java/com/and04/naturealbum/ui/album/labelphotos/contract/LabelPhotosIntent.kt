package com.and04.naturealbum.ui.album.labelphotos.contract

import com.and04.naturealbum.data.localdata.room.PhotoDetail
import com.and04.naturealbum.ui.component.PermissionDialogState

sealed interface LabelPhotosIntent {
    data class EditModeSwitched(val isEditModeEnabled: Boolean) : LabelPhotosIntent

    data class ImageDownLoading(val isImgDownLoading: Boolean) : LabelPhotosIntent

    data class PermissionDialogStateSet(val pdState: PermissionDialogState?) : LabelPhotosIntent

    data object PhotosDelete : LabelPhotosIntent

    data class SelectAll(val isAllSelected: Boolean) : LabelPhotosIntent

    data object EditModeBack : LabelPhotosIntent

    data class PhotoLongPress(val photo: PhotoDetail) : LabelPhotosIntent

    data class PhotoTap(
        val isSelected: Boolean,
        val photo: PhotoDetail
    ): LabelPhotosIntent

    data class PhotoClicked(val id: Int): LabelPhotosIntent

    data object MyPageClicked: LabelPhotosIntent

    data object BackButtonClicked: LabelPhotosIntent
}
