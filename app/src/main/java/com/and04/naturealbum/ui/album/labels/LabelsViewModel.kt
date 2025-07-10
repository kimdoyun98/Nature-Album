package com.and04.naturealbum.ui.album.labels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.and04.naturealbum.data.repository.local.LocalAlbumRepository
import com.and04.naturealbum.ui.album.labels.contract.LabelsEffect
import com.and04.naturealbum.ui.album.labels.contract.LabelsIntent
import com.and04.naturealbum.ui.album.labels.contract.LabelsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class LabelsViewModel @Inject constructor(
    private val repository: LocalAlbumRepository,
) : ContainerHost<LabelsState, LabelsEffect>, ViewModel() {

    override val container: Container<LabelsState, LabelsEffect> = container(LabelsState())

    init {
        intent {
            repository.getAllAlbum()
                .onEach { labels ->
                    reduce { state.copy(labels = labels.toImmutableList()) }
                }
                .launchIn(viewModelScope)
        }
    }

    fun onIntent(intent: LabelsIntent) = intent {
        when (intent) {
            is LabelsIntent.LabelClicked -> {
                postSideEffect(LabelsEffect.LabelPhotos(intent.id))
            }

            is LabelsIntent.MyPageClicked -> {
                postSideEffect(LabelsEffect.MyPage)
            }

            is LabelsIntent.BackButtonClicked -> {
                postSideEffect(LabelsEffect.Back)
            }
        }
    }
}
