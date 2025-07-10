package com.and04.naturealbum.ui.album.photoinfo

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.and04.naturealbum.R
import com.and04.naturealbum.ui.album.photoinfo.contract.PhotoInfoIntent
import com.and04.naturealbum.ui.album.photoinfo.contract.PhotoInfoState
import com.and04.naturealbum.ui.album.photoinfo.utils.PhotoInfoUiState
import com.and04.naturealbum.ui.component.AppBarType
import com.and04.naturealbum.ui.component.ProgressIndicator
import com.and04.naturealbum.utils.GetTopBar

@Composable
fun PhotoInfoScreen(
    state: PhotoInfoState,
    onIntent: (PhotoInfoIntent) -> Unit,
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            context.GetTopBar(
                title = stringResource(R.string.topbar_title_photo_info),
                type = AppBarType.All,
                navigateToBackScreen = { onIntent(PhotoInfoIntent.BackButtonClicked) },
                navigateToMyPage = { onIntent(PhotoInfoIntent.MyPageButtonClicked) },
            )
        }
    ) { innerPadding ->
        when (state.state) {
            is PhotoInfoUiState.Idle, PhotoInfoUiState.Loading -> {
                Box(
                    modifier = Modifier.padding(innerPadding)
                ) {
                    ProgressIndicator(true)
                }
            }

            is PhotoInfoUiState.Success -> {
                PhotoDetailInfo(
                    innerPadding = innerPadding,
                    state = state,
                    onIntent = onIntent,
                )
            }
        }
    }
}

@Composable
private fun PhotoDetailInfo(
    innerPadding: PaddingValues,
    state: PhotoInfoState,
    onIntent: (PhotoInfoIntent) -> Unit,
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    if (isLandscape) {
        PhotoInfoLandscape(
            modifier = Modifier.padding(innerPadding),
            state = state,
            onIntent = onIntent,
        )
    } else {
        PhotoInfoPortrait(
            modifier = Modifier.padding(innerPadding),
            state = state,
            onIntent = onIntent,
        )
    }
}

@Composable
fun RowInfo(
    imgVector: ImageVector,
    contentDescription: String,
    text: String,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = imgVector,
            contentDescription = contentDescription,
        )
        Text(text)
    }
}

@Composable
fun SetThumbnailContent(
    onClick: () -> Unit,
) {
    Button(onClick = { onClick() }) {
        Text(text = stringResource(R.string.photo_info_set_thumbnail_btn_txt))
    }
}


@Preview
@Composable
private fun PhotoInfoPreview() {
    PhotoInfoScreen(
        state = PhotoInfoState(),
        onIntent = {}
    )
}

@Preview
@Composable
private fun PhotoInfoPortraitPreview() {
    PhotoInfoPortrait(
        state = PhotoInfoState(),
        onIntent = {}
    )
}

@Preview
@Composable
private fun PhotoInfoLandScapePreview() {
    PhotoInfoLandscape(
        state = PhotoInfoState(),
        onIntent = {}
    )
}
