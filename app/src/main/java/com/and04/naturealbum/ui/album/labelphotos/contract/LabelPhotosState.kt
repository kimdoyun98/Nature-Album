package com.and04.naturealbum.ui.album.labelphotos.contract

import androidx.compose.runtime.Immutable
import com.and04.naturealbum.data.localdata.room.Label
import com.and04.naturealbum.data.localdata.room.PhotoDetail
import com.and04.naturealbum.ui.album.labelphotos.utils.LabelPhotosUiState
import com.and04.naturealbum.ui.component.PermissionDialogState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf

@Immutable
data class LabelPhotosState(
    val state: LabelPhotosUiState = LabelPhotosUiState.Idle,
    val label: Label = Label.emptyLabel(),
    val photos: ImmutableList<PhotoDetail> = persistentListOf(),
    val imgDownLoading: Boolean = false,
    val editMode: Boolean = false,
    val checkList: ImmutableSet<PhotoDetail> = persistentSetOf(),
    val selectAll: Boolean = false,
    val permissionDialogState: PermissionDialogState? = null,
)
