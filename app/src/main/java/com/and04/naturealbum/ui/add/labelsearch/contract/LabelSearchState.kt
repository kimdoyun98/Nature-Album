package com.and04.naturealbum.ui.add.labelsearch.contract

import androidx.compose.runtime.Immutable
import com.and04.naturealbum.data.localdata.room.Label
import com.and04.naturealbum.ui.add.labelsearch.LabelSearchUiState
import com.and04.naturealbum.ui.add.labelsearch.getRandomColor

@Immutable
data class LabelSearchState(
    val uiState: LabelSearchUiState = LabelSearchUiState.Loading,
    val query: String = "",
    val color: String = getRandomColor(),
    val label: Label = Label.emptyLabel(),
    val labelList: List<Label> = emptyList(),
)
