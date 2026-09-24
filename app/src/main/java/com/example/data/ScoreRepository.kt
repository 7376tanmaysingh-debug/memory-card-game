package com.example.data

import kotlinx.coroutines.flow.Flow

class ScoreRepository(private val scoreDao: ScoreDao) {
    val allScores: Flow<List<ScoreRecord>> = scoreDao.getAllScores()

    fun getScoresByDifficulty(difficulty: String): Flow<List<ScoreRecord>> {
        return scoreDao.getScoresByDifficulty(difficulty)
    }

    fun getTopScores(limit: Int = 10): Flow<List<ScoreRecord>> {
        return scoreDao.getTopScores(limit)
    }

    suspend fun insertScore(score: ScoreRecord): Long {
        return scoreDao.insertScore(score)
    }

    suspend fun deleteScore(id: Int) {
        scoreDao.deleteScore(id)
    }

    suspend fun clearAllScores() {
        scoreDao.clearAllScores()
    }
}
