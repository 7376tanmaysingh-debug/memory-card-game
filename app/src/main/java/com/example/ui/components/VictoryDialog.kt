package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.game.Difficulty
import com.example.game.GameState

@Composable
fun VictoryDialog(
    gameState: GameState,
    onSaveScore: (String) -> Unit,
    onPlayAgain: () -> Unit,
    onNextDifficulty: (Difficulty) -> Unit,
    onViewLeaderboard: () -> Unit,
    onDismiss: () -> Unit
) {
    var playerNameInput by remember { mutableStateOf(gameState.playerName) }
    var hasSaved by remember { mutableStateOf(gameState.lastSavedScoreId != null) }

    LaunchedEffect(gameState.lastSavedScoreId) {
        if (gameState.lastSavedScoreId != null) {
            hasSaved = true
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("victory_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1E1B4B)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Celebration Icon
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(Color(0xFFFBBF24), Color(0xFFD97706))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Victory",
                        tint = Color.White,
                        modifier = Modifier.size(38.dp)
                    )
                }

                Text(
                    text = "Puzzle Solved!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )

                // Star Rating
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 1..3) {
                        val isStarEarned = i <= gameState.ratingStars
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Star $i",
                            tint = if (isStarEarned) Color(0xFFFBBF24) else Color(0xFF475569),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                // Stats Summary Grid
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF0F172A).copy(alpha = 0.8f)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Final Score", color = Color.LightGray, style = MaterialTheme.typography.bodyMedium)
                            Text(
                                text = "${gameState.currentScore} pts",
                                color = Color(0xFFFBBF24),
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Time Elapsed", color = Color.LightGray, style = MaterialTheme.typography.bodyMedium)
                            Text(
                                text = formatTime(gameState.elapsedSeconds),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Total Moves", color = Color.LightGray, style = MaterialTheme.typography.bodyMedium)
                            Text(
                                text = "${gameState.moves} moves",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Accuracy", color = Color.LightGray, style = MaterialTheme.typography.bodyMedium)
                            Text(
                                text = "${gameState.accuracyPercent}%",
                                color = Color(0xFF34D399),
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        if (gameState.bestStreak > 1) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Max Combo Streak", color = Color.LightGray, style = MaterialTheme.typography.bodyMedium)
                                Text(
                                    text = "×${gameState.bestStreak} 🔥",
                                    color = Color(0xFFF97316),
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }

                // Name Input & Save to Leaderboard
                if (!hasSaved) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = playerNameInput,
                            onValueChange = { playerNameInput = it.take(20) },
                            label = { Text("Player Name") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("player_name_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF818CF8),
                                unfocusedBorderColor = Color(0xFF475569),
                                focusedLabelColor = Color(0xFF818CF8),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )

                        Button(
                            onClick = {
                                onSaveScore(playerNameInput)
                                hasSaved = true
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("save_score_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF6366F1)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Save to Leaderboard")
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF064E3B).copy(alpha = 0.7f))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Saved",
                            tint = Color(0xFF34D399),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Score Saved to Leaderboard!",
                            color = Color(0xFF6EE7B7),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }
                }

                // Action Buttons: Play Again & View Leaderboard
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onViewLeaderboard,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("dialog_leaderboard_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Leaderboard,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Scores", fontSize = 13.sp)
                    }

                    Button(
                        onClick = onPlayAgain,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("play_again_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Play Again", fontSize = 13.sp)
                    }
                }

                // If not highest difficulty, option to play next level
                val nextDiff = when (gameState.difficulty) {
                    Difficulty.EASY -> Difficulty.MEDIUM
                    Difficulty.MEDIUM -> Difficulty.HARD
                    Difficulty.HARD -> Difficulty.EXPERT
                    Difficulty.EXPERT -> null
                }

                if (nextDiff != null) {
                    Button(
                        onClick = { onNextDifficulty(nextDiff) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("next_difficulty_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF10B981)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Next Level: ${nextDiff.displayName} (${nextDiff.pairs} pairs) 🚀",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
