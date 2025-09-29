package com.example.sequeniamovies.presentation.adapter

import com.example.sequeniamovies.domain.entity.Genre
import com.example.sequeniamovies.domain.entity.Movie

sealed class UiItem {
    data class Header(val titleRes: Int) : UiItem()
    data class GenreRow(val genre: Genre, val isSelected: Boolean) : UiItem()
    data class MovieCard(val movie: Movie) : UiItem()
}