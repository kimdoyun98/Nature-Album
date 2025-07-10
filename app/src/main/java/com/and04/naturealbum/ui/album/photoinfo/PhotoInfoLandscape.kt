package com.and04.naturealbum.ui.album.photoinfo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.and04.naturealbum.R
import com.and04.naturealbum.ui.album.photoinfo.contract.PhotoInfoIntent
import com.and04.naturealbum.ui.album.photoinfo.contract.PhotoInfoState
import com.and04.naturealbum.ui.component.AlbumLabel
import com.and04.naturealbum.utils.color.toColor
import com.and04.naturealbum.utils.time.toDate

@Composable
fun PhotoInfoLandscape(
    modifier: Modifier = Modifier,
    state: PhotoInfoState,
    onIntent: (PhotoInfoIntent) -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(36.dp)
        ) {
            AsyncImage(
                model = state.photo.photoUri,
                contentDescription = state.photo.description,
                modifier = Modifier.clip(RoundedCornerShape(10.dp))
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AlbumLabel(
                modifier = Modifier
                    .background(
                        color = state.label.backgroundColor.toColor(),
                        shape = CircleShape
                    )
                    .fillMaxWidth(0.6f),
                text = state.label.name,
                backgroundColor = state.label.backgroundColor.toColor()
            )

            RowInfo(
                imgVector = Icons.Default.DateRange,
                contentDescription = stringResource(R.string.photo_info_screen_calender_icon),
                text = state.photo.datetime.toDate()
            )

            RowInfo(
                imgVector = Icons.Default.LocationOn,
                contentDescription = stringResource(R.string.photo_info_screen_location_icon),
                text = state.address
            )

            if (state.photo.description.isNotEmpty()) {
                RowInfo(
                    imgVector = Icons.Default.Edit,
                    contentDescription = stringResource(R.string.photo_info_screen_description_icon),
                    text = state.photo.description
                )
            }

            SetThumbnailContent(
                onClick = {
                    onIntent(
                        PhotoInfoIntent.SetThumbnailButtonClicked(
                            state.photo.id,
                            R.string.photo_info_set_thumbnail_btn_txt
                        )
                    )
                },
            )
        }
    }
}
