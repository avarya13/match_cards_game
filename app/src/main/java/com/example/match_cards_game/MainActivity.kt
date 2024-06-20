package com.example.match_cards_game

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.ComponentActivity
import com.example.match_cards_game.models.Board

class MainActivity : ComponentActivity() {
    private lateinit var startGameButton: Button // Кнопка для начала игры
    private var selectedDifficulty: Board = Board.EASY // По умолчанию выбран уровень сложности "Легкий"

    // Метод, который вызывается при создании активности
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Устанавливаем макет для этой активности

        startGameButton = findViewById(R.id.startGameButton) // Инициализация кнопки для начала игры

        // Установка слушателя для кнопки
        startGameButton.setOnClickListener {
            startGame() // Вызов метода startGame при нажатии на кнопку
        }
    }

    // Метод для начала игры
    private fun startGame() {
        val intent = Intent(this, PlayGameActivity::class.java) // Создаем намерение для перехода к активности PlayGameActivity
        intent.putExtra("DIFFICULTY", selectedDifficulty) // Передаем выбранный уровень сложности
        startActivity(intent) // Запускаем новую активность
    }
}
