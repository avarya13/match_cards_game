package com.example.match_cards_game.models

import com.example.match_cards_game.IMAGES

class Game(private val boardSize: Board) {

    val cards: List<Card>
    var numPairsFound = 0 // Количество найденных пар карт

    private var numCardFlips = 0 // Количество переворотов карт
    private var indexOfSingleSelectedCard: Int? = null // Индекс единственной перевернутой карты

    init {
        // Перемешиваем изображения и выбираем необходимое количество для текущего размера игрового поля
        val chosenImages = IMAGES.shuffled().take(boardSize.getNumPairs())
        // Дублируем и снова перемешиваем изображения
        val randomizedImages = (chosenImages + chosenImages).shuffled()
        // Создаем список объектов Card на основе перемешанных изображений
        cards = randomizedImages.map { Card(it) }
    }

    // Метод для переворота карты по заданной позиции
    fun flipCard(position: Int): Boolean {
        numCardFlips++ // Увеличиваем счетчик переворотов
        val card: Card = cards[position]
        var foundMatch = false

        if (indexOfSingleSelectedCard == null) {
            // Если ни одна или две карты перевернуты, восстанавливаем карты и сохраняем индекс текущей карты
            restoreCards()
            indexOfSingleSelectedCard = position
        } else {
            // Если одна карта уже перевернута, проверяем на совпадение
            foundMatch = checkForMatch(indexOfSingleSelectedCard!!, position)
            indexOfSingleSelectedCard = null
            restoreCards()
        }

        // Переворачиваем текущую карту
        card.isFaceUp = !card.isFaceUp
        return foundMatch // Возвращаем результат проверки на совпадение
    }

    // Метод для проверки совпадения двух карт
    private fun checkForMatch(position1: Int, position2: Int): Boolean {
        if (cards[position1].identifier != cards[position2].identifier) {
            return false // Если идентификаторы не совпадают, возвращаем false
        }
        // Устанавливаем признак совпадения для обеих карт и увеличиваем счетчик найденных пар
        cards[position1].isMatched = true
        cards[position2].isMatched = true
        numPairsFound++
        return true // Возвращаем true, если карты совпали
    }

    // Метод для восстановления состояния карт (переворачиваем непарные карты лицом вниз)
    private fun restoreCards() {
        for (card: Card in cards) {
            if (!card.isMatched) {
                card.isFaceUp = false
            }
        }
    }

    // Метод для проверки, выиграна ли игра
    fun haveWonGame(): Boolean {
        return numPairsFound == boardSize.getNumPairs()
    }

    // Метод для проверки, перевернута ли карта
    fun isCardFacedUp(position: Int): Boolean {
        return cards[position].isFaceUp
    }

    // Метод для получения количества ходов
    fun getNumMoves(): Int {
        return numCardFlips / 2
    }
}
