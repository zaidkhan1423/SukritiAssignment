package com.zaid.sukritiassignment.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.zaid.sukritiassignment.data.model.AudioFile
import com.zaid.sukritiassignment.presentation.music_player_screen.MusicPlayerScreen
import com.zaid.sukritiassignment.presentation.music_list_screen.MusicListScreen
import com.zaid.sukritiassignment.presentation.view_model.MusicViewModel

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    musicViewModel: MusicViewModel
) {
    NavHost(navController = navHostController, startDestination = Screen.MusicListScreen) {

        composable<Screen.MusicListScreen> {
            MusicListScreen(navController = navHostController, musicViewModel)
        }
        composable<Screen.MusicPlayerScreen> {
            MusicPlayerScreen(navController = navHostController, viewModel = musicViewModel)
        }
    }
}