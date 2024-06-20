package com.example.match_cards_game

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.match_cards_game.models.Board
import com.example.match_cards_game.models.Card
import kotlin.math.min

class BoardAdapter(
    private val context: Context,
    private val boardSize: Board,
    private val cards: List<Card>,
    private val cardClickListener: CardClickListener
) : RecyclerView.Adapter<BoardAdapter.ViewHolder>() {

    companion object {
        private const val MARGIN_SIZE = 10 // Размер отступа между карточками
        private const val TAG = "MemoryBoardAdapter" // Тег для логирования
    }

    interface CardClickListener {
        fun onCardClicked(position: Int) // Интерфейс для обработки кликов по карточкам
    }

    // Создание нового ViewHolder при создании новой карточки
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val cardWidth = parent.width / boardSize.getWidth() - (2 * MARGIN_SIZE) // Вычисляем ширину карточки
        val cardHeight = parent.height / boardSize.getHeight() - (2 * MARGIN_SIZE) // Вычисляем высоту карточки
        val cardSideLength = min(cardWidth, cardHeight) // Определяем размер стороны карточки как минимальное значение между шириной и высотой

        val view = LayoutInflater.from(context).inflate(R.layout.item_card, parent, false) // Инфлейтим layout карточки
        val layoutParams = view.findViewById<CardView>(R.id.cardView).layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.width = cardSideLength // Устанавливаем ширину карточки
        layoutParams.height = cardSideLength // Устанавливаем высоту карточки
        layoutParams.setMargins(MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE) // Устанавливаем отступы
        return ViewHolder(view)
    }

    // Привязка данных к ViewHolder при прокрутке RecyclerView
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position) // Привязка данных к элементу на данной позиции
    }

    // Возвращает количество карточек на доске
    override fun getItemCount() = boardSize.numCards

    // Внутренний класс ViewHolder, который описывает элементы карточки
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageButton = itemView.findViewById<ImageButton>(R.id.imageButton) // Получаем ссылку на ImageButton
        fun bind(position: Int) {
            val memoryCard = cards[position]
            imageButton.setImageResource(if (memoryCard.isFaceUp) cards[position].identifier else R.drawable.ic_launcher_background) // Устанавливаем изображение карточки
            imageButton.alpha = if (memoryCard.isMatched) .4f else 1.0f // Устанавливаем прозрачность, если карточка найдена
            val colorStateList = if (memoryCard.isMatched) ContextCompat.getColorStateList(context, R.color.color_gray) else null // Устанавливаем цвет, если карточка найдена
            imageButton.setOnClickListener {
                Log.i(TAG, "Clicked on position $position") // Логируем клик по карточке
                cardClickListener.onCardClicked(position) // Обрабатываем клик по карточке
            }
        }
    }
}
