package com.and04.naturealbum.ui.album.labelphotos

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Intent
import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import coil3.compose.AsyncImage
import com.and04.naturealbum.R
import com.and04.naturealbum.data.localdata.room.PhotoDetail
import com.and04.naturealbum.ui.album.labelphotos.component.ButtonWithAnimation
import com.and04.naturealbum.ui.album.labelphotos.contract.LabelPhotosIntent
import com.and04.naturealbum.ui.album.labelphotos.contract.LabelPhotosState
import com.and04.naturealbum.ui.album.labelphotos.utils.LabelPhotosUiState
import com.and04.naturealbum.ui.album.labelphotos.utils.saveImagesWithLoading
import com.and04.naturealbum.ui.component.AlbumLabel
import com.and04.naturealbum.ui.component.AppBarType
import com.and04.naturealbum.ui.component.PermissionDialogState
import com.and04.naturealbum.ui.component.PermissionDialogs
import com.and04.naturealbum.ui.component.ProgressIndicator
import com.and04.naturealbum.ui.component.RotatingImageLoading
import com.and04.naturealbum.ui.theme.NatureAlbumTheme
import com.and04.naturealbum.utils.GetTopBar
import com.and04.naturealbum.utils.color.toColor
import com.and04.naturealbum.utils.gridColumnCount

@Composable
fun LabelPhotosScreen(
    state: () -> LabelPhotosState,
    labelId: Int = 0,
    onIntent: (LabelPhotosIntent) -> Unit,
    loadFolderData: (Int) -> Unit,
) {
    val context = LocalContext.current

    val saveImagesWithLoading = {
        saveImagesWithLoading(
            context = context,
            photoDetails = state().checkList.toList(),
            setLoading = { isImgDownLoading: Boolean ->
                onIntent(LabelPhotosIntent.ImageDownLoading(isImgDownLoading))
            },
            switchEditMode = { isEditModeEnabled: Boolean ->
                onIntent(LabelPhotosIntent.EditModeSwitched(isEditModeEnabled))
            },
        )
    }

    val requestPermissionLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission())
        { permissionAllow ->
            if (permissionAllow) {
                saveImagesWithLoading()
            } else {
                val activity = context as? Activity ?: return@rememberLauncherForActivityResult
                val hasPreviouslyDeniedPermission =
                    ActivityCompat.shouldShowRequestPermissionRationale(
                        activity,
                        WRITE_EXTERNAL_STORAGE
                    )
                if (!hasPreviouslyDeniedPermission)
                    onIntent(
                        LabelPhotosIntent.PermissionDialogStateSet(
                            PermissionDialogState(
                                onDismiss = {
                                    onIntent(
                                        LabelPhotosIntent.PermissionDialogStateSet(
                                            null
                                        )
                                    )
                                },
                                onConfirmation = {
                                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                        data = Uri.fromParts("package", context.packageName, null)
                                        context.startActivity(this)
                                    }
                                },
                                dialogText = R.string.album_folder_permission_go_to_settings
                            )
                        )
                    )
            }
        }

    val requestPermission = { requestPermissionLauncher.launch(WRITE_EXTERNAL_STORAGE) }

    val savePhotos = {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.Q) requestPermission()
        else saveImagesWithLoading()
    }

    LabelPhotosScreen(
        state = state,
        onIntent = onIntent,
        loadFolderData = { loadFolderData(labelId) },
        savePhotos = savePhotos,
    )
}

@Composable
fun LabelPhotosScreen(
    state: () -> LabelPhotosState,
    onIntent: (LabelPhotosIntent) -> Unit,
    loadFolderData: () -> Unit,
    savePhotos: () -> Unit,
) {
    val context = LocalContext.current
    Scaffold(
        topBar = {
            context.GetTopBar(
                type = AppBarType.All,
                navigateToBackScreen = { onIntent(LabelPhotosIntent.BackButtonClicked) },
                navigateToMyPage = { onIntent(LabelPhotosIntent.MyPageClicked) },
            )
        }
    ) { innerPadding ->
        when (state().state) {
            is LabelPhotosUiState.Idle -> {
                loadFolderData()
            }

            is LabelPhotosUiState.Loading -> {
                Box(
                    modifier = Modifier.padding(innerPadding)
                ) {
                    ProgressIndicator(true)
                }
            }

            is LabelPhotosUiState.Success -> {
                ItemContainer(
                    innerPaddingValues = innerPadding,
                    state = state,
                    onIntent = onIntent,
                    savePhotos = savePhotos,
                )

                if (state().imgDownLoading) {
                    RotatingImageLoading(
                        drawableRes = R.drawable.fish_loading_image,
                        stringRes = R.string.album_folder_screen_save_text
                    )
                }

                PermissionDialogs(state().permissionDialogState)
            }
        }
    }
}

@Composable
private fun ItemContainer(
    innerPaddingValues: PaddingValues,
    state: () -> LabelPhotosState,
    onIntent: (LabelPhotosIntent) -> Unit,
    savePhotos: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPaddingValues)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AlbumLabel(
                modifier = Modifier
                    .background(
                        color = state().label.backgroundColor.toColor(),
                        shape = CircleShape
                    )
                    .fillMaxWidth(0.9f),
                text = state().label.name,
                backgroundColor = state().label.backgroundColor.toColor(),
            )

            Box(
                modifier = Modifier.fillMaxSize(),
            ) {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(LocalContext.current.gridColumnCount()),
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(24.dp),
                    horizontalArrangement = Arrangement.spacedBy(28.dp),
                    verticalItemSpacing = 16.dp
                ) {
                    items(
                        items = state().photos,
                        key = { item -> item.id }
                    ) { photoDetail ->
                        PhotoDetailItem(
                            photoDetail = photoDetail,
                            onIntent = onIntent,
                            editMode = state().editMode,
                            selectAll = state().selectAll,
                        )
                    }
                }

                ButtonWithAnimation(
                    selectAll = { isAllSelected: Boolean ->
                        onIntent(LabelPhotosIntent.SelectAll(isAllSelected))
                    },
                    savePhotos = savePhotos,
                    deletePhotos = { onIntent(LabelPhotosIntent.PhotosDelete) },
                    editMode = state().editMode,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomEnd)
                )
            }
        }
    }

    BackHandler(enabled = state().editMode) {
        onIntent(LabelPhotosIntent.EditModeBack)
    }

}

@Composable
private fun PhotoDetailItem(
    photoDetail: PhotoDetail,
    onIntent: (LabelPhotosIntent) -> Unit,
    editMode: Boolean,
    selectAll: Boolean,
) {
    var isSelected by rememberSaveable { mutableStateOf(selectAll) }
    LaunchedEffect(selectAll) { isSelected = selectAll }
    if (!editMode) isSelected = false

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        onIntent(LabelPhotosIntent.PhotoLongPress(photoDetail))
                        isSelected = true
                    },
                    onTap = {
                        if (editMode) {
                            isSelected = !isSelected

                            onIntent(
                                LabelPhotosIntent.PhotoTap(
                                    isSelected = isSelected,
                                    photo = photoDetail
                                )
                            )
                        } else {
                            onIntent(LabelPhotosIntent.PhotoClicked(photoDetail.id))
                        }
                    })
            }
    ) {
        Column {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(28.dp))
                    .fillMaxWidth()
            ) {
                PhotoDetailImage(photoDetail = photoDetail)
                if (isSelected) {
                    ImageOverlay(
                        modifier = Modifier
                            .matchParentSize()

                    )
                }
            }
            Text(
                text = photoDetail.description,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun PhotoDetailImage(photoDetail: PhotoDetail) {
    val rememberedImage = rememberSaveable(photoDetail.photoUri) { photoDetail.photoUri }
    val time = photoDetail.datetime
    AsyncImage(
        model = rememberedImage,
        contentDescription = stringResource(R.string.album_folder_screen_item_image_description),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun ImageOverlay(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.background(MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = stringResource(R.string.album_folder_screen_item_select_icon_description),
            tint = MaterialTheme.colorScheme.surface,
        )
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO)
@Composable
private fun AlbumFolderScreenPreview() {
    NatureAlbumTheme {
        LabelPhotosScreen(
            state = { LabelPhotosState() },
            onIntent = {},
            savePhotos = { },
            loadFolderData = {}
        )
    }
}
