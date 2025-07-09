package com.and04.naturealbum.ui.add.labelsearch

import android.widget.Toast
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.and04.naturealbum.R
import com.and04.naturealbum.data.localdata.room.Label
import com.and04.naturealbum.ui.add.labelsearch.contract.LabelSearchIntent
import com.and04.naturealbum.ui.add.labelsearch.contract.LabelSearchState
import com.and04.naturealbum.ui.utils.UiState

@Composable
fun GeminiLabelContent(
    vertexAIState: State<UiState<String>>,
    state: () -> LabelSearchState,
    onIntent: (LabelSearchIntent) -> Unit,
) {
    Row(
        modifier = Modifier.padding(start = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(stringResource(R.string.label_by_gemini_search_create))

        Spacer(Modifier.size(4.dp))

        GeminiLabelChip(
            vertexAIState = vertexAIState,
            state = state,
            onIntent = onIntent,
        )
    }
}

@Composable
private fun GeminiLabelChip(
    vertexAIState: State<UiState<String>>,
    state: () -> LabelSearchState,
    onIntent: (LabelSearchIntent) -> Unit,
) {
    val context = LocalContext.current
    val nestToastText = stringResource(R.string.label_search_nest_label_toast)

    when (val success = vertexAIState.value) {
        is UiState.Success -> {
            val labelByGemini = success.data.trim()
            SuggestionChip(
                onClick = {
                    if (state().labelList.any { label -> label.name == labelByGemini }) {
                        Toast.makeText(context, nestToastText, Toast.LENGTH_LONG).show()
                        return@SuggestionChip
                    }

                    onIntent(
                        LabelSearchIntent.LabelClicked(
                            Label(
                                backgroundColor = state().color,
                                name = labelByGemini
                            )
                        )
                    )
                },
                label = {
                    Text(
                        text = labelByGemini,
                    )
                },
                colors = SuggestionChipDefaults.suggestionChipColors(
                    containerColor = Color(
                        state().color.toLong(16)
                    ),
                    labelColor = if (Color(state().color.toLong(16)).luminance() > 0.5f) Color.Black else Color.White
                )
            )
        }

        is UiState.Idle, UiState.Loading -> {
            CircularProgressIndicator(
                modifier = Modifier.width(32.dp),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        }

        is UiState.Error<*> -> {
            /* TODO ERROR */
        }
    }
}
