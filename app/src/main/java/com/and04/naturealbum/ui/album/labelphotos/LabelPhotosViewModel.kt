package com.and04.naturealbum.ui.album.labelphotos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.and04.naturealbum.data.localdata.datastore.DataStoreManager
import com.and04.naturealbum.data.localdata.room.PhotoDetail
import com.and04.naturealbum.data.repository.firebase.AlbumRepository
import com.and04.naturealbum.data.repository.local.LabelRepository
import com.and04.naturealbum.data.repository.local.PhotoDetailRepository
import com.and04.naturealbum.ui.album.labelphotos.contract.LabelPhotosEffect
import com.and04.naturealbum.ui.album.labelphotos.contract.LabelPhotosIntent
import com.and04.naturealbum.ui.album.labelphotos.contract.LabelPhotosState
import com.and04.naturealbum.ui.album.labelphotos.utils.LabelPhotosUiState
import com.and04.naturealbum.ui.utils.UserManager
import com.and04.naturealbum.utils.network.NetworkState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import java.io.File
import javax.inject.Inject

@HiltViewModel
class LabelPhotosViewModel @Inject constructor(
    private val photoDetailRepository: PhotoDetailRepository,
    private val syncDataStore: DataStoreManager,
    private val albumRepository: AlbumRepository,
    private val labelRepository: LabelRepository,
) : ContainerHost<LabelPhotosState, LabelPhotosEffect>, ViewModel() {

    override val container: Container<LabelPhotosState, LabelPhotosEffect> =
        container(LabelPhotosState())

    fun onIntent(intent: LabelPhotosIntent) = intent {
        when (intent) {
            is LabelPhotosIntent.EditModeSwitched -> {
                reduce { state.copy(editMode = intent.isEditModeEnabled) }
            }

            is LabelPhotosIntent.ImageDownLoading -> {
                reduce { state.copy(imgDownLoading = intent.isImgDownLoading) }
            }

            is LabelPhotosIntent.PermissionDialogStateSet -> {
                reduce { state.copy(permissionDialogState = intent.pdState) }
            }

            is LabelPhotosIntent.PhotosDelete -> {
                deletePhotos(state.checkList)

                reduce { state.copy(editMode = false) }
            }

            is LabelPhotosIntent.SelectAll -> {
                reduce {
                    state.copy(
                        selectAll = intent.isAllSelected,
                        checkList =
                        if (intent.isAllSelected) state.photos.toImmutableSet()
                        else persistentSetOf(),
                    )
                }
            }

            is LabelPhotosIntent.EditModeBack -> {
                reduce {
                    state.copy(
                        editMode = false,
                        checkList = persistentSetOf()
                    )
                }
            }

            is LabelPhotosIntent.PhotoLongPress -> {
                val newCheckSet = state.checkList.toMutableSet()
                newCheckSet += intent.photo

                reduce {
                    state.copy(
                        editMode = true,
                        checkList = newCheckSet.toImmutableSet()
                    )
                }
            }

            is LabelPhotosIntent.PhotoTap -> {
                val newCheckSet = state.checkList.toMutableSet()

                if (intent.isSelected) newCheckSet += intent.photo
                else newCheckSet -= intent.photo

                reduce { state.copy(checkList = newCheckSet.toImmutableSet()) }
            }

            is LabelPhotosIntent.PhotoClicked -> {
                postSideEffect(LabelPhotosEffect.PhotoInfo(intent.id))
            }

            is LabelPhotosIntent.MyPageClicked -> {
                postSideEffect(LabelPhotosEffect.MyPage)
            }

            is LabelPhotosIntent.BackButtonClicked -> {
                postSideEffect(LabelPhotosEffect.Back)
            }
        }
    }

    fun loadFolderData(labelId: Int) = intent {
        viewModelScope.launch {
            reduce { state.copy(state = LabelPhotosUiState.Loading) }

            val labelJob = async {
                labelRepository.getLabelById(id = labelId)
            }

            val photoDetailsJob = async {
                photoDetailRepository.getPhotoDetailsUriByLabelId(labelId = labelId).reversed()
            }

            val labelData = labelJob.await()
            val photoDetailsData = photoDetailsJob.await().toImmutableList()

            reduce {
                state.copy(
                    state = LabelPhotosUiState.Success,
                    label = labelData,
                    photos = photoDetailsData
                )
            }
        }
    }

    private fun deletePhotos(photoDetails: Set<PhotoDetail>) = intent {
        viewModelScope.launch {
            val updatedPhotoDetails = state.photos.toMutableList()
            photoDetails.forEach { photoDetail ->
                photoDetailRepository.deleteImage(photoDetail) // Room에서 삭제
                syncDataStore.setDeletedFileName(photoDetail.fileName) // 삭제 정보를 DataStore에 저장

                launch(Dispatchers.IO) { deleteFile(photoDetail.fileName) } //file에서 이미지 삭제

                launch(Dispatchers.IO) {
                    val uid = UserManager.getUser()?.uid
                    if (NetworkState.getNetWorkCode() != NetworkState.DISCONNECTED && !uid.isNullOrEmpty()) {
                        val label = labelRepository.getLabelById(photoDetail.labelId)
                        albumRepository.deleteImageFile(
                            uid = uid,
                            label = label,
                            fileName = photoDetail.fileName,
                        )
                    }
                }
                updatedPhotoDetails.remove(photoDetail)
            }

            if (updatedPhotoDetails.isEmpty()) postSideEffect(LabelPhotosEffect.Error)
            else reduce { state.copy(photos = updatedPhotoDetails.toImmutableList()) }
        }
    }

    private fun deleteFile(fileName: String) {
        val file = File("$FILE_PATH${fileName}")
        if (file.exists()) {
            file.delete()
        }
    }

    companion object {
        private const val FILE_PATH = "/data/user/0/com.and04.naturealbum/files/"
    }
}
