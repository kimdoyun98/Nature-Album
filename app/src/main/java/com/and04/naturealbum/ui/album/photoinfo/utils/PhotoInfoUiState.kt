package com.and04.naturealbum.ui.album.photoinfo.utils

sealed interface PhotoInfoUiState {
    data object Idle: PhotoInfoUiState

    data object Loading: PhotoInfoUiState

    data object Success: PhotoInfoUiState
}
