package com.and04.naturealbum.ui.component.topappbar

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.and04.naturealbum.ui.add.savephoto.SavePhotoScreen
import com.and04.naturealbum.ui.add.savephoto.contract.SavePhotoState
import com.and04.naturealbum.ui.album.labelphotos.LabelPhotosScreen
import com.and04.naturealbum.ui.album.labelphotos.contract.LabelPhotosState
import com.and04.naturealbum.ui.album.labels.LabelsScreen
import com.and04.naturealbum.ui.album.labels.contract.LabelsState
import com.and04.naturealbum.ui.album.photoinfo.PhotoInfoScreen
import com.and04.naturealbum.ui.album.photoinfo.contract.PhotoInfoState
import com.and04.naturealbum.ui.home.HomeScreen
import com.and04.naturealbum.ui.mypage.utils.LoginState
import com.and04.naturealbum.ui.mypage.MyPageScreenContent
import com.and04.naturealbum.ui.mypage.contract.MyPageState
import com.and04.naturealbum.ui.utils.LocationHandler
import org.junit.Rule
import org.junit.Test

class TopAppBarScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun 홈_화면() {
        composeTestRule.setContent {
            HomeScreen(LocationHandler(LocalContext.current),
                {},
                {},
                {},
                {}
            )
        }

        composeTestRule
            .onNodeWithText("Nature Album")
            .assertExists()


        composeTestRule
            .onNodeWithTag("navigation")
            .assertDoesNotExist()

        composeTestRule
            .onNodeWithTag("action")
            .assertExists()
    }

    @Test
    fun 마이페이지_화면() {
        composeTestRule.setContent {
            MyPageScreenContent(
                state = { MyPageState() },
                onIntent = {},
                myFriendsState = mutableStateOf(emptyList()),
                friendRequestsState = mutableStateOf(emptyList()),
                acceptFriendRequest = { _ -> },
                rejectFriendRequest = { _ -> },
                initializeFriendViewModel = { _ -> },
            )
        }

        composeTestRule
            .onNodeWithText("Nature Album")
            .assertExists()

        composeTestRule
            .onNodeWithTag("navigation")
            .assertExists()

        composeTestRule
            .onNodeWithTag("action")
            .assertDoesNotExist()
    }

    @Test
    fun 앨범_등록_화면() {
        val state = SavePhotoState()
        composeTestRule.setContent {
            SavePhotoScreen(
                state = { state },
                initState = { state },
                changeState = {},
                onIntent = {},
            )
        }

        composeTestRule
            .onNodeWithText("도감 등록")
            .assertExists()

        composeTestRule
            .onNodeWithTag("navigation")
            .assertExists()

        composeTestRule
            .onNodeWithTag("action")
            .assertExists()
    }

    @Test
    fun 앨범_화면() {
        composeTestRule.setContent {
            LabelsScreen(
                state = LabelsState(),
                onIntent = {}
            )
        }

        composeTestRule
            .onNodeWithText("Nature Album")
            .assertExists()

        composeTestRule
            .onNodeWithTag("navigation")
            .assertExists()

        composeTestRule
            .onNodeWithTag("action")
            .assertExists()
    }

    @Test
    fun 앨범_라벨_상세_화면() {
        composeTestRule.setContent {
            LabelPhotosScreen(
                state = { LabelPhotosState() },
                onIntent = {},
                savePhotos = { },
                loadFolderData = {}
            )
        }

        composeTestRule
            .onNodeWithText("Nature Album")
            .assertExists()

        composeTestRule
            .onNodeWithTag("navigation")
            .assertExists()

        composeTestRule
            .onNodeWithTag("action")
            .assertExists()
    }

    @Test
    fun 앨범_사진_상세_화면() {
        composeTestRule.setContent {
            PhotoInfoScreen(
                state = PhotoInfoState(),
                onIntent = {}
            )
        }

        composeTestRule
            .onNodeWithText("사진 정보")
            .assertExists()

        composeTestRule
            .onNodeWithTag("navigation")
            .assertExists()

        composeTestRule
            .onNodeWithTag("action")
            .assertExists()
    }

}
