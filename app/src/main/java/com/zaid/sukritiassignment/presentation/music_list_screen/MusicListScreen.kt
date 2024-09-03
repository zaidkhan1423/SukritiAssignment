package com.zaid.sukritiassignment.presentation.music_list_screen

import android.media.MediaPlayer
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.zaid.sukritiassignment.R
import com.zaid.sukritiassignment.data.model.AudioFile
import com.zaid.sukritiassignment.data.repository.MusicRepositoryImpl
import com.zaid.sukritiassignment.presentation.navigation.Screen
import com.zaid.sukritiassignment.presentation.view_model.MusicViewModel
import com.zaid.sukritiassignment.ui.theme.SukritiAssignmentTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicListScreen(navController: NavController, viewModel: MusicViewModel) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val audioFiles by viewModel.audioFiles.collectAsStateWithLifecycle()
        val isMusicPlaying by viewModel.isMusicPlaying.collectAsStateWithLifecycle()
        val mediaPlayer by viewModel.mediaPlayer.collectAsState()
        val playingAudioFile by viewModel.playingAudioFile.collectAsStateWithLifecycle()

        Column {
            TopAppBar(
                title = {
                    Text(
                        text = "Music Player",
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                modifier = Modifier
                    .fillMaxWidth(),
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
            HorizontalDivider()
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                items(audioFiles.size) { index ->
                    MusicItem(
                        audio = audioFiles[index],
                        onClick = {
                            viewModel.stopMusic()
                            viewModel.playAudio(audioFiles[index])
                            navController.navigate(Screen.MusicPlayerScreen)
                        })
                }
            }
        }
        if (mediaPlayer != null) {
            Box(
                modifier = Modifier
                    .padding(10.dp)
                    .clip(shape = RoundedCornerShape(8.dp))
                    .clickable { navController.navigate(Screen.MusicPlayerScreen) }
                    .align(Alignment.BottomCenter)
                    .background(MaterialTheme.colorScheme.outlineVariant)
                    .padding(10.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .fillMaxWidth(0.8f)
                ) {
                    AsyncImage(
                        model = playingAudioFile?.albumArtUri,
                        contentDescription = null,
                        modifier = Modifier
                            .clip(shape = RoundedCornerShape(10.dp))
                            .size(50.dp),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Column(
                        modifier = Modifier
                    ) {
                        playingAudioFile?.let {
                            Text(
                                text = it.name,
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1
                            )
                            val minutes = (playingAudioFile!!.duration / 1000) / 60
                            val seconds = (playingAudioFile!!.duration / 1000) % 60

                            Text(
                                text = "Duration: $minutes:${
                                    seconds.toString().padStart(2, '0')
                                } minutes",
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                            )
                        }
                    }
                }
                Icon(
                    if (isMusicPlaying) painterResource(id = R.drawable.ic_pause_btn) else painterResource(
                        id = R.drawable.ic_play_btn
                    ), contentDescription = null,
                    modifier = Modifier
                        .clickable {
                            if (isMusicPlaying) {
                                viewModel.pauseMusic()
                            } else {
                                viewModel.startMusic()
                            }
                        }
                        .size(45.dp)
                        .align(Alignment.CenterEnd),
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

@Composable
fun MusicItem(audio: AudioFile, onClick: (AudioFile) -> Unit) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .clickable { onClick(audio) }
        .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .fillMaxWidth(0.8f)
        ) {
            Text(
                text = audio.name,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
            val minutes = (audio.duration / 1000) / 60
            val seconds = (audio.duration / 1000) % 60

            Text(
                text = "Duration: $minutes:${seconds.toString().padStart(2, '0')} minutes",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        AsyncImage(
            model = audio.albumArtUri,
            contentDescription = null,
            modifier = Modifier
                .clip(shape = RoundedCornerShape(10.dp))
                .size(50.dp)
                .align(Alignment.CenterEnd),
            contentScale = ContentScale.Crop
        )
    }
    HorizontalDivider()
}

//@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
//@Preview(showBackground = true)
//@Composable
//fun MusicListScreenPreview() {
//    SukritiAssignmentTheme {
//        MusicListScreen(
//            navController = NavController(LocalContext.current), viewModel = MusicViewModel(
//                MusicRepositoryImpl(
//                    context = LocalContext.current, mediaPlayer = MediaPlayer(LocalContext.current)
//                )
//            )
//        )
//    }
//}