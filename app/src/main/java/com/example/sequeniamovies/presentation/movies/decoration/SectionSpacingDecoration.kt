package com.example.sequeniamovies.presentation.movies.decoration

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.sequeniamovies.R
import com.example.sequeniamovies.presentation.adapter.UiItem
import kotlin.math.roundToInt

class SectionSpacingDecoration(
    private val adapter: ListAdapter<UiItem, *>,
    context: Context
) : RecyclerView.ItemDecoration() {

    private val dp8 = 8.dp(context)
    private val dp16 = 16.dp(context)

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val pos = parent.getChildAdapterPosition(view)
        if (pos == RecyclerView.NO_POSITION) return
        val item = adapter.currentList.getOrNull(pos) ?: return

        when (item) {
            is UiItem.Header -> {
                if (item.titleRes == R.string.genres) {
                    outRect.top = dp8
                } else if (item.titleRes == R.string.title_movies) {
                    outRect.top = dp16
                    outRect.bottom = dp8
                }
            }

            else -> {}
        }
    }

    private fun Int.dp(context: Context): Int =
        (this * context.resources.displayMetrics.density).roundToInt()
}
