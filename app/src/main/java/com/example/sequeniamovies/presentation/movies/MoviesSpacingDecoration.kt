package com.example.sequeniamovies.presentation.movies

import android.content.res.Resources
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sequeniamovies.presentation.adapter.MoviesScreenAdapter
import kotlin.math.roundToInt

/**
 * Отступы только для карточек фильмов:
 * - слева/справа по краям экрана: 16dp
 * - между карточками по горизонтали: 8dp
 * - вертикальные между карточками: 16dp
 * Жанры/заголовки не трогаем.
 */
class MoviesSpacingDecoration(
) : RecyclerView.ItemDecoration() {

    private val density = Resources.getSystem().displayMetrics.density
    private fun dp(v: Int) = (v * density).roundToInt()

    private val edge = dp(16)
    private val gapH = dp(8)
    private val gapV = dp(16)

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val pos = parent.getChildAdapterPosition(view)
        if (pos == RecyclerView.NO_POSITION) return

        val adapter = parent.adapter as? MoviesScreenAdapter ?: return
        val type = adapter.getItemViewType(pos)

        if (type != MoviesScreenAdapter.TYPE_MOVIE) {
            // Для заголовков/жанров — никаких внешних отступов
            outRect.set(0, 0, 0, 0)
            return
        }

        // Карточки фильмов (Grid 2 колонки)
        val lp = view.layoutParams as GridLayoutManager.LayoutParams
        val isLeftColumn = lp.spanIndex == 0

        outRect.top = gapV
        outRect.bottom = gapV
        outRect.left  = if (isLeftColumn) edge else gapH / 2
        outRect.right = if (isLeftColumn) gapH / 2 else edge
    }
}
