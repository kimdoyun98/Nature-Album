package com.and04.naturealbum.ui.album.labels.contract

import com.and04.naturealbum.data.dto.AlbumDto
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class LabelsState (
    val labels: ImmutableList<AlbumDto> = persistentListOf()
)
