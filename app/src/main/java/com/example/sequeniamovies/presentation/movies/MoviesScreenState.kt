package com.example.sequeniamovies.presentation.movies

import com.example.sequeniamovies.domain.entity.Genre
import com.example.sequeniamovies.domain.entity.Movie
import com.example.sequeniamovies.presentation.adapter.UiItem

sealed interface MoviesScreenState {
    data object Loading : MoviesScreenState
    data class Content(
        val movies: List<Movie>,
        val genres: List<Genre>,
        val selected: Genre?,
        val items: List<UiItem>
    ) : MoviesScreenState
    data class Error(val message: String) : MoviesScreenState
}
