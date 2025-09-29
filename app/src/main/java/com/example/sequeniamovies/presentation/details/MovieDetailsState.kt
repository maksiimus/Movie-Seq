package com.example.sequeniamovies.presentation.details

import com.example.sequeniamovies.domain.entity.Movie

sealed interface MovieDetailsState {
    data object Loading : MovieDetailsState
    data class Content(val movie: Movie) : MovieDetailsState
    data class Error(val message: String) : MovieDetailsState
}
