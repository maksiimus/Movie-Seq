package com.example.sequeniamovies.presentation.movies

import android.content.res.Resources
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sequeniamovies.presentation.adapter.MoviesScreenAdapter
import kotlin.math.roundToInt

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
            outRect.set(0, 0, 0, 0)
            return
        }

        val lp = view.layoutParams as GridLayoutManager.LayoutParams
        val isLeftColumn = lp.spanIndex == 0
        outRect.bottom = gapV
        outRect.left  = if (isLeftColumn) edge else gapH / 2
        outRect.right = if (isLeftColumn) gapH / 2 else edge
    }
}
