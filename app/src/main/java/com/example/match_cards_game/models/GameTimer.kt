package com.example.match_cards_game.models

import android.os.CountDownTimer
import android.os.SystemClock

class GameTimer(private val timeLimits: TimeLimits, private val level: Board) {
    private var timeRemaining: Long = 0 // Оставшееся время
    private var startTime: Long = 0  // Время начала отсчёта
    private var countDownTimer: CountDownTimer? = null
    private var isTimerRunning: Boolean = false // Флаг состояния таймера

    // Инициализация оставшегося времени в зависимости от уровня сложности
    init {
        timeRemaining = when (level) {
            Board.EASY -> timeLimits.easyLevelTime
            Board.MEDIUM -> timeLimits.mediumLevelTime
            Board.HARD -> timeLimits.hardLevelTime
            Board.VERY_HARD -> timeLimits.veryHardLevelTime
        }
    }

    // Метод для запуска таймера
    fun startTimer(updateCallback: (Long) -> Unit, finishCallback: () -> Unit) {
        startTime = SystemClock.elapsedRealtime()  // Сохраняем время начала отсчёта
        countDownTimer = object : CountDownTimer(timeRemaining, 1000) { // Создаем новый объект CountDownTimer
            override fun onTick(millisUntilFinished: Long) {
                timeRemaining = millisUntilFinished // Обновляем оставшееся время
                updateCallback(timeRemaining) // Вызываем обратный вызов для обновления интерфейса
            }

            override fun onFinish() {
                isTimerRunning = false // Таймер завершен
                finishCallback() // Вызываем обратный вызов для завершения игры
            }
        }
        countDownTimer?.start() // Запускаем таймер
        isTimerRunning = true // Устанавливаем флаг состояния таймера
    }

    // Метод для остановки таймера
    fun stopTimer() {
        countDownTimer?.cancel() // Останавливаем таймер
        isTimerRunning = false // Сбрасываем флаг состояния таймера
    }

    // Метод для получения прошедшего времени
    fun getTimeElapsed(): Long {
        val elapsedRealtime = SystemClock.elapsedRealtime()
        return if (isTimerRunning) {
            elapsedRealtime - startTime // Возвращаем разницу между текущим временем и временем старта
        } else {
            timeLimits.getTimeLimitForLevel(level) - timeRemaining // Возвращаем прошедшее время как разницу между полным временем и оставшимся
        }
    }

    // Метод для получения оставшегося времени
    fun getTimeRemaining(): Long {
        return timeRemaining
    }

    // Метод для проверки состояния таймера
    fun isTimerRunning(): Boolean {
        return isTimerRunning
    }
}
