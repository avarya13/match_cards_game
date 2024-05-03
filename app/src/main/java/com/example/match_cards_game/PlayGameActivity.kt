package com.example.match_cards_game

import android.animation.ArgbEvaluator
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.match_cards_game.models.Board
import com.example.match_cards_game.models.Game
import com.example.match_cards_game.view.BoardAdapter
import com.google.android.material.snackbar.Snackbar
import androidx.fragment.app.FragmentManager
import com.example.match_cards_game.models.GameTimer
import com.example.match_cards_game.models.TimeLimits


class PlayGameActivity : ComponentActivity() { // : ComponentActivity()
    companion object {
        private const val TAG = "PlayGameActivity"
    }
    private lateinit var clRoot: ConstraintLayout
    private lateinit var memoryGame: Game
    private lateinit var rvBoard: RecyclerView
    private lateinit var tvNumMoves: TextView
    private lateinit var tvNumPairs: TextView
    private lateinit var adapter: BoardAdapter
    private var board: Board = Board.EASY

    private lateinit var tvTimer: TextView
    private var countDownTimer: CountDownTimer? = null
    private var timeElapsed = 0L


    private lateinit var timeLimits: TimeLimits
    private lateinit var timer: GameTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_game)

        clRoot = findViewById(R.id.clRoot)
        rvBoard = findViewById(R.id.recyclerView)
        tvNumMoves = findViewById(R.id.tvNumMoves)
        tvNumPairs = findViewById(R.id.tvNumPairs)

        // Инициализация levelTimeLimits
        timeLimits = TimeLimits()

        // Создание экземпляра LevelTimeLimit для текущего уровня
        timer = GameTimer(timeLimits, board)

        tvNumPairs.setTextColor(ContextCompat.getColor(this, R.color.color_progress_none))

        val selectedDifficulty = intent.getSerializableExtra("DIFFICULTY") as Board
        board = selectedDifficulty
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
            //showExitConfirmationDialog()
        }


    }

    private fun showExitConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Exit Game")
        builder.setMessage("Are you sure you want to exit the game?")
        builder.setPositiveButton("Yes") { dialog, which ->
            // Do something when "Yes" button is clicked
            exitRound() // Move this line inside the 'onClick' block
        }
        builder.setNegativeButton("No") { dialog, which ->
            // Do nothing, dismiss the dialog
        }
        val dialog = builder.create()
        dialog.show()  // Show the exit confirmation dialog
    }


    private fun exitRound() {
        // Navigate back to the main screen (MainActivity)
        Toast.makeText(this, "Exiting the game...", Toast.LENGTH_SHORT).show()
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            finish() // Finish the current activity to prevent returning to it on back press
        }, 2000)

    }

    private fun updateGameWithFlip(position: Int) {
        // Error checking
        if (memoryGame.haveWonGame()) {
            // alert the user invalid move
            //Snackbar.make(clRoot, "You already won!", Snackbar.LENGTH_LONG ).show()
            Toast.makeText(this, "You already won!", Toast.LENGTH_SHORT).show()
            return
        }
        if (memoryGame.isCardFacedUp(position)) {
            // alert the user invalid move
            //Snackbar.make(clRoot, "Invalid move!", Snackbar.LENGTH_SHORT).show()
            Toast.makeText(this, "Invalid move!", Toast.LENGTH_SHORT).show()
            return
        }
        //actually flipping over card
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
               // Toast.makeText(this, "You Won! Congratulations!", Toast.LENGTH_SHORT).show()

                Handler(Looper.getMainLooper()).postDelayed({
                    // Переход на следующий уровень после завершения игры
                    val nextLevel = board.getNextLevel()
                    navigateToNextLevel()
                }, 2000)
            }
        }
        tvNumMoves.text = "Moves: ${memoryGame.getNumMoves()}"
        adapter.notifyDataSetChanged()

    }

    private fun navigateToNextLevel() {
        timer.stopTimer()
        // Определяем следующий уровень
        val nextLevel = board.getNextLevel()
        if (nextLevel != null) {
            val message = "Congratulations! You have unlocked the ${nextLevel.name.toLowerCase()} level."
            // Показываем предупреждение о новом уровне
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            // Переход на новый уровень
            board = nextLevel

            // Переход на уровень
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this, PlayGameActivity::class.java)
                intent.putExtra("DIFFICULTY", nextLevel)
                startActivity(intent)
                finish()
            }, 2000)// Finish the current activity to prevent returning to it on back press
        } else {
            // Handle the case where nextLevel is null
            // Можно добавить сообщение, что больше нет следующего уровня
            Toast.makeText(this, "You Won! Congratulations!", Toast.LENGTH_SHORT).show()
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
        // Действия при завершении времени (проигрыш)
        Toast.makeText(this, "Time's up! Game Over!", Toast.LENGTH_SHORT).show()
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