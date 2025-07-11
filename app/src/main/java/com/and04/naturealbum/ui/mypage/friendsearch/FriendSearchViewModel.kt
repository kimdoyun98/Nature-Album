package com.and04.naturealbum.ui.mypage.friendsearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.and04.naturealbum.data.dto.FirestoreUserWithStatus
import com.and04.naturealbum.data.dto.FriendStatus
import com.and04.naturealbum.data.repository.firebase.FriendRepository
import com.and04.naturealbum.ui.mypage.friendsearch.contract.FriendSearchEffect
import com.and04.naturealbum.ui.mypage.friendsearch.contract.FriendSearchState
import com.and04.naturealbum.ui.utils.UserManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class FriendSearchViewModel @Inject constructor(
    private val friendRepository: FriendRepository,
    private val userManager: UserManager,
) : ContainerHost<FriendSearchState, FriendSearchEffect>, ViewModel() {

    override val container: Container<FriendSearchState, FriendSearchEffect> =
        container(FriendSearchState())

    private val _searchResults = MutableStateFlow<Map<String, FirestoreUserWithStatus>>(emptyMap())
    val searchResults: StateFlow<Map<String, FirestoreUserWithStatus>> = _searchResults

    private val _friendRequestStatus = MutableStateFlow<Boolean?>(null)
    val friendRequestStatus: StateFlow<Boolean?> = _friendRequestStatus

    private val uid: String? = userManager.getUser()?.uid

    private var currentSearchJob: Job? = null

    private val _searchQuery = MutableStateFlow("")

    init {
        _searchQuery
            .debounce(DEBOUNCE_PERIOD)
            .distinctUntilChanged()
            .filter { uid != null }
            .onEach { query -> fetchFilteredUsersAsFlow(query) }
            .launchIn(viewModelScope)
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    private fun fetchFilteredUsersAsFlow(query: String) {
        currentSearchJob?.cancel()
        currentSearchJob = viewModelScope.launch {
            friendRepository.searchUsersAsFlow(uid!!, query)
                .collectLatest { results ->
                    _searchResults.value = results
                }
        }
    }

    fun setFriendRequestStatusNull() {
        _friendRequestStatus.value = null
    }

    fun sendFriendRequest(targetUid: String) {
        uid?.let { currentUid ->
            viewModelScope.launch {
                val result = friendRepository.sendFriendRequest(currentUid, targetUid)
                    .onSuccess {
                        _searchResults.value = _searchResults.value.toMutableMap().apply {
                            this[targetUid] =
                                this[targetUid]?.copy(status = FriendStatus.SENT) ?: return@launch
                        }
                    }

                _friendRequestStatus.value = result.isSuccess
            }
        }
    }

    companion object {
        private const val DEBOUNCE_PERIOD = 100L
    }
}
