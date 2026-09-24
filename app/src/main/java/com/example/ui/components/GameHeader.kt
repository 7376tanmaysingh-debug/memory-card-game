package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.CardTheme
import com.example.game.Difficulty
import com.example.game.GameState

@Composable
fun GameHeader(
    gameState: GameState,
    onDifficultySelected: (Difficulty) -> Unit,
    onThemeSelected: (CardTheme) -> Unit,
    onTogglePause: () -> Unit,
    onRestart: () -> Unit,
    onUseHint: () -> Unit,
    onShowHowToPlay: () -> Unit,
    modifier: Modifier = Modifier
) {
    var themeMenuExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // TOP ROW: App Title, Theme selector & Settings/Rules
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "🧠 Memory Match",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                // Theme Dropdown Pill
                Box {
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { themeMenuExpanded = true }
                            .testTag("theme_selector_button"),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
                        tonalElevation = 2.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(text = gameState.theme.icon, fontSize = 14.sp)
                            Text(
                                text = gameState.theme.displayName,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(text = "▼", fontSize = 10.sp, color = Color.Gray)
                        }
                    }

                    DropdownMenu(
                        expanded = themeMenuExpanded,
                        onDismissRequest = { themeMenuExpanded = false }
                    ) {
                        CardTheme.values().forEach { theme ->
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(text = theme.icon, fontSize = 18.sp)
                                        Text(text = theme.displayName)
                                    }
                                },
                                onClick = {
                                    themeMenuExpanded = false
                                    onThemeSelected(theme)
                                }
                            )
                        }
                    }
                }
            }

            // Quick rules / how to play info button
            FilledTonalButton(
                onClick = onShowHowToPlay,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("rules_button")
            ) {
                Text(text = "Rules ℹ️", style = MaterialTheme.typography.labelSmall)
            }
        }

        // DIFFICULTY SELECTOR CHIPS
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Difficulty.values().forEach { diff ->
                val selected = gameState.difficulty == diff
                FilterChip(
                    selected = selected,
                    onClick = { onDifficultySelected(diff) },
                    label = {
                        Text(
                            text = "${diff.icon} ${diff.displayName}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("difficulty_${diff.name.lowercase()}"),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }

        // STATS BAR: Timer, Moves, Progress, Score & Combo
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.85f),
            tonalElevation = 3.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Timer
                StatItem(
                    icon = Icons.Default.Timer,
                    label = "Time",
                    value = formatTime(gameState.elapsedSeconds),
                    tint = Color(0xFF38BDF8)
                )

                // Moves
                StatItem(
                    icon = Icons.Default.TouchApp,
                    label = "Moves",
                    value = "${gameState.moves}",
                    tint = Color(0xFFA78BFA)
                )

                // Pairs / Matches Progress
                StatItem(
                    icon = Icons.Default.Whatshot,
                    label = "Pairs",
                    value = "${gameState.matchesFound}/${gameState.totalPairs}",
                    tint = Color(0xFF34D399)
                )

                // Score & Combo
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Score: ${gameState.currentScore}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFBBF24)
                    )

                    AnimatedVisibility(
                        visible = gameState.streak > 1,
                        enter = fadeIn() + scaleIn(),
                        exit = fadeOut() + scaleOut()
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFFEF4444), Color(0xFFF97316))
                                    )
                                )
                                .padding(horizontal = 6.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "COMBO ×${gameState.streak}! 🔥",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }
        }

        // CONTROL BUTTONS ROW (Hint, Pause, Restart)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Hint Power-Up Button
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(enabled = gameState.hintsLeft > 0 && !gameState.isHintActive) {
                        onUseHint()
                    }
                    .testTag("hint_button"),
                color = if (gameState.hintsLeft > 0) Color(0xFF3B82F6).copy(alpha = 0.2f) else Color.DarkGray.copy(alpha = 0.2f),
                border = BorderStroke(
                    1.dp,
                    if (gameState.hintsLeft > 0) Color(0xFF60A5FA) else Color.Transparent
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = "Hint",
                        tint = if (gameState.hintsLeft > 0) Color(0xFFFDE047) else Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Hint (${gameState.hintsLeft})",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (gameState.hintsLeft > 0) Color.White else Color.Gray
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                // Pause / Resume Button
                IconButton(
                    onClick = onTogglePause,
                    enabled = gameState.isGameActive && !gameState.isVictory,
                    modifier = Modifier.testTag("pause_button")
                ) {
                    Icon(
                        imageVector = if (gameState.isGamePaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                        contentDescription = if (gameState.isGamePaused) "Resume" else "Pause",
                        tint = if (gameState.isGameActive) MaterialTheme.colorScheme.primary else Color.Gray
                    )
                }

                // Restart Game Button
                IconButton(
                    onClick = onRestart,
                    modifier = Modifier.testTag("restart_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Restart Game",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun StatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    tint: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(tint.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = tint,
                modifier = Modifier.size(16.dp)
            )
        }
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Color.LightGray
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

fun formatTime(seconds: Long): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return String.format("%02d:%02d", mins, secs)
}
