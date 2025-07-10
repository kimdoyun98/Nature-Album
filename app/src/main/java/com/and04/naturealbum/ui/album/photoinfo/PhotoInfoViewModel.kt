package com.and04.naturealbum.ui.album.photoinfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.and04.naturealbum.data.localdata.room.PhotoDetail
import com.and04.naturealbum.data.repository.RetrofitRepository
import com.and04.naturealbum.data.repository.local.LabelRepository
import com.and04.naturealbum.data.repository.local.LocalAlbumRepository
import com.and04.naturealbum.data.repository.local.PhotoDetailRepository
import com.and04.naturealbum.ui.album.photoinfo.contract.PhotoInfoEffect
import com.and04.naturealbum.ui.album.photoinfo.contract.PhotoInfoIntent
import com.and04.naturealbum.ui.album.photoinfo.contract.PhotoInfoState
import com.and04.naturealbum.ui.album.photoinfo.utils.PhotoInfoUiState
import com.and04.naturealbum.utils.network.NetworkState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class PhotoInfoViewModel @Inject constructor(
    private val photoDetailRepository: PhotoDetailRepository,
    private val retrofitRepository: RetrofitRepository,
    private val localAlbumRepository: LocalAlbumRepository,
    private val labelRepository: LabelRepository,
) : ContainerHost<PhotoInfoState, PhotoInfoEffect>, ViewModel() {

    override val container: Container<PhotoInfoState, PhotoInfoEffect> = container(PhotoInfoState())

    init {
        intent {
            postSideEffect(PhotoInfoEffect.InitPhotoInfo)
        }
    }

    fun onIntent(intent: PhotoInfoIntent) = intent {
        when (intent) {
            is PhotoInfoIntent.BackButtonClicked -> {
                postSideEffect(PhotoInfoEffect.Back)
            }

            is PhotoInfoIntent.MyPageButtonClicked -> {
                postSideEffect(PhotoInfoEffect.MyPage)
            }

            is PhotoInfoIntent.SetThumbnailButtonClicked -> {
                setAlbumThumbnail(intent.id)

                postSideEffect(PhotoInfoEffect.Toast(intent.message))
            }
        }
    }

    fun loadPhotoDetail(id: Int) = intent {
        viewModelScope.launch {
            reduce { state.copy(state = PhotoInfoUiState.Loading) }

            val photoDetail = photoDetailRepository.getPhotoDetailById(id)
            val label = labelRepository.getLabelById(photoDetail.labelId)

            convertCoordsToAddress(photoDetail = photoDetail)

            reduce {
                state.copy(
                    state = PhotoInfoUiState.Success,
                    label = label,
                    photo = photoDetail
                )
            }
        }
    }

    private fun setAlbumThumbnail(photoDetailId: Int) {
        viewModelScope.launch {
            localAlbumRepository.updateAlbumPhotoDetailByAlbumId(photoDetailId)
        }
    }

    private fun convertCoordsToAddress(photoDetail: PhotoDetail) = intent {
        val coords = "${photoDetail.latitude}, ${photoDetail.longitude}"
        val cachedAddress = photoDetailRepository.getAddressByPhotoDetailId(photoDetail.id)

        if (cachedAddress.isNotEmpty()) {
            reduce { state.copy(address = cachedAddress) }
            return@intent
        }

        if (NetworkState.getNetWorkCode() == NetworkState.DISCONNECTED) {
            reduce { state.copy(address = coords) }
            return@intent
        }

        val newAddress = retrofitRepository.convertCoordsToAddress(
            latitude = photoDetail.latitude,
            longitude = photoDetail.longitude
        )

        if (newAddress.isNotEmpty()) {
            photoDetailRepository.updateAddressByPhotoDetailId(newAddress, photoDetail.id)
            reduce { state.copy(address = newAddress) }
        } else {
            reduce { state.copy(address = coords) }
        }
    }
}
