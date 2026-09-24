package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ScoreRecord
import com.example.ui.components.formatTime
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LeaderboardScreen(
    scores: List<ScoreRecord>,
    selectedFilter: String,
    onFilterChange: (String) -> Unit,
    onDeleteScore: (Int) -> Unit,
    onClearAll: () -> Unit,
    onPlayNow: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showClearDialog by remember { mutableStateOf(false) }

    val filterOptions = listOf("All", "Easy", "Medium", "Hard", "Expert")

    val highestScore = scores.maxOfOrNull { it.score } ?: 0
    val fastestClear = scores.minOfOrNull { it.timeSeconds } ?: 0L
    val avgAccuracy = if (scores.isNotEmpty()) {
        scores.map { it.accuracyPercent }.average().toInt()
    } else 0

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
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            // Screen Title & Clear action
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFBBF24).copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "Leaderboard",
                            tint = Color(0xFFFBBF24),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Text(
                        text = "Leaderboard",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                if (scores.isNotEmpty()) {
                    IconButton(
                        onClick = { showClearDialog = true },
                        modifier = Modifier.testTag("clear_leaderboard_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = "Clear Leaderboard",
                            tint = Color(0xFFF87171)
                        )
                    }
                }
            }

            // Quick Stats summary strip
            if (scores.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SummaryMiniCard(
                        title = "Best Score",
                        value = "$highestScore",
                        color = Color(0xFFFBBF24),
                        modifier = Modifier.weight(1f)
                    )
                    SummaryMiniCard(
                        title = "Fastest Win",
                        value = if (fastestClear > 0) formatTime(fastestClear) else "--:--",
                        color = Color(0xFF38BDF8),
                        modifier = Modifier.weight(1f)
                    )
                    SummaryMiniCard(
                        title = "Avg Accuracy",
                        value = "$avgAccuracy%",
                        color = Color(0xFF34D399),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Difficulty Filter Chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filterOptions) { filter ->
                    val isSelected = selectedFilter == filter
                    FilterChip(
                        selected = isSelected,
                        onClick = { onFilterChange(filter) },
                        label = { Text(filter) },
                        modifier = Modifier.testTag("filter_$filter"),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }

            // Scores List or Empty State
            if (scores.isEmpty()) {
                EmptyLeaderboardView(
                    filter = selectedFilter,
                    onPlayNow = onPlayNow
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(
                        items = scores,
                        key = { _, item -> item.id }
                    ) { index, record ->
                        ScoreCardItem(
                            rank = index + 1,
                            record = record,
                            onDelete = { onDeleteScore(record.id) }
                        )
                    }
                }
            }
        }

        // Confirmation Dialog for clearing leaderboard
        if (showClearDialog) {
            AlertDialog(
                onDismissRequest = { showClearDialog = false },
                title = { Text("Clear All High Scores?") },
                text = { Text("Are you sure you want to delete all saved scores? This action cannot be undone.") },
                confirmButton = {
                    Button(
                        onClick = {
                            onClearAll()
                            showClearDialog = false
                        }
                    ) {
                        Text("Clear All")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showClearDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
private fun SummaryMiniCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF1E293B).copy(alpha = 0.8f)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = Color.LightGray
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
private fun ScoreCardItem(
    rank: Int,
    record: ScoreRecord,
    onDelete: () -> Unit
) {
    val rankBadgeColor = when (rank) {
        1 -> Color(0xFFFBBF24) // Gold
        2 -> Color(0xFFE2E8F0) // Silver
        3 -> Color(0xFFCD7F32) // Bronze
        else -> Color(0xFF64748B)
    }

    val rankIcon = when (rank) {
        1 -> "🥇"
        2 -> "🥈"
        3 -> "🥉"
        else -> "#$rank"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("score_item_${record.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (rank <= 3) Color(0xFF1E1B4B) else Color(0xFF1E293B).copy(alpha = 0.7f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (rank <= 3) 4.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Rank and Player details
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Rank Emblem
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(rankBadgeColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = rankIcon,
                        fontSize = if (rank <= 3) 18.sp else 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = rankBadgeColor
                    )
                }

                Column {
                    Text(
                        text = record.playerName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Difficulty Pill
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = record.difficulty,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        // Theme Pill
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF8B5CF6).copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = record.themeName,
                                color = Color(0xFFA78BFA),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        // Date
                        Text(
                            text = formatScoreDate(record.timestamp),
                            color = Color.Gray,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            // Score and Stats
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${record.score} pts",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFFBBF24)
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "⏱️ ${formatTime(record.timeSeconds)}",
                            color = Color.LightGray,
                            fontSize = 11.sp
                        )
                        Text(
                            text = "🎯 ${record.moves}m",
                            color = Color.LightGray,
                            fontSize = 11.sp
                        )
                    }
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete score",
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyLeaderboardView(
    filter: String,
    onPlayNow: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Color(0xFF312E81).copy(alpha = 0.6f)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "🏆", fontSize = 42.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (filter == "All") "No Scores Recorded Yet" else "No $filter Scores Yet",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Match all card pairs to set a brand new high score on the leaderboard!",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.LightGray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onPlayNow,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.testTag("empty_play_now_button")
        ) {
            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
            Spacer(modifier = Modifier.width(6.dp))
            Text("Play Game Now")
        }
    }
}

private fun formatScoreDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM d, HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
