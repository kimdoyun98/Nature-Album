package com.and04.naturealbum.ui.album.labels

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil3.compose.AsyncImage
import com.and04.naturealbum.R
import com.and04.naturealbum.data.dto.AlbumDto
import com.and04.naturealbum.ui.album.labels.contract.LabelsIntent
import com.and04.naturealbum.ui.album.labels.contract.LabelsState
import com.and04.naturealbum.ui.component.AlbumLabel
import com.and04.naturealbum.ui.component.AppBarType
import com.and04.naturealbum.ui.theme.NatureAlbumTheme
import com.and04.naturealbum.utils.GetTopBar
import com.and04.naturealbum.utils.color.toColor
import com.and04.naturealbum.utils.gridColumnCount
import kotlinx.collections.immutable.ImmutableList

@Composable
fun LabelsScreen(
    state: LabelsState,
    onIntent: (LabelsIntent) -> Unit,
) {
    val context = LocalContext.current
    Scaffold(
        topBar = {
            context.GetTopBar(
                type = AppBarType.All,
                navigateToBackScreen = { onIntent(LabelsIntent.BackButtonClicked) },
                navigateToMyPage = { onIntent(LabelsIntent.MyPageClicked) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            AlbumGrid(
                albums = state.labels,
                onIntent = onIntent,
                columnCount = context.gridColumnCount(),
            )
        }
    }
}

@Composable
fun AlbumGrid(
    albums: ImmutableList<AlbumDto>,
    onIntent: (LabelsIntent) -> Unit,
    columnCount: Int,
) {
    if (albums.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = stringResource(R.string.nothing_album_txt))
        }
    } else {
        AlbumGridList(
            albums = albums,
            onIntent = onIntent,
            columnCount = columnCount,
        )
    }

}

@Composable
fun AlbumGridList(
    albums: ImmutableList<AlbumDto>,
    onIntent: (LabelsIntent) -> Unit,
    columnCount: Int,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(columnCount),
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(24.dp),
        horizontalArrangement = Arrangement.spacedBy(28.dp),
    ) {
        items(
            items = albums,
            key = { albumDto -> albumDto.labelId }
        ) { album ->
            AlbumItem(
                album = album,
                onLabelClick = { onIntent(LabelsIntent.LabelClicked(album.labelId)) },
            )
        }
    }
}

@Composable
fun AlbumItem(
    album: AlbumDto,
    onLabelClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AlbumLabel(
            modifier = Modifier
                .background(
                    color = album.labelBackgroundColor.toColor(),
                    shape = CircleShape
                )
                .fillMaxWidth(0.8f)
                .clickable { onLabelClick() },
            text = album.labelName,
            backgroundColor = album.labelBackgroundColor.toColor()
        )

        Spacer(modifier = Modifier.height(10.dp))

        AsyncImage(
            model = album.photoDetailUri.toUri(),
            contentDescription = album.labelName,
            modifier = Modifier
                .wrapContentSize(Alignment.Center)
                .aspectRatio(1f)
                .clip(MaterialTheme.shapes.medium)
                .clickable { onLabelClick() },
            contentScale = ContentScale.Crop,
        )
    }
}


@Preview(
    name = "AlbumScreen Light Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Preview(
    name = "AlbumScreen Dark Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun AlbumScreenPreview() {
    NatureAlbumTheme {
        LabelsScreen(
            state = LabelsState(),
            onIntent = {},
        )
    }
}
