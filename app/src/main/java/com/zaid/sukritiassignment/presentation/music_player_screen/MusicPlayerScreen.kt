package com.zaid.sukritiassignment.presentation.music_player_screen

import android.media.MediaPlayer
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import com.zaid.sukritiassignment.presentation.view_model.MusicViewModel
import com.zaid.sukritiassignment.ui.theme.SukritiAssignmentTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicPlayerScreen(
    navController: NavController,
    viewModel: MusicViewModel
) {
    val mediaPlayer by viewModel.mediaPlayer.collectAsStateWithLifecycle()
    val playingAudioFile by viewModel.playingAudioFile.collectAsStateWithLifecycle()
    val isMusicPlaying by viewModel.isMusicPlaying.collectAsStateWithLifecycle()
    var seekPosition by rememberSaveable { mutableIntStateOf(0) }

    LaunchedEffect(mediaPlayer) {
        mediaPlayer?.let {
            viewModel.updateSeekBarPosition().collect { position ->
                seekPosition = position
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TopAppBar(
            title = {
                Text(
                    text = playingAudioFile?.name ?: "Unknown Song",
                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
        )
        HorizontalDivider()
        playingAudioFile?.let { audioFile ->
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.9f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AsyncImage(
                        model = audioFile.albumArtUri,
                        contentDescription = null,
                        modifier = Modifier
                            .size(280.dp)
                            .clip(RoundedCornerShape(20.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.size(20.dp))
                    Text(
                        text = audioFile.name,
                        fontSize = MaterialTheme.typography.titleMedium.fontSize,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Slider(
                        value = seekPosition.toFloat(),
                        onValueChange = { newPosition ->
                            seekPosition = newPosition.toInt()
                        },
                        valueRange = 0f..(audioFile.duration.toFloat()),
                        onValueChangeFinished = {
                            viewModel.seekTo(seekPosition)
                        }
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = formatTime(seekPosition),
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = MaterialTheme.typography.bodyMedium.fontSize
                        )
                        Text(
                            text = formatTime(audioFile.duration.toInt()),
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = MaterialTheme.typography.bodyMedium.fontSize
                        )
                    }
                    Spacer(modifier = Modifier.size(10.dp))
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(onClick = {
                            viewModel.stopMusic()
                            viewModel.previousTrack()
                        }) {
                            Icon(
                                painterResource(id = R.drawable.ic_skip_back),
                                contentDescription = "Previous"
                            )
                        }
                        IconButton(
                            onClick = {
                                if (isMusicPlaying) viewModel.pauseMusic()
                                else viewModel.startMusic()
                            },
                            modifier = Modifier
                                .size(60.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = CircleShape
                                )
                        ) {
                            Icon(
                                if (isMusicPlaying) painterResource(id = R.drawable.ic_pause_btn) else painterResource(
                                    id = R.drawable.ic_play_btn
                                ),
                                contentDescription = if (isMusicPlaying) "Pause" else "Play"
                            )
                        }
                        IconButton(onClick = {
                            viewModel.stopMusic()
                            viewModel.nextTrack()
                        }) {
                            Icon(
                                painterResource(id = R.drawable.ic_skip_forward),
                                contentDescription = "Next"
                            )
                        }
                    }
                    Spacer(modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

fun formatTime(milliseconds: Int): String {
    val minutes = (milliseconds / 1000) / 60
    val seconds = (milliseconds / 1000) % 60
    return String.format("%02d:%02d", minutes, seconds)
}

//@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
//@Preview(showBackground = true)
//@Composable
//fun MusicPlayerScreenPreview() {
//    SukritiAssignmentTheme {
//        MusicPlayerScreen(
//            navController = NavController(LocalContext.current),
//            viewModel = MusicViewModel(
//                MusicRepositoryImpl(
//                    context = LocalContext.current, mediaPlayer = MediaPlayer(LocalContext.current)
//                )
//            )
//        )
//    }
//}

