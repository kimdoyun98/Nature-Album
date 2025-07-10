package com.and04.naturealbum.ui.album.labels.contract

interface LabelsEffect {
    data object Back: LabelsEffect

    data object MyPage: LabelsEffect

    data class LabelPhotos(val id: Int): LabelsEffect
}
