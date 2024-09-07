package com.zaid.sukritiassignment.presentation.music_screens

import android.annotation.SuppressLint
import android.media.MediaPlayer
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.zaid.sukritiassignment.R
import com.zaid.sukritiassignment.presentation.view_model.MusicUiState
import com.zaid.sukritiassignment.ui.theme.SukritiAssignmentTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.MusicPlayerScreen(
    navController: NavController,
    uiState: MusicUiState,
    onShowSnackBar: suspend (message: String, actionLabel: String?, duration: SnackbarDuration) -> Boolean,
    onEvent: (MusicPlayerUiEvent) -> Unit,
    mediaPlayer: MediaPlayer?,
    animatedVisibilityScope: AnimatedVisibilityScope?
) {
    var seekPosition by remember { mutableIntStateOf(0) }
    var isUserSeeking by remember { mutableStateOf(false) }

    var currentRotation by remember {
        mutableFloatStateOf(0f)
    }

    val rotation = remember {
        Animatable(currentRotation)
    }

    LaunchedEffect(uiState.isMusicPlaying) {
        if (uiState.isMusicPlaying) {
            rotation.animateTo(
                targetValue = currentRotation + 360f, animationSpec = infiniteRepeatable(
                    animation = tween(3000, easing = LinearEasing), repeatMode = RepeatMode.Restart
                )
            ) {
                currentRotation = value
            }
        } else {
            if (currentRotation > 0f) {
                rotation.animateTo(
                    targetValue = currentRotation + 50, animationSpec = tween(
                        1250, easing = LinearOutSlowInEasing
                    )
                ) {
                    currentRotation = value
                }
            }
        }
    }


//    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
//    val infiniteTransition = rememberInfiniteTransition(label = "")
//
//    // Animate the offset from screenWidth to -screenWidth continuously
//    val offsetX by infiniteTransition.animateFloat(
//        initialValue = screenWidth.value,
//        targetValue = -screenWidth.value,
//        animationSpec = infiniteRepeatable(
//            animation = tween(durationMillis = 15000, easing = LinearEasing),
//            repeatMode = RepeatMode.Restart
//        ), label = ""
//    )

    LaunchedEffect(uiState) {
        if (uiState.snackBarMessage != null) {
            onShowSnackBar(uiState.snackBarMessage, null, SnackbarDuration.Short)
        }
    }

    LaunchedEffect(mediaPlayer) {
        mediaPlayer?.let {
            onEvent(MusicPlayerUiEvent.OnUpdateSeekBarPosition)
        }
    }

    // State to keep track of drag distance
    var dragDistance by remember { mutableStateOf(0f) }
    var screenHeight by remember { mutableStateOf(0f) }

    Column(
        modifier = Modifier
            .onGloballyPositioned { coordinates ->
                // Get screen height in pixels
                screenHeight = coordinates.size.height.toFloat()
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        // Check if dragDistance is 30% or more of the screen height
                        if (dragDistance >= screenHeight * 0.3f) {
                            navController.popBackStack()
                        }
                        // Reset drag distance
                        dragDistance = 0f
                    },
                    onDrag = { change, dragAmount ->
                        dragDistance += dragAmount.y
                        change.consume() // Consume the event
                    }
                )
            }
            .fillMaxSize()
            .sharedBounds(
                sharedContentState = rememberSharedContentState(key = "background"),
                animatedVisibilityScope = animatedVisibilityScope!!
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TopAppBar(
            title = {
                Text(
                    text = uiState.playingAudioFile?.name ?: "Unknown Song",
                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.sharedBounds(
                        sharedContentState = rememberSharedContentState(key = "name"),
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                )
            },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
        )

        uiState.playingAudioFile?.let { audioFile ->
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
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        modifier = Modifier
                            .sharedBounds(
                                sharedContentState = rememberSharedContentState(key = "image"),
                                animatedVisibilityScope = animatedVisibilityScope
                            )
                            .rotate(rotation.value)
                    ) {
                        AsyncImage(
                            model = null ?: R.drawable.ic_music_thumbnail,
                            contentDescription = null,
                            modifier = Modifier
                                .size(280.dp)
                                .clip(RoundedCornerShape(20.dp)),
                            contentScale = ContentScale.Crop
                        )
                        AsyncImage(
                            model = audioFile.albumArtUri,
                            contentDescription = null,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(120.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(modifier = Modifier.size(20.dp))
                    Text(
                        text = audioFile.name,
                        fontSize = MaterialTheme.typography.titleMedium.fontSize,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
//                        modifier = Modifier
//                            .offset(x = offsetX.dp)
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Slider(
                        value = if (isUserSeeking) seekPosition.toFloat() else uiState.playerSeekBarPosition.toFloat(),
                        onValueChange = { newPosition ->
                            seekPosition = newPosition.toInt()
                            isUserSeeking = true
                        },
                        valueRange = 0f..(audioFile.duration.toFloat()),
                        onValueChangeFinished = {
                            isUserSeeking = false
                            onEvent(MusicPlayerUiEvent.OnSeekToClick(seekPosition))
                        }
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = formatTime(mediaPlayer!!.currentPosition),
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
                            onEvent(MusicPlayerUiEvent.OnStopMusicClick)
                            onEvent(MusicPlayerUiEvent.OnPreviousTrackClick)
                        }) {
                            Icon(
                                painterResource(id = R.drawable.ic_skip_back),
                                contentDescription = "Previous"
                            )
                        }
                        IconButton(
                            onClick = {
                                if (uiState.isMusicPlaying) onEvent(MusicPlayerUiEvent.OnPauseMusicClick)
                                else onEvent(MusicPlayerUiEvent.OnStartMusicClick)
                            },
                            modifier = Modifier
                                .size(60.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = CircleShape
                                )
                        ) {
                            Icon(
                                if (uiState.isMusicPlaying) painterResource(id = R.drawable.ic_pause_btn) else painterResource(
                                    id = R.drawable.ic_play_btn
                                ),
                                contentDescription = if (uiState.isMusicPlaying) "Pause" else "Play",
                                tint = Color.White
                            )
                        }
                        IconButton(onClick = {
                            onEvent(MusicPlayerUiEvent.OnStopMusicClick)
                            onEvent(MusicPlayerUiEvent.OnNextTrackClick)
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

@SuppressLint("DefaultLocale")
fun formatTime(milliseconds: Int): String {
    val minutes = (milliseconds / 1000) / 60
    val seconds = (milliseconds / 1000) % 60
    return String.format("%02d:%02d", minutes, seconds)
}


@OptIn(ExperimentalSharedTransitionApi::class)
@Preview(showBackground = true)
@Composable
fun MusicPlayerScreenPreview() {
    SukritiAssignmentTheme {
        SharedTransitionLayout {
            MusicPlayerScreen(
                navController = NavController(LocalContext.current),
                onShowSnackBar = { _, _, _ -> false },
                onEvent = {},
                mediaPlayer = null,
                uiState = MusicUiState(),
                animatedVisibilityScope = null
            )
        }
    }
}

