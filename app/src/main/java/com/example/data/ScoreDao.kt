package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ScoreDao {
    @Query("SELECT * FROM scores ORDER BY score DESC, timeSeconds ASC, timestamp DESC")
    fun getAllScores(): Flow<List<ScoreRecord>>

    @Query("SELECT * FROM scores WHERE difficulty = :difficulty ORDER BY score DESC, timeSeconds ASC, timestamp DESC")
    fun getScoresByDifficulty(difficulty: String): Flow<List<ScoreRecord>>

    @Query("SELECT * FROM scores ORDER BY score DESC, timeSeconds ASC LIMIT :limit")
    fun getTopScores(limit: Int = 10): Flow<List<ScoreRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScore(score: ScoreRecord): Long

    @Query("DELETE FROM scores WHERE id = :id")
    suspend fun deleteScore(id: Int)

    @Query("DELETE FROM scores")
    suspend fun clearAllScores()
}
