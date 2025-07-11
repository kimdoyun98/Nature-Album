package com.and04.naturealbum.ui.mypage.mypage

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.and04.naturealbum.background.workmanager.SynchronizationWorker
import com.and04.naturealbum.data.localdata.datastore.DataStoreManager
import com.and04.naturealbum.data.repository.firebase.FriendRepository
import com.and04.naturealbum.ui.mypage.mypage.contract.MyPageEffect
import com.and04.naturealbum.ui.mypage.mypage.contract.MyPageIntent
import com.and04.naturealbum.ui.mypage.mypage.contract.MyPageState
import com.and04.naturealbum.ui.mypage.utils.AuthResponse
import com.and04.naturealbum.ui.mypage.utils.AuthenticationManager
import com.and04.naturealbum.ui.mypage.utils.LoginState
import com.and04.naturealbum.ui.utils.UserManager
import com.and04.naturealbum.utils.network.NetworkManager
import com.and04.naturealbum.utils.network.NetworkState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val friendRepository: FriendRepository,
    private val authenticationManager: AuthenticationManager,
    private val userManager: UserManager,
    private val syncDataStore: DataStoreManager,
    private val networkManager: NetworkManager,
) : ContainerHost<MyPageState, MyPageEffect>, ViewModel() {

    override val container: Container<MyPageState, MyPageEffect> = container(MyPageState())

    private var uid: String? = userManager.getUser()?.uid

    init {
        checkNetwork()
        syncTime()
        listenToFriends()
        listenToReceivedFriendRequests()
    }

    fun onIntent(intent: MyPageIntent) = intent {
        when (intent) {
            is MyPageIntent.BackButtonClicked -> {
                postSideEffect(MyPageEffect.Back)
            }

            is MyPageIntent.FriendSearchClicked -> {
                postSideEffect(MyPageEffect.FriendSearch)
            }

            is MyPageIntent.LoginClicked -> {
                if (state.networkState != NetworkState.DISCONNECTED) {
                    signInWithGoogle(intent.context)
                } else {
                    postSideEffect(MyPageEffect.Toast(intent.massage))
                }
            }

            is MyPageIntent.SyncButtonClicked -> {
                startSync()
            }

            is MyPageIntent.FriendRequestAccept -> {
                acceptFriendRequest(intent.friendUid)
            }

            is MyPageIntent.FriendRequestReject -> {
                rejectFriendRequest(intent.friendUid)
            }
        }
    }

    private fun startSync() = intent {
        viewModelScope.launch {
            var syncWorkingStatus = false
            while (true) {
                val status = SynchronizationWorker.isWorking()
                reduce { state.copy(isSyncWorking = status) }

                if (syncWorkingStatus && !status) break
                syncWorkingStatus = status
                delay(1_00L)
            }
        }
    }

    private fun syncTime() = intent {
        syncDataStore.syncTime
            .onEach { time ->
                reduce { state.copy(recentSyncTime = time) }
            }.launchIn(viewModelScope)
    }

    private fun signInWithGoogle(context: Context) = intent {
        reduce { state.copy(loginState = LoginState.LoginLoading) }
        authenticationManager
            .signInWithGoogle(context)
            .onEach { response ->
                when (response) {
                    is AuthResponse.Success -> {
                        reduce { state.copy(loginState = LoginState.initState(userManager)) }

                        uid = userManager.getUser()?.uid ?: throw Exception()

                        listenToFriends()
                        listenToReceivedFriendRequests()
                    }

                    is AuthResponse.Error -> {
                        reduce { state.copy(loginState = LoginState.Logout) }
                    }
                }
            }.launchIn(viewModelScope)
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

    private fun listenToFriends() = intent {
        uid?.let { currentUid ->
            viewModelScope.launch {
                friendRepository
                    .getFriendsAsFlow(currentUid)
                    .collect { friends ->
                        reduce { state.copy(friends = friends.toImmutableList()) }
                    }
            }
        }
    }

    private fun listenToReceivedFriendRequests() = intent {
        uid?.let { currentUid ->
            viewModelScope.launch {
                friendRepository
                    .getReceivedFriendRequestsAsFlow(currentUid)
                    .collect { receivedFriendRequests ->
                        reduce {
                            state.copy(
                                receivedFriendRequests = receivedFriendRequests.toImmutableList()
                            )
                        }
                    }
            }
        }
    }

    private fun rejectFriendRequest(targetUid: String) {
        uid?.let { currentUid ->
            viewModelScope.launch {
                friendRepository.rejectFriendRequest(currentUid, targetUid)
            }
        }
    }

    private fun acceptFriendRequest(targetUid: String) {
        uid?.let { currentUid ->
            viewModelScope.launch {
                friendRepository.acceptFriendRequest(currentUid, targetUid)
            }
        }
    }
}
