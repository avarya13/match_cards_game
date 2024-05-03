package com.example.match_cards_game.models

enum class Board(val numCards: Int) {
    EASY(8),
    MEDIUM(18),
    HARD(24),
    VERY_HARD(40);

    fun getWidth(): Int {
        return when (this) {
            EASY -> 2
            MEDIUM -> 3
            HARD -> 4
            VERY_HARD -> 5
        }
    }

    fun getHeight(): Int {
        return numCards / getWidth()
    }

    fun getNumPairs(): Int {
        return numCards / 2
    }

    fun getNextLevel(): Board? {
        return when(this) {
            EASY -> MEDIUM
            MEDIUM -> HARD
            HARD -> VERY_HARD
            VERY_HARD -> null // Возвращаем null, если VERY_HARD - последний уровень
        }
    }

}