package com.example.match_cards_game.models

import org.intellij.lang.annotations.Identifier

data class Card(
    val identifier: Int,
    var isFaceUp: Boolean = false,
    var isMatched: Boolean = false,

    )