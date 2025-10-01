package com.example.sequeniamovies.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.dispose
import coil.load
import com.example.sequeniamovies.R
import com.example.sequeniamovies.databinding.ItemGenreBinding
import com.example.sequeniamovies.databinding.ItemHeaderBinding
import com.example.sequeniamovies.databinding.ItemMovieBinding
import com.example.sequeniamovies.domain.entity.Genre
import com.example.sequeniamovies.domain.entity.Movie

class MoviesScreenAdapter(
    private val onGenreClick: (Genre) -> Unit,
    private val onMovieClick: (Movie) -> Unit
) : ListAdapter<UiItem, RecyclerView.ViewHolder>(Diff) {

    init { setHasStableIds(true) }

    override fun getItemId(position: Int): Long = when (val item = getItem(position)) {
        is UiItem.Header    -> item.titleRes.toLong()
        is UiItem.GenreRow  -> ("genre:" + item.genre.name.lowercase()).hashCode().toLong()
        is UiItem.MovieCard -> item.movie.id.toLong()
    }

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is UiItem.Header    -> TYPE_HEADER
        is UiItem.GenreRow  -> TYPE_GENRE
        is UiItem.MovieCard -> TYPE_MOVIE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_HEADER -> HeaderVH(ItemHeaderBinding.inflate(
                inflater,
                parent,
                false
            ))
            TYPE_GENRE  -> GenreVH(ItemGenreBinding.inflate(
                inflater,
                parent,
                false
            ), onGenreClick)
            else        -> MovieVH(ItemMovieBinding.inflate(
                inflater,
                parent,
                false
            ), onMovieClick)
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.any { it === PayloadSelection } && holder is GenreVH) {
            val item = getItem(position) as UiItem.GenreRow
            holder.bindSelection(item.isSelected)
            return
        }
        super.onBindViewHolder(holder, position, payloads)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is UiItem.Header    -> (holder as HeaderVH).bind(item)
            is UiItem.GenreRow  -> (holder as GenreVH).bind(item)
            is UiItem.MovieCard -> (holder as MovieVH).bind(item)
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        if (holder is MovieVH) holder.recycle()
        super.onViewRecycled(holder)
    }

    class HeaderVH(
        private val binding: ItemHeaderBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: UiItem.Header) {
            binding.tvHeader.setText(item.titleRes)
        }
    }

    class GenreVH(
        private val binding: ItemGenreBinding,
        private val onClick: (Genre) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: UiItem.GenreRow) {
            binding.tvGenre.text = item.genre.name.replaceFirstChar { it.uppercase() }
            bindSelection(item.isSelected)
            binding.root.setOnClickListener { onClick(item.genre) }
        }

        fun bindSelection(selected: Boolean) {
            // фон ставим на контейнер, не на TextView
            binding.genreContainer.setBackgroundResource(
                if (selected) R.drawable.bg_genre_selected
                else android.R.color.white
            )
        }
    }

    class MovieVH(
        private val binding: ItemMovieBinding,
        private val onClick: (Movie) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: UiItem.MovieCard) {
            val m = item.movie
            binding.tvName.text = m.localizedName
            binding.ivPoster.load(m.imageUrl ?: "") {
                crossfade(true)
                placeholder(R.drawable.no_img)
                error(R.drawable.no_img)
                fallback(R.drawable.no_img)
            }
            binding.root.setOnClickListener { onClick(m) }
        }

        fun recycle() {
            binding.ivPoster.dispose()
            binding.tvName.text = null
        }
    }

    companion object {
        const val TYPE_HEADER = 1
        const val TYPE_GENRE  = 2
        const val TYPE_MOVIE  = 3
        private object PayloadSelection
    }

    object Diff : DiffUtil.ItemCallback<UiItem>() {
        override fun areItemsTheSame(old: UiItem, new: UiItem): Boolean = when {
            old is UiItem.Header && new is UiItem.Header -> old.titleRes == new.titleRes
            old is UiItem.GenreRow && new is UiItem.GenreRow ->
                old.genre.name.equals(new.genre.name, true)
            old is UiItem.MovieCard && new is UiItem.MovieCard ->
                old.movie.id == new.movie.id
            else -> false
        }

        override fun areContentsTheSame(old: UiItem, new: UiItem): Boolean = old == new

        override fun getChangePayload(oldItem: UiItem, newItem: UiItem): Any? {
            if (oldItem is UiItem.GenreRow && newItem is UiItem.GenreRow &&
                oldItem.isSelected != newItem.isSelected
            ) return PayloadSelection
            return null
        }
    }
}
