package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.example.ui.components.CinematicBottomBar
import com.example.ui.components.CinematicTopBar
import com.example.ui.components.FloatingAudioBar
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.CinematicViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: CinematicViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    CinematicAppRoot(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun CinematicAppRoot(viewModel: CinematicViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val playbackState by viewModel.playbackState.collectAsState()

    // Handle back button for secondary screens
    if (currentScreen != AppScreen.HOME) {
        BackHandler {
            viewModel.navigateTo(AppScreen.HOME)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CinematicTopBar(
                currentScreen = currentScreen,
                onBackToHome = { viewModel.navigateTo(AppScreen.HOME) }
            )
        },
        bottomBar = {
            Column {
                FloatingAudioBar(
                    playbackState = playbackState,
                    onPlayPause = {
                        if (playbackState.isPlaying) {
                            viewModel.audioPlayer.pause()
                        } else {
                            viewModel.audioPlayer.resume()
                        }
                    },
                    onStop = { viewModel.audioPlayer.stop() }
                )
                CinematicBottomBar(
                    currentScreen = currentScreen,
                    onNavigate = { screen -> viewModel.navigateTo(screen) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (currentScreen) {
                AppScreen.HOME -> HomeScreen(
                    viewModel = viewModel,
                    onNavigate = { screen -> viewModel.navigateTo(screen) },
                    onSelectScene = { scene -> viewModel.selectScene(scene) }
                )
                AppScreen.SCRIPT -> ScriptReaderScreen(viewModel = viewModel)
                AppScreen.TTS_STUDIO -> TtsStudioScreen(viewModel = viewModel)
                AppScreen.MUSIC_STUDIO -> MusicStudioScreen(viewModel = viewModel)
                AppScreen.VISUAL_STUDIO -> VisualStudioScreen(viewModel = viewModel)
                AppScreen.DIRECTOR_NOTES -> DirectorNotesScreen(viewModel = viewModel)
            }
        }
    }
}
