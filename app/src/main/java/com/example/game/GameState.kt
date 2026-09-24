package com.example.game

data class GameState(
    val cards: List<MemoryCard> = emptyList(),
    val difficulty: Difficulty = Difficulty.MEDIUM,
    val theme: CardTheme = CardTheme.CREATURES,
    val moves: Int = 0,
    val matchesFound: Int = 0,
    val elapsedSeconds: Long = 0L,
    val isGameActive: Boolean = false,
    val isGamePaused: Boolean = false,
    val isVictory: Boolean = false,
    val currentScore: Int = 0,
    val streak: Int = 0,
    val bestStreak: Int = 0,
    val firstSelectedCardId: Int? = null,
    val secondSelectedCardId: Int? = null,
    val isEvaluating: Boolean = false,
    val hintsLeft: Int = 1,
    val isHintActive: Boolean = false,
    val playerName: String = "Player 1",
    val ratingStars: Int = 0,
    val lastSavedScoreId: Long? = null,
    val soundEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true
) {
    val totalPairs: Int get() = difficulty.pairs
    val isCompleted: Boolean get() = matchesFound >= totalPairs && totalPairs > 0
    val accuracyPercent: Int
        get() = if (moves == 0) 100 else {
            val ratio = (matchesFound.toFloat() / moves.toFloat()) * 100f
            ratio.toInt().coerceIn(0, 100)
        }
}
