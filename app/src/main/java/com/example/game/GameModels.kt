package com.example.game

import androidx.compose.ui.graphics.Color

enum class Difficulty(
    val displayName: String,
    val rows: Int,
    val cols: Int,
    val pairs: Int,
    val targetSeconds: Long,
    val basePoints: Int,
    val icon: String
) {
    EASY("Easy", 4, 3, 6, 35, 600, "🌱"),
    MEDIUM("Medium", 4, 4, 8, 55, 1000, "⚡"),
    HARD("Hard", 5, 4, 10, 80, 1500, "🔥"),
    EXPERT("Expert", 6, 4, 12, 110, 2200, "👑")
}

data class ThemeItem(
    val symbol: String,
    val name: String,
    val accentColor: Color
)

enum class CardTheme(
    val displayName: String,
    val icon: String,
    val primaryColor: Color,
    val secondaryColor: Color,
    val items: List<ThemeItem>
) {
    CREATURES(
        displayName = "Creatures",
        icon = "🦄",
        primaryColor = Color(0xFF8B5CF6),
        secondaryColor = Color(0xFFA78BFA),
        items = listOf(
            ThemeItem("🦄", "Unicorn", Color(0xFFEC4899)),
            ThemeItem("🐉", "Dragon", Color(0xFFEF4444)),
            ThemeItem("🦊", "Fox", Color(0xFFF97316)),
            ThemeItem("🦁", "Lion", Color(0xFFFBBF24)),
            ThemeItem("🐼", "Panda", Color(0xFF10B981)),
            ThemeItem("🦉", "Owl", Color(0xFF8B5CF6)),
            ThemeItem("🐙", "Octopus", Color(0xFF06B6D4)),
            ThemeItem("🦋", "Butterfly", Color(0xFF3B82F6)),
            ThemeItem("🐬", "Dolphin", Color(0xFF0EA5E9)),
            ThemeItem("🦖", "T-Rex", Color(0xFF84CC16)),
            ThemeItem("🦚", "Peacock", Color(0xFF14B8A6)),
            ThemeItem("🐝", "Bee", Color(0xFFEAB308))
        )
    ),
    COSMIC(
        displayName = "Cosmic",
        icon = "🚀",
        primaryColor = Color(0xFF6366F1),
        secondaryColor = Color(0xFF818CF8),
        items = listOf(
            ThemeItem("🚀", "Rocket", Color(0xFFEF4444)),
            ThemeItem("🪐", "Saturn", Color(0xFFF59E0B)),
            ThemeItem("🌟", "Star", Color(0xFFFBBF24)),
            ThemeItem("🛸", "UFO", Color(0xFF10B981)),
            ThemeItem("🌌", "Galaxy", Color(0xFF8B5CF6)),
            ThemeItem("☄️", "Comet", Color(0xFFEC4899)),
            ThemeItem("🛰️", "Satellite", Color(0xFF06B6D4)),
            ThemeItem("🌕", "Moon", Color(0xFFFDE047)),
            ThemeItem("🔭", "Telescope", Color(0xFF6366F1)),
            ThemeItem("☀️", "Sun", Color(0xFFF97316)),
            ThemeItem("👾", "Alien", Color(0xFFA855F7)),
            ThemeItem("🌍", "Earth", Color(0xFF3B82F6))
        )
    ),
    FOOD(
        displayName = "Foodie",
        icon = "🍕",
        primaryColor = Color(0xFFF97316),
        secondaryColor = Color(0xFFFB923C),
        items = listOf(
            ThemeItem("🍕", "Pizza", Color(0xFFEF4444)),
            ThemeItem("🍣", "Sushi", Color(0xFFF43F5E)),
            ThemeItem("🥑", "Avocado", Color(0xFF10B981)),
            ThemeItem("🍔", "Burger", Color(0xFFD97706)),
            ThemeItem("🍩", "Donut", Color(0xFFEC4899)),
            ThemeItem("🍓", "Strawberry", Color(0xFFE11D48)),
            ThemeItem("🍦", "Ice Cream", Color(0xFF06B6D4)),
            ThemeItem("🌮", "Taco", Color(0xFFF59E0B)),
            ThemeItem("🥐", "Croissant", Color(0xFFCA8A04)),
            ThemeItem("🍇", "Grapes", Color(0xFF8B5CF6)),
            ThemeItem("🥞", "Pancakes", Color(0xFFD97706)),
            ThemeItem("🍉", "Watermelon", Color(0xFF22C55E))
        )
    ),
    TECH(
        displayName = "Tech & Arcade",
        icon = "🕹️",
        primaryColor = Color(0xFF06B6D4),
        secondaryColor = Color(0xFF22D3EE),
        items = listOf(
            ThemeItem("💻", "Laptop", Color(0xFF3B82F6)),
            ThemeItem("🎮", "Gamepad", Color(0xFF8B5CF6)),
            ThemeItem("🎧", "Headphones", Color(0xFFEC4899)),
            ThemeItem("📱", "Smartphone", Color(0xFF10B981)),
            ThemeItem("🕹️", "Arcade", Color(0xFFF59E0B)),
            ThemeItem("🤖", "Robot", Color(0xFF06B6D4)),
            ThemeItem("⌚", "Smartwatch", Color(0xFF6366F1)),
            ThemeItem("📸", "Camera", Color(0xFFEF4444)),
            ThemeItem("🔋", "Battery", Color(0xFF22C55E)),
            ThemeItem("📡", "Radar", Color(0xFF0EA5E9)),
            ThemeItem("🎙️", "Mic", Color(0xFFA855F7)),
            ThemeItem("💡", "Idea", Color(0xFFFBBF24))
        )
    )
}

data class MemoryCard(
    val id: Int,
    val pairId: Int,
    val symbol: String,
    val name: String,
    val accentColor: Color,
    val isFaceUp: Boolean = false,
    val isMatched: Boolean = false,
    val isShaking: Boolean = false
)
