package com.example.game

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.ScoreRecord
import com.example.data.ScoreRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.max

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ScoreRepository = ScoreRepository(
        AppDatabase.getInstance(application).scoreDao()
    )

    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private val _leaderboardFilter = MutableStateFlow<String>("All")
    val leaderboardFilter: StateFlow<String> = _leaderboardFilter.asStateFlow()

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val scores: StateFlow<List<ScoreRecord>> = _leaderboardFilter
        .flatMapLatest { filter ->
            if (filter == "All") {
                repository.allScores
            } else {
                repository.getScoresByDifficulty(filter)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private var timerJob: Job? = null

    init {
        startNewGame(Difficulty.MEDIUM, CardTheme.CREATURES)
    }

    fun startNewGame(
        difficulty: Difficulty = _gameState.value.difficulty,
        theme: CardTheme = _gameState.value.theme
    ) {
        timerJob?.cancel()

        val pairsNeeded = difficulty.pairs
        val themeItems = theme.items.shuffled().take(pairsNeeded)

        val cardsList = mutableListOf<MemoryCard>()
        var cardIndex = 0

        themeItems.forEachIndexed { pairIndex, item ->
            // Add first card of pair
            cardsList.add(
                MemoryCard(
                    id = cardIndex++,
                    pairId = pairIndex,
                    symbol = item.symbol,
                    name = item.name,
                    accentColor = item.accentColor,
                    isFaceUp = false,
                    isMatched = false
                )
            )
            // Add second card of pair
            cardsList.add(
                MemoryCard(
                    id = cardIndex++,
                    pairId = pairIndex,
                    symbol = item.symbol,
                    name = item.name,
                    accentColor = item.accentColor,
                    isFaceUp = false,
                    isMatched = false
                )
            )
        }

        cardsList.shuffle()

        _gameState.value = GameState(
            cards = cardsList,
            difficulty = difficulty,
            theme = theme,
            moves = 0,
            matchesFound = 0,
            elapsedSeconds = 0L,
            isGameActive = false,
            isGamePaused = false,
            isVictory = false,
            currentScore = 0,
            streak = 0,
            bestStreak = 0,
            firstSelectedCardId = null,
            secondSelectedCardId = null,
            isEvaluating = false,
            hintsLeft = 1,
            isHintActive = false,
            playerName = _gameState.value.playerName,
            ratingStars = 0,
            lastSavedScoreId = null,
            soundEnabled = _gameState.value.soundEnabled,
            hapticsEnabled = _gameState.value.hapticsEnabled
        )
    }

    fun onCardClicked(cardId: Int) {
        val state = _gameState.value
        if (state.isEvaluating || state.isGamePaused || state.isVictory || state.isHintActive) {
            return
        }

        val clickedCard = state.cards.firstOrNull { it.id == cardId } ?: return
        if (clickedCard.isFaceUp || clickedCard.isMatched) {
            return
        }

        // Start timer on first card tap
        if (!state.isGameActive) {
            _gameState.update { it.copy(isGameActive = true) }
            startTimer()
        }

        val firstId = state.firstSelectedCardId
        if (firstId == null) {
            // First card flip
            _gameState.update { current ->
                current.copy(
                    cards = current.cards.map {
                        if (it.id == cardId) it.copy(isFaceUp = true) else it
                    },
                    firstSelectedCardId = cardId
                )
            }
        } else if (state.secondSelectedCardId == null && cardId != firstId) {
            // Second card flip
            val newMoves = state.moves + 1
            _gameState.update { current ->
                current.copy(
                    cards = current.cards.map {
                        if (it.id == cardId) it.copy(isFaceUp = true) else it
                    },
                    secondSelectedCardId = cardId,
                    moves = newMoves,
                    isEvaluating = true
                )
            }

            evaluateMatch(firstId, cardId)
        }
    }

    private fun evaluateMatch(firstId: Int, secondId: Int) {
        viewModelScope.launch {
            val firstCard = _gameState.value.cards.first { it.id == firstId }
            val secondCard = _gameState.value.cards.first { it.id == secondId }

            if (firstCard.pairId == secondCard.pairId) {
                // Match found!
                delay(280) // brief pause for flip to settle
                val newMatches = _gameState.value.matchesFound + 1
                val newStreak = _gameState.value.streak + 1
                val bestStreak = max(_gameState.value.bestStreak, newStreak)
                val comboBonus = (newStreak - 1) * 60
                val matchPoints = 120 + comboBonus
                val newScore = _gameState.value.currentScore + matchPoints

                val isWon = newMatches >= _gameState.value.totalPairs

                _gameState.update { current ->
                    current.copy(
                        cards = current.cards.map {
                            if (it.id == firstId || it.id == secondId) {
                                it.copy(isMatched = true, isFaceUp = true)
                            } else it
                        },
                        matchesFound = newMatches,
                        streak = newStreak,
                        bestStreak = bestStreak,
                        currentScore = newScore,
                        firstSelectedCardId = null,
                        secondSelectedCardId = null,
                        isEvaluating = false
                    )
                }

                if (isWon) {
                    onGameWon()
                }
            } else {
                // Not a match
                delay(400)
                // Shake feedback
                _gameState.update { current ->
                    current.copy(
                        cards = current.cards.map {
                            if (it.id == firstId || it.id == secondId) {
                                it.copy(isShaking = true)
                            } else it
                        }
                    )
                }
                delay(650)
                // Flip back down
                _gameState.update { current ->
                    current.copy(
                        cards = current.cards.map {
                            if (it.id == firstId || it.id == secondId) {
                                it.copy(isFaceUp = false, isShaking = false)
                            } else it
                        },
                        streak = 0,
                        firstSelectedCardId = null,
                        secondSelectedCardId = null,
                        isEvaluating = false
                    )
                }
            }
        }
    }

    private fun onGameWon() {
        timerJob?.cancel()
        val state = _gameState.value
        val elapsed = state.elapsedSeconds
        val target = state.difficulty.targetSeconds
        val pairs = state.totalPairs

        // Calculate time bonus
        val timeBonus = if (elapsed < target) {
            ((target - elapsed) * 20).toInt()
        } else 0

        // Calculate moves bonus
        val minPossibleMoves = pairs
        val moveEfficiency = (minPossibleMoves.toFloat() / state.moves.toFloat()).coerceIn(0f, 1f)
        val moveBonus = (moveEfficiency * 300).toInt()

        val finalScore = state.currentScore + state.difficulty.basePoints + timeBonus + moveBonus

        // Star rating
        val stars = when {
            state.moves <= pairs * 1.5 && elapsed <= target -> 3
            state.moves <= pairs * 2.2 -> 2
            else -> 1
        }

        _gameState.update { current ->
            current.copy(
                isVictory = true,
                currentScore = finalScore,
                ratingStars = stars
            )
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                if (!_gameState.value.isGamePaused && !_gameState.value.isVictory) {
                    _gameState.update { it.copy(elapsedSeconds = it.elapsedSeconds + 1) }
                }
            }
        }
    }

    fun togglePause() {
        val current = _gameState.value
        if (!current.isGameActive || current.isVictory) return
        _gameState.update { it.copy(isGamePaused = !it.isGamePaused) }
    }

    fun useHint() {
        val state = _gameState.value
        if (state.hintsLeft <= 0 || state.isHintActive || state.isEvaluating || state.isVictory) return

        viewModelScope.launch {
            _gameState.update { current ->
                current.copy(
                    hintsLeft = current.hintsLeft - 1,
                    isHintActive = true,
                    currentScore = max(0, current.currentScore - 50)
                )
            }
            delay(1200)
            _gameState.update { current ->
                current.copy(isHintActive = false)
            }
        }
    }

    fun updatePlayerName(name: String) {
        _gameState.update { it.copy(playerName = name.trim().take(20)) }
    }

    fun toggleSound() {
        _gameState.update { it.copy(soundEnabled = !it.soundEnabled) }
    }

    fun toggleHaptics() {
        _gameState.update { it.copy(hapticsEnabled = !it.hapticsEnabled) }
    }

    fun setLeaderboardFilter(filter: String) {
        _leaderboardFilter.value = filter
    }

    fun saveScoreToLeaderboard(customPlayerName: String? = null) {
        val state = _gameState.value
        if (!state.isVictory || state.lastSavedScoreId != null) return

        val name = customPlayerName?.trim()?.ifEmpty { null }
            ?: state.playerName.ifEmpty { "Champion" }

        viewModelScope.launch {
            val record = ScoreRecord(
                playerName = name,
                score = state.currentScore,
                timeSeconds = state.elapsedSeconds,
                moves = state.moves,
                difficulty = state.difficulty.displayName,
                themeName = state.theme.displayName,
                accuracyPercent = state.accuracyPercent,
                timestamp = System.currentTimeMillis()
            )
            val insertedId = repository.insertScore(record)
            _gameState.update { it.copy(lastSavedScoreId = insertedId, playerName = name) }
        }
    }

    fun deleteScore(id: Int) {
        viewModelScope.launch {
            repository.deleteScore(id)
        }
    }

    fun clearAllScores() {
        viewModelScope.launch {
            repository.clearAllScores()
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
