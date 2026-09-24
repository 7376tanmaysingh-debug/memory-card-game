package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scores")
data class ScoreRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val playerName: String,
    val score: Int,
    val timeSeconds: Long,
    val moves: Int,
    val difficulty: String,
    val themeName: String,
    val accuracyPercent: Int,
    val timestamp: Long = System.currentTimeMillis()
)
