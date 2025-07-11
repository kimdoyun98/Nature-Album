package com.and04.naturealbum.ui.mypage.friendsearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.and04.naturealbum.R
import com.and04.naturealbum.data.dto.FriendStatus
import com.and04.naturealbum.data.repository.firebase.FriendRepository
import com.and04.naturealbum.ui.mypage.friendsearch.contract.FriendSearchEffect
import com.and04.naturealbum.ui.mypage.friendsearch.contract.FriendSearchIntent
import com.and04.naturealbum.ui.mypage.friendsearch.contract.FriendSearchState
import com.and04.naturealbum.ui.utils.UserManager
import com.and04.naturealbum.utils.network.NetworkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
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
    private val networkManager: NetworkManager,
) : ContainerHost<FriendSearchState, FriendSearchEffect>, ViewModel() {

    override val container: Container<FriendSearchState, FriendSearchEffect> =
        container(FriendSearchState())

    private var currentSearchJob: Job? = null
    private val searchQuery = MutableStateFlow("")
    private val friendRequestStatus: MutableStateFlow<Boolean?> = MutableStateFlow(null)
    private val uid: String? = userManager.getUser()?.uid

    init {
        checkNetwork()

        searchQuery
            .onEach { query ->
                intent { reduce { state.copy(searchQuery = query) } }
            }
            .debounce(DEBOUNCE_PERIOD)
            .filter { uid != null }
            .onEach { query ->
                fetchFilteredUsersAsFlow(query)
            }
            .launchIn(viewModelScope)

        friendRequestStatus
            .filter { it != null }
            .onEach { success ->
                intent {
                    postSideEffect(
                        FriendSearchEffect.Toast(
                            if (success!!) {
                                R.string.friend_search_screen_friend_request_success
                            } else {
                                R.string.friend_search_screen_friend_request_fail
                            }
                        )
                    )
                }

                setFriendRequestStatusNull()
            }
            .launchIn(viewModelScope)
    }

    fun onIntent(intent: FriendSearchIntent) = intent {
        when (intent) {
            is FriendSearchIntent.QueryChanged -> {
                searchQuery.value = intent.query
            }

            is FriendSearchIntent.ExpandedChanged -> {
                reduce { state.copy(expanded = intent.expanded) }
            }

            is FriendSearchIntent.FriendRequestSend -> {
                sendFriendRequest(intent.id)
            }

            is FriendSearchIntent.BackButtonClicked -> {
                postSideEffect(FriendSearchEffect.Back)
            }
        }
    }

    private fun setFriendRequestStatusNull() {
        friendRequestStatus.value = null
    }

    private fun sendFriendRequest(targetUid: String) = intent {
        uid?.let { currentUid ->
            viewModelScope.launch {
                val result = friendRepository.sendFriendRequest(currentUid, targetUid)
                    .onSuccess {
                        val newResult = state.searchResult.toMutableMap().apply {
                            this[targetUid] =
                                this[targetUid]?.copy(status = FriendStatus.SENT) ?: return@launch
                        }

                        reduce { state.copy(searchResult = newResult.toImmutableMap()) }
                    }

                friendRequestStatus.value = result.isSuccess
            }
        }
    }

    private fun fetchFilteredUsersAsFlow(query: String) = intent {
        currentSearchJob?.cancel()
        currentSearchJob = viewModelScope.launch {
            friendRepository.searchUsersAsFlow(uid!!, query)
                .collectLatest { results ->
                    reduce { state.copy(searchResult = results.toImmutableMap()) }
                }
        }
    }

    private fun checkNetwork() = intent {
        viewModelScope.launch {
            networkManager.networkState.collect { networkState ->
                reduce {
                    state.copy(networkState = networkState)
                }
            }
        }
    }

    companion object {
        private const val DEBOUNCE_PERIOD = 100L
    }
}
