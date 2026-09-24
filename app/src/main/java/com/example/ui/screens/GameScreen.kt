package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.game.CardTheme
import com.example.game.Difficulty
import com.example.game.GameState
import com.example.ui.components.CardFlipView
import com.example.ui.components.GameHeader
import com.example.ui.components.HowToPlayDialog
import com.example.ui.components.PauseOverlay
import com.example.ui.components.VictoryDialog

@Composable
fun GameScreen(
    gameState: GameState,
    onCardClicked: (Int) -> Unit,
    onDifficultySelected: (Difficulty) -> Unit,
    onThemeSelected: (CardTheme) -> Unit,
    onTogglePause: () -> Unit,
    onRestart: () -> Unit,
    onUseHint: () -> Unit,
    onToggleHaptics: () -> Unit,
    onSaveScore: (String) -> Unit,
    onViewLeaderboard: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showHowToPlay by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F172A),
                        Color(0xFF1E1B4B),
                        Color(0xFF0F172A)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header with stats, difficulty, controls
            GameHeader(
                gameState = gameState,
                onDifficultySelected = onDifficultySelected,
                onThemeSelected = onThemeSelected,
                onTogglePause = onTogglePause,
                onRestart = onRestart,
                onUseHint = onUseHint,
                onShowHowToPlay = { showHowToPlay = true }
            )

            // The Card Grid
            val cardSpacing = if (gameState.difficulty.cols > 4) 4.dp else 6.dp
            LazyVerticalGrid(
                columns = GridCells.Fixed(gameState.difficulty.cols),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                contentPadding = PaddingValues(vertical = 6.dp, horizontal = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(cardSpacing),
                verticalArrangement = Arrangement.spacedBy(cardSpacing)
            ) {
                items(
                    items = gameState.cards,
                    key = { it.id }
                ) { card ->
                    CardFlipView(
                        card = card,
                        isHintActive = gameState.isHintActive,
                        hapticsEnabled = gameState.hapticsEnabled,
                        onClick = { onCardClicked(card.id) }
                    )
                }
            }
        }

        // Pause state overlay
        if (gameState.isGamePaused) {
            PauseOverlay(
                onResume = onTogglePause,
                onRestart = onRestart
            )
        }

        // Victory celebration dialog
        if (gameState.isVictory) {
            VictoryDialog(
                gameState = gameState,
                onSaveScore = onSaveScore,
                onPlayAgain = onRestart,
                onNextDifficulty = onDifficultySelected,
                onViewLeaderboard = onViewLeaderboard,
                onDismiss = onRestart
            )
        }

        // How to play / rules dialog
        if (showHowToPlay) {
            HowToPlayDialog(
                hapticsEnabled = gameState.hapticsEnabled,
                onToggleHaptics = onToggleHaptics,
                onDismiss = { showHowToPlay = false }
            )
        }
    }
}
