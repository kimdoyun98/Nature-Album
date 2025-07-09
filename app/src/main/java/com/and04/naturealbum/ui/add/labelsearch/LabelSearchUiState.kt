package com.and04.naturealbum.ui.add.labelsearch

sealed interface LabelSearchUiState {

    data object Loading : LabelSearchUiState

    data object Success : LabelSearchUiState
}
