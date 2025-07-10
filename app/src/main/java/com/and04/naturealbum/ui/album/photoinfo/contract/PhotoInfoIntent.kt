package com.and04.naturealbum.ui.album.photoinfo.contract

sealed interface PhotoInfoIntent {
    data object MyPageButtonClicked: PhotoInfoIntent

    data object BackButtonClicked: PhotoInfoIntent

    data class SetThumbnailButtonClicked(val id: Int, val message: Int): PhotoInfoIntent
}
