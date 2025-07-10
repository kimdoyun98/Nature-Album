package com.and04.naturealbum.ui.album.photoinfo.contract

import androidx.annotation.StringRes

sealed interface PhotoInfoEffect {
    data object InitPhotoInfo: PhotoInfoEffect

    data object Back: PhotoInfoEffect

    data object MyPage: PhotoInfoEffect

    data class Toast(@StringRes val message: Int): PhotoInfoEffect
}
