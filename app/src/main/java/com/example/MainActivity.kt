package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Style
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.game.GameViewModel
import com.example.ui.screens.GameScreen
import com.example.ui.screens.LeaderboardScreen
import com.example.ui.theme.MyApplicationTheme

enum class ScreenTab {
    GAME,
    LEADERBOARD
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainApp()
            }
        }
    }
}

@Composable
fun MainApp(
    gameViewModel: GameViewModel = viewModel()
) {
    val gameState by gameViewModel.gameState.collectAsStateWithLifecycle()
    val scores by gameViewModel.scores.collectAsStateWithLifecycle()
    val selectedFilter by gameViewModel.leaderboardFilter.collectAsStateWithLifecycle()

    var currentTab by remember { mutableStateOf(ScreenTab.GAME) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("bottom_navigation_bar"),
                containerColor = Color(0xFF1E1B4B),
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = currentTab == ScreenTab.GAME,
                    onClick = { currentTab = ScreenTab.GAME },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == ScreenTab.GAME) Icons.Filled.Style else Icons.Outlined.Style,
                            contentDescription = "Play Game"
                        )
                    },
                    label = {
                        Text(
                            text = "Play",
                            fontWeight = if (currentTab == ScreenTab.GAME) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    modifier = Modifier.testTag("tab_game"),
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = Color(0xFF818CF8),
                        indicatorColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = Color.LightGray,
                        unselectedTextColor = Color.Gray
                    )
                )

                NavigationBarItem(
                    selected = currentTab == ScreenTab.LEADERBOARD,
                    onClick = { currentTab = ScreenTab.LEADERBOARD },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == ScreenTab.LEADERBOARD) Icons.Filled.EmojiEvents else Icons.Outlined.EmojiEvents,
                            contentDescription = "Leaderboard"
                        )
                    },
                    label = {
                        Text(
                            text = "Leaderboard",
                            fontWeight = if (currentTab == ScreenTab.LEADERBOARD) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    modifier = Modifier.testTag("tab_leaderboard"),
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = Color(0xFF818CF8),
                        indicatorColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = Color.LightGray,
                        unselectedTextColor = Color.Gray
                    )
                )
            }
        }
    ) { innerPadding ->
        Crossfade(
            targetState = currentTab,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            label = "tab_crossfade"
        ) { tab ->
            when (tab) {
                ScreenTab.GAME -> {
                    GameScreen(
                        gameState = gameState,
                        onCardClicked = { gameViewModel.onCardClicked(it) },
                        onDifficultySelected = { gameViewModel.startNewGame(difficulty = it) },
                        onThemeSelected = { gameViewModel.startNewGame(theme = it) },
                        onTogglePause = { gameViewModel.togglePause() },
                        onRestart = { gameViewModel.startNewGame() },
                        onUseHint = { gameViewModel.useHint() },
                        onToggleHaptics = { gameViewModel.toggleHaptics() },
                        onSaveScore = { name -> gameViewModel.saveScoreToLeaderboard(name) },
                        onViewLeaderboard = { currentTab = ScreenTab.LEADERBOARD }
                    )
                }

                ScreenTab.LEADERBOARD -> {
                    LeaderboardScreen(
                        scores = scores,
                        selectedFilter = selectedFilter,
                        onFilterChange = { gameViewModel.setLeaderboardFilter(it) },
                        onDeleteScore = { gameViewModel.deleteScore(it) },
                        onClearAll = { gameViewModel.clearAllScores() },
                        onPlayNow = { currentTab = ScreenTab.GAME }
                    )
                }
            }
        }
    }
}
