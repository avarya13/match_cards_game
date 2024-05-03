package com.example.match_cards_game.models

import android.os.CountDownTimer


class GameTimer(private val timeLimits: TimeLimits, private val level: Board) {
    private var timeRemaining: Long = 0
    private var countDownTimer: CountDownTimer? = null
    private var isTimerRunning: Boolean = false

    init {
        timeRemaining = when (level) {
            Board.EASY -> timeLimits.easyLevelTime
            Board.MEDIUM -> timeLimits.mediumLevelTime
            Board.HARD -> timeLimits.hardLevelTime
            Board.VERY_HARD -> timeLimits.veryHardLevelTime
        }
    }

    fun startTimer(
        updateCallback: (Long) -> Unit,
        finishCallback: () -> Unit
    ) {
        countDownTimer = object : CountDownTimer(timeRemaining, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeRemaining = millisUntilFinished
                updateCallback(timeRemaining)
            }

            override fun onFinish() {
                isTimerRunning = false
                finishCallback()
            }
        }
        countDownTimer?.start()
        isTimerRunning = true
    }

    fun stopTimer() {
        countDownTimer?.cancel()
        isTimerRunning = false
    }

    /*fun resetTimer() {
        timeRemaining = timeLimitMillis
        isTimerRunning = false
    }*/

    fun getTimeRemaining(): Long {
        return timeRemaining
    }

    fun isTimerRunning(): Boolean {
        return isTimerRunning
    }
}
