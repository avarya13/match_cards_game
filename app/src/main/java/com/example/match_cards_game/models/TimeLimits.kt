package com.example.match_cards_game.models

data class TimeLimits(
    val easyLevelTime: Long = 30000, // 30 seconds
    val mediumLevelTime: Long = 60000, // 60 seconds
    val hardLevelTime: Long = 120000, // 120 seconds
    val veryHardLevelTime: Long = 150000 // 120 seconds
)