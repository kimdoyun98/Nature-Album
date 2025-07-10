package com.and04.naturealbum.ui.album.labelphotos.contract

sealed interface LabelPhotosEffect {
    data object Error : LabelPhotosEffect

    data object MyPage : LabelPhotosEffect

    data object Back : LabelPhotosEffect

    data class PhotoInfo(val id: Int) : LabelPhotosEffect
}
