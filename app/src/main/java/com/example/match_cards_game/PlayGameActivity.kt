package com.example.match_cards_game

import android.animation.ArgbEvaluator
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.match_cards_game.models.Board
import com.example.match_cards_game.models.Game
import com.example.match_cards_game.models.GameTimer
import com.example.match_cards_game.models.TimeLimits

class PlayGameActivity : ComponentActivity() {
    companion object {
        private const val TAG = "PlayGameActivity"
    }

    private lateinit var clRoot: ConstraintLayout // Корневой макет
    private lateinit var memoryGame: Game // Экземпляр игры
    private lateinit var rvBoard: RecyclerView // RecyclerView для отображения карт
    private lateinit var tvNumMoves: TextView // Текстовое поле для отображения количества ходов
    private lateinit var tvNumPairs: TextView // Текстовое поле для отображения количества найденных пар
    private lateinit var adapter: BoardAdapter // Адаптер для RecyclerView
    private var board: Board = Board.EASY // Уровень сложности по умолчанию

    private lateinit var tvTimer: TextView // Текстовое поле для таймера
    private var countDownTimer: CountDownTimer? = null // Таймер обратного отсчета
    private var timeElapsed = 0L // Прошедшее время

    private lateinit var timeLimits: TimeLimits // Ограничения времени для различных уровней
    private lateinit var timer: GameTimer // Таймер игры

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_game)

        clRoot = findViewById(R.id.clRoot)
        if (clRoot == null) {
            Log.e(TAG, "clRoot is not initialized properly")
            return
        }

        rvBoard = findViewById(R.id.recyclerView)
        tvNumMoves = findViewById(R.id.tvNumMoves)
        tvNumPairs = findViewById(R.id.tvNumPairs)

        // Инициализация timeLimits
        timeLimits = TimeLimits()

        // Получение выбранного уровня сложности из интента
        val selectedDifficulty = intent.getStringExtra("DIFFICULTY")
        if (selectedDifficulty != null) {
            board = Board.valueOf(selectedDifficulty.toUpperCase())
        } else {
            board = Board.EASY // Значение по умолчанию, если сложность не передана
        }

        // Создание экземпляра GameTimer для текущего уровня
        timer = GameTimer(timeLimits, board)

        tvNumPairs.setTextColor(ContextCompat.getColor(this, R.color.color_progress_none))

        memoryGame = Game(board)

        adapter = BoardAdapter(
            this,
            board,
            memoryGame.cards,
            object : BoardAdapter.CardClickListener {
                override fun onCardClicked(position: Int) {
                    updateGameWithFlip(position)
                }
            })
        rvBoard.adapter = adapter
        rvBoard.setHasFixedSize(true)
        rvBoard.layoutManager = GridLayoutManager(this, board.getWidth())

        tvTimer = findViewById(R.id.tvTimer)
        startTimer()

        val exitIcon: ImageView = findViewById(R.id.ivExit)
        exitIcon.setOnClickListener {
            exitRound()
        }
    }

    private fun showExitConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Exit Game")
        builder.setMessage("Are you sure you want to exit the game?")
        builder.setPositiveButton("Yes") { dialog, which ->
            exitRound()
        }
        builder.setNegativeButton("No") { dialog, which ->
            // Ничего не делать, закрыть диалог
        }
        val dialog = builder.create()
        dialog.show() // Показать диалог подтверждения выхода
    }

    private fun exitRound() {
        Toast.makeText(this, "Exiting the game...", Toast.LENGTH_SHORT).show()
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            finish() // Завершить текущую активность, чтобы предотвратить возвращение к ней при нажатии назад
        }, 2000)
    }

    private fun updateGameWithFlip(position: Int) {
        if (memoryGame.haveWonGame()) {
            timer.stopTimer()
            val toast = Toast.makeText(this, "You already won!", Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.CENTER, 0, 0)
            toast.show()
            return
        }
        if (memoryGame.isCardFacedUp(position)) {
            val toast = Toast.makeText(this, "Invalid move!", Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.CENTER, 0, 0)
            toast.show()
            return
        }
        if (memoryGame.flipCard(position)) {
            Log.i(TAG, "found a Match! Num Pairs found: ${memoryGame.numPairsFound}")
            val color = ArgbEvaluator().evaluate(
                memoryGame.numPairsFound.toFloat() / board.getNumPairs(),
                ContextCompat.getColor(this, R.color.color_progress_none),
                ContextCompat.getColor(this, R.color.color_progress_full)
            ) as Int

            tvNumPairs.setTextColor(color)
            tvNumPairs.text = "Pairs: ${memoryGame.numPairsFound} / ${board.getNumPairs()}"
            if (memoryGame.haveWonGame()) {
                Handler(Looper.getMainLooper()).postDelayed({
                    navigateToNextLevel()
                }, 2000)
            }
        }
        tvNumMoves.text = "Moves: ${memoryGame.getNumMoves()}"
        adapter.notifyDataSetChanged()
    }

    private fun navigateToNextLevel() {
        val nextLevel = board.getNextLevel()
        if (nextLevel != null) {
            timer.stopTimer()
            val message = "Congratulations! You have unlocked the ${nextLevel.name.toLowerCase()} level."
            val toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.CENTER, 0, 0)
            toast.show()
            board = nextLevel

            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this, PlayGameActivity::class.java)
                intent.putExtra("DIFFICULTY", nextLevel.name)
                startActivity(intent)
                finish()
            }, 2000)
        } else {
            timer.stopTimer()
            val toast = Toast.makeText(this, "You Won! Congratulations!", Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.CENTER, 0, 0)
            toast.show()
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }, 2000)
        }
    }

    private fun startTimer() {
        timer.startTimer(
            { timeRemaining ->
                updateTimer(timeRemaining)
            },
            {
                handleGameEnd() // Обрабатываем проигрыш при истечении времени
            }
        )
    }

    private fun handleGameEnd() {
        timer.stopTimer()
        val toast = Toast.makeText(this, "Time's up! Game Over!", Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 2000)
    }

    private fun updateTimer(time: Long) {
        val minutes = (time / 60000).toInt()
        val seconds = (time % 60000 / 1000).toInt()
        val timeString = String.format("%02d:%02d", minutes, seconds)
        tvTimer.text = "Timer: $timeString"
    }

    override fun onDestroy() {
        countDownTimer?.cancel()
        super.onDestroy()
    }
}
