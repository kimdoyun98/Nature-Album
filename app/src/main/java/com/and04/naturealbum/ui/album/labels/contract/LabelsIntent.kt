package com.and04.naturealbum.ui.album.labels.contract

sealed interface LabelsIntent {
    data object BackButtonClicked: LabelsIntent

    data object MyPageClicked: LabelsIntent

    data class LabelClicked(val id: Int): LabelsIntent
}
