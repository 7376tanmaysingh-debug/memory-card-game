package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.MemoryCard
import kotlin.math.roundToInt

@Composable
fun CardFlipView(
    card: MemoryCard,
    isHintActive: Boolean,
    hapticsEnabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val isRevealed = card.isFaceUp || card.isMatched || isHintActive

    val rotation by animateFloatAsState(
        targetValue = if (isRevealed) 180f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "cardRotation_${card.id}"
    )

    // Shake animation for mismatched cards
    val shakeOffset = remember { Animatable(0f) }
    LaunchedEffect(card.isShaking) {
        if (card.isShaking) {
            if (hapticsEnabled) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            }
            // Rapid back-and-forth shake
            for (i in 0..2) {
                shakeOffset.animateTo(12f, tween(50))
                shakeOffset.animateTo(-12f, tween(50))
            }
            shakeOffset.animateTo(0f, tween(50))
        }
    }

    val density = LocalDensity.current.density

    Card(
        modifier = modifier
            .aspectRatio(0.78f)
            .offset { IntOffset(shakeOffset.value.roundToInt(), 0) }
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 14f * density
            }
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, color = Color.White),
                enabled = !card.isFaceUp && !card.isMatched && !isHintActive,
                onClick = {
                    if (hapticsEnabled) {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    }
                    onClick()
                }
            )
            .testTag("card_${card.id}"),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (card.isMatched) 2.dp else 6.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        if (rotation <= 90f) {
            // BACK OF CARD (Face down)
            CardBackView()
        } else {
            // FRONT OF CARD (Face up)
            // Flip back horizontally so content reads correctly
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { rotationY = 180f }
            ) {
                CardFrontView(card = card)
            }
        }
    }
}

@Composable
private fun CardBackView() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF312E81),
                        Color(0xFF1E1B4B),
                        Color(0xFF0F172A)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(300f, 500f)
                )
            )
            .drawBehind {
                // Subtle diamond lattice pattern
                val strokeColor = Color(0x22818CF8)
                val step = 20.dp.toPx()
                for (x in -size.width.toInt()..size.width.toInt() * 2 step step.toInt()) {
                    drawLine(
                        color = strokeColor,
                        start = Offset(x.toFloat(), 0f),
                        end = Offset(x + size.height, size.height),
                        strokeWidth = 1.2f
                    )
                    drawLine(
                        color = strokeColor,
                        start = Offset(x.toFloat(), size.height),
                        end = Offset(x + size.height, 0f),
                        strokeWidth = 1.2f
                    )
                }
            },
        contentAlignment = Alignment.Center
    ) {
        // Glowing Center Emblem
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0xFF6366F1), Color(0xFF4338CA))
                    )
                )
                .shadow(elevation = 4.dp, shape = RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "✦",
                color = Color(0xFFE0E7FF),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun CardFrontView(card: MemoryCard) {
    val borderColor = if (card.isMatched) {
        Color(0xFF10B981)
    } else {
        card.accentColor.copy(alpha = 0.7f)
    }

    val backgroundBrush = if (card.isMatched) {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFF064E3B).copy(alpha = 0.85f),
                Color(0xFF022C22).copy(alpha = 0.95f)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFF1E293B),
                Color(0xFF0F172A)
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
            .padding(2.dp)
            .clip(RoundedCornerShape(14.dp))
            .drawBehind {
                // Glow border
                drawRoundRect(
                    color = borderColor,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(
                        width = if (card.isMatched) 3.5.dp.toPx() else 2.dp.toPx()
                    ),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(14.dp.toPx())
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Main Emoji Symbol
            Text(
                text = card.symbol,
                fontSize = 34.sp,
                modifier = Modifier.padding(bottom = 2.dp)
            )

            // Name Label
            Text(
                text = card.name,
                style = MaterialTheme.typography.labelSmall,
                color = if (card.isMatched) Color(0xFF6EE7B7) else Color(0xFFCBD5E1),
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
        }

        // Matched indicator checkmark badge
        if (card.isMatched) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF10B981)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Matched",
                    tint = Color.White,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}
