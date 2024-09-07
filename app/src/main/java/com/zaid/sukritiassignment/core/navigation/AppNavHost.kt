package com.zaid.sukritiassignment.core.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.zaid.sukritiassignment.presentation.music_screens.MusicPlayerScreen
import com.zaid.sukritiassignment.presentation.music_screens.MusicListScreen
import com.zaid.sukritiassignment.presentation.view_model.MusicViewModel

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    musicViewModel: MusicViewModel,
    onShowSnackBar: suspend (message: String, actionLabel: String?, duration: SnackbarDuration) -> Boolean
) {
    val uiState by musicViewModel.musicUiState.collectAsStateWithLifecycle()
    val mediaPlayer by musicViewModel.mediaPlayer.collectAsStateWithLifecycle()

    SharedTransitionLayout {
        NavHost(navController = navHostController, startDestination = Screen.MusicListScreen) {

            composable<Screen.MusicListScreen> {

                MusicListScreen(
                    navController = navHostController,
                    uiState = uiState,
                    onShowSnackBar = onShowSnackBar,
                    onEvent = musicViewModel::onEvent,
                    mediaPlayer = mediaPlayer,
                    animatedVisibilityScope = this
                )


            }
            composable<Screen.MusicPlayerScreen> {

                MusicPlayerScreen(
                    navController = navHostController,
                    uiState = uiState,
                    onShowSnackBar = onShowSnackBar,
                    onEvent = musicViewModel::onEvent,
                    mediaPlayer = mediaPlayer,
                    animatedVisibilityScope = this
                )
            }


        }
    }
}