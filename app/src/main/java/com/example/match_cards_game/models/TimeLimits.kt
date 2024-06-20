package com.example.match_cards_game.models

data class TimeLimits(
    val easyLevelTime: Long = 60000, // 60 seconds
    val mediumLevelTime: Long = 120000, // 120 seconds
    val hardLevelTime: Long = 150000, // 150 seconds
    val veryHardLevelTime: Long = 180000 // 180 seconds
) {
    fun getTimeLimitForLevel(level: Board): Long {
        return when (level) {
            Board.EASY -> easyLevelTime
            Board.MEDIUM -> mediumLevelTime
            Board.HARD -> hardLevelTime
            Board.VERY_HARD -> veryHardLevelTime
        }
    }
}

