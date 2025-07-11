package com.and04.naturealbum.ui.mypage.mypage

import android.content.Context
import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Badge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.and04.naturealbum.R
import com.and04.naturealbum.background.workmanager.SynchronizationWorker
import com.and04.naturealbum.data.dto.FirebaseFriend
import com.and04.naturealbum.data.dto.FirebaseFriendRequest
import com.and04.naturealbum.data.dto.MyFriend
import com.and04.naturealbum.data.model.UserInfo
import com.and04.naturealbum.ui.component.AppBarType
import com.and04.naturealbum.ui.component.ProgressIndicator
import com.and04.naturealbum.ui.component.RotatingButton
import com.and04.naturealbum.ui.mypage.component.NoNetworkSocialContent
import com.and04.naturealbum.ui.mypage.mypage.contract.MyPageIntent
import com.and04.naturealbum.ui.mypage.mypage.contract.MyPageState
import com.and04.naturealbum.ui.mypage.utils.LoginState
import com.and04.naturealbum.ui.mypage.utils.MyPageAlarm
import com.and04.naturealbum.ui.mypage.utils.MyPageSocialList
import com.and04.naturealbum.ui.theme.NatureAlbumTheme
import com.and04.naturealbum.ui.utils.PermissionHandler
import com.and04.naturealbum.utils.GetTopBar
import com.and04.naturealbum.utils.network.NetworkState
import com.and04.naturealbum.utils.network.NetworkState.CONNECTED_DATA
import com.and04.naturealbum.utils.network.NetworkState.CONNECTED_WIFI
import com.and04.naturealbum.utils.network.NetworkState.DISCONNECTED
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private const val SOCIAL_LIST_TAB_INDEX = 0
private const val SOCIAL_SEARCH_TAB_INDEX = 1
private const val SOCIAL_ALARM_TAB_INDEX = 2

@Composable
fun MyPageScreen(
    state: () -> MyPageState,
    onIntent: (MyPageIntent) -> Unit,
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            context.GetTopBar(
                type = AppBarType.Navigation,
                navigateToBackScreen = { onIntent(MyPageIntent.BackButtonClicked) }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) }
    ) { innerPadding ->
        MyPageContent(
            modifier = Modifier.padding(innerPadding),
            state = state(),
            onIntent = onIntent,
            snackBarHostState = snackBarHostState,
        )
    }
}

@Composable
private fun MyPageContent(
    modifier: Modifier,
    state: MyPageState,
    onIntent: (MyPageIntent) -> Unit,
    snackBarHostState: SnackbarHostState,
) {
    val requestPermissionLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestMultiplePermissions()) {}

    val context = LocalContext.current
    val permissionHandler = remember {
        PermissionHandler(
            context = context,
            allPermissionGranted = {},
            onRequestPermission = { deniedPermissions ->
                requestPermissionLauncher.launch(deniedPermissions)
            },
        )
    }

    Column(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(32.dp),
    ) {
        when (val loginState = state.loginState) {
            is LoginState.Login -> {
                UserProfileContent(
                    userInfo = loginState.userInfo,
                )

                SyncContent(
                    snackBarHostState = snackBarHostState,
                    recentSyncTime = state.recentSyncTime,
                    isSyncWorking = state.isSyncWorking,
                    onClick = { onIntent(MyPageIntent.SyncButtonClicked) }
                )

                SocialContent(
                    friends = state.friends,
                    friendRequests = state.receivedFriendRequests,
                    networkState = state.networkState,
                    navigateToFriendSearchScreen = {
                        onIntent(MyPageIntent.FriendSearchClicked)
                    },
                    acceptFriendRequest = { uid ->
                        onIntent(MyPageIntent.FriendRequestAccept(uid))
                    },
                    rejectFriendRequest = { uid ->
                        onIntent(MyPageIntent.FriendRequestReject(uid))
                    },
                )

                LaunchedEffect(Unit) {
                    permissionHandler.checkPermissions(PermissionHandler.Permissions.NOTIFICATION)
                }
            }

            is LoginState.Logout, LoginState.LoginLoading -> {
                Box {
                    ProgressIndicator(state.loginState is LoginState.LoginLoading)
                }

                UserProfileContent()

                LoginContent(
                    loginHandle = { onIntent(MyPageIntent.LoginClicked(context)) }
                )
            }
        }
    }
}

@Composable
private fun UserProfileContent(
    userInfo: UserInfo? = null,
) {
    val uri = userInfo?.userPhotoUri ?: ""
    val email = userInfo?.userEmail ?: stringResource(R.string.my_page_default_user_email)
    val displayName = userInfo?.userDisplayName ?: ""

    UserProfileImage(
        uri = uri,
        modifier = Modifier
            .fillMaxHeight(0.2f)
            .aspectRatio(1f)
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = displayName,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Text(
            text = email,
            modifier = Modifier.fillMaxWidth(),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun UserProfileImage(uri: String?, modifier: Modifier) {
    uri?.let {
        AsyncImage(
            model = uri,
            contentDescription = stringResource(R.string.my_page_user_profile_image),
            modifier = modifier.clip(CircleShape)
        )
    } ?: Image(
        imageVector = Icons.Default.AccountCircle,
        contentDescription = stringResource(R.string.my_page_user_profile_image),
        modifier = modifier
    )

}

@Composable
private fun LoginContent(
    loginHandle: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = Color.Gray, shape = RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .clickable { loginHandle() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            Image(
                imageVector = ImageVector.vectorResource(R.drawable.ic_google_login),
                contentDescription = null,
            )

            Spacer(modifier = Modifier.width(24.dp))

            Text(text = stringResource(R.string.my_page_google_login_btn))
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SocialContent(
    friends: ImmutableList<FirebaseFriend>,
    friendRequests: ImmutableList<FirebaseFriendRequest>,
    networkState: Int,
    navigateToFriendSearchScreen: () -> Unit,
    acceptFriendRequest: (String) -> Unit,
    rejectFriendRequest: (String) -> Unit,
) {
    if (networkState == DISCONNECTED) {
        NoNetworkSocialContent()
        return
    }

    val friendRequestsCount = friendRequests.size

    var tabState by remember { mutableIntStateOf(SOCIAL_LIST_TAB_INDEX) }

    val titles = listOf(
        stringResource(R.string.my_page_social_list),
        stringResource(R.string.my_page_social_search),
        stringResource(R.string.my_page_social_alarm)
    )

    Column {
        PrimaryTabRow(selectedTabIndex = tabState) {
            titles.forEachIndexed { index, title ->
                MyPageCustomTab(tabState, index, title, friendRequestsCount) {
                    tabState = index
                }
            }
        }

        when (tabState) {
            SOCIAL_LIST_TAB_INDEX -> MyPageSocialList(friends) // 친구 목록
            SOCIAL_SEARCH_TAB_INDEX -> {
                navigateToFriendSearchScreen()
            }

            SOCIAL_ALARM_TAB_INDEX -> MyPageAlarm(
                myAlarms = friendRequests,
                onAccept = acceptFriendRequest,
                onDenied = rejectFriendRequest,
            )
        }
    }
}

@Composable
private fun MyPageCustomTab(
    tabState: Int,
    index: Int,
    title: String,
    friendRequestsCount: Int,
    onClick: () -> Unit,
) {
    Tab(
        selected = tabState == index,
        onClick = onClick,
        text = {
            Row {
                Text(
                    text = title, maxLines = 2, overflow = TextOverflow.Ellipsis
                )

                Box {
                    if (index == SOCIAL_ALARM_TAB_INDEX && friendRequestsCount > 0) {
                        Badge(
                            modifier = Modifier.padding(start = 8.dp),
                            containerColor = Color.Red,
                            contentColor = Color.White
                        ) {
                            Text("$friendRequestsCount")
                        }
                    }
                }
            }
        }
    )
}

@Composable
private fun SyncContent(
    snackBarHostState: SnackbarHostState,
    recentSyncTime: String,
    isSyncWorking: Boolean,
    onClick: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(stringResource(R.string.my_page_sync))
            IconButton(
                modifier = Modifier.size(24.dp),
                onClick = {
                    when (NetworkState.getNetWorkCode()) {
                        CONNECTED_WIFI -> {
                            SynchronizationWorker.runImmediately(context)
                            onClick()
                        }

                        CONNECTED_DATA -> {
                            startSnackBar(
                                context = context,
                                coroutineScope = coroutineScope,
                                snackBarHostState = snackBarHostState,
                                message = context.getString(R.string.my_page_snackbar_network_state_data_keep_going),
                                actionLabel = context.getString(R.string.my_page_snackbar_confirm_button),
                                onClickActionPerformed = { onClick() }
                            )
                        }

                        DISCONNECTED -> {
                            startSnackBar(
                                context = context,
                                coroutineScope = coroutineScope,
                                snackBarHostState = snackBarHostState,
                                message = context.getString(R.string.my_page_snackbar_network_state_disconnect),
                                actionLabel = null
                            )
                        }
                    }
                }
            ) {
                RotatingButton(
                    rotatingState = isSyncWorking,
                    imageVector = Icons.Default.Sync,
                    contentDescription = stringResource(R.string.my_page_sync_icon_content_description)
                )
            }
        }

        Text(
            style = MaterialTheme.typography.bodySmall,
            text = recentSyncTime
        )
    }
}

private fun startSnackBar(
    context: Context,
    coroutineScope: CoroutineScope,
    snackBarHostState: SnackbarHostState,
    message: String,
    actionLabel: String?,
    onClickActionPerformed: () -> Unit = {},
) {
    coroutineScope.launch {
        val result = snackBarHostState.showSnackbar(
            message = message,
            actionLabel = actionLabel,
            duration = SnackbarDuration.Long,
        )

        if (result == SnackbarResult.ActionPerformed) {
            SynchronizationWorker.runImmediately(context)
            onClickActionPerformed()
        }
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO)
@Composable
private fun MyPageScreenPreview() {
    val loginState = remember { mutableStateOf(LoginState.Logout) }
    val myFriends = remember {
        mutableStateOf(
            listOf(
                MyFriend("", "grand2181@gmail.com", true),
                MyFriend("", "도윤@gmail.com", true),
                MyFriend("", "정호@gmail.com", true)
            )
        )
    }
    val recentSyncTime = remember { mutableStateOf("2024-01-01") }

    // 테스트용 사용자 정보
    val userEmail = remember { mutableStateOf("test@example.com") }
    val userPhotoUrl = remember { mutableStateOf("https://via.placeholder.com/150") }
    val userDisplayName = remember { mutableStateOf("Test User") }

    NatureAlbumTheme {
//        MyPageScreen(
//            navigateToHome = {},
//            loginState = loginState,
//            myFriends = myFriends,
//            userEmail = userEmail.value,
//            userPhotoUrl = userPhotoUrl.value,
//            userDisplayName = userDisplayName.value,
//            signInWithGoogle = {}
//        )
    }
}
