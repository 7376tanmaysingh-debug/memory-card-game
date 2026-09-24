package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun HowToPlayDialog(
    hapticsEnabled: Boolean,
    onToggleHaptics: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("how_to_play_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B4B))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "How to Play 🃏",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                RuleItem(
                    emoji = "👆",
                    title = "Tap to Flip",
                    description = "Tap any face-down card to reveal its symbol. Tap a second card to find its match."
                )

                RuleItem(
                    emoji = "🔥",
                    title = "Combo Streaks",
                    description = "Consecutive successful matches build your combo multiplier (2x, 3x...) for massive bonus points!"
                )

                RuleItem(
                    emoji = "⏱️",
                    title = "Beat the Clock",
                    description = "Finish faster than the target time to earn hefty time bonuses for high scores."
                )

                RuleItem(
                    emoji = "💡",
                    title = "Hint Power-Up",
                    description = "Stuck? Tap the Hint button to flash all cards face up for 1.2 seconds (1 hint per game)."
                )

                // Haptics setting
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Haptic Vibration",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Vibrate on taps and matches",
                            color = Color.LightGray,
                            fontSize = 12.sp
                        )
                    }
                    Switch(
                        checked = hapticsEnabled,
                        onCheckedChange = { onToggleHaptics() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dismiss_rules_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Got It! Let's Play")
                }
            }
        }
    }
}

@Composable
private fun RuleItem(emoji: String, title: String, description: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(text = emoji, fontSize = 22.sp)
        Column {
            Text(
                text = title,
                color = Color(0xFFFBBF24),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                text = description,
                color = Color(0xFFCBD5E1),
                fontSize = 13.sp,
                lineHeight = 17.sp
            )
        }
    }
}
