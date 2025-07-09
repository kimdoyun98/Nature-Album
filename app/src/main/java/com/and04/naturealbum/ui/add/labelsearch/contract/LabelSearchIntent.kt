package com.and04.naturealbum.ui.add.labelsearch.contract

import com.and04.naturealbum.data.localdata.room.Label

sealed interface LabelSearchIntent {
    data class QueryInput(val query: String) : LabelSearchIntent

    data class LabelClicked(val label: Label) : LabelSearchIntent
}
