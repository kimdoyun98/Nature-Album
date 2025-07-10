package com.and04.naturealbum.ui.album.labelphotos.utils

sealed interface LabelPhotosUiState {
    data object Idle : LabelPhotosUiState

    data object Loading : LabelPhotosUiState

    data object Success : LabelPhotosUiState
}
