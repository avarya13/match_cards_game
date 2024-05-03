package com.example.match_cards_game

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import com.example.match_cards_game.models.Board

//
class MainActivity : ComponentActivity() {
    private lateinit var startGameButton: Button
    private lateinit var difficultyRadioGroup: RadioGroup
    private var selectedDifficulty: Board = Board.EASY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startGameButton = findViewById(R.id.startGameButton)
        difficultyRadioGroup = findViewById(R.id.difficultyRadioGroup)

        startGameButton.setOnClickListener {
            startGame()
        }
    }

    private fun startGame() {
        val intent = Intent(this, PlayGameActivity::class.java)
        intent.putExtra("DIFFICULTY", selectedDifficulty) // Передача выбранного уровня сложности
        startActivity(intent)
    }

    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            when (view.id) {
                R.id.radioEasy -> selectedDifficulty = Board.EASY
                R.id.radioMedium -> selectedDifficulty = Board.MEDIUM
                R.id.radioHard -> selectedDifficulty = Board.HARD
                R.id.radioVeryHard -> selectedDifficulty = Board.VERY_HARD
            }

            // После выбора уровня сложности, обновите игру
            //startGame()
        }
    }
}

