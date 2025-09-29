package com.example.sequeniamovies.domain.repository

import com.example.sequeniamovies.domain.entity.Genre
import com.example.sequeniamovies.domain.entity.Movie

interface MoviesRepository {
    suspend fun getMovies(): List<Movie>

    fun buildGenres(movies: List<Movie>): List<Genre>

    fun filterMoviesByGenre(movies: List<Movie>, genre: Genre?): List<Movie>

    suspend fun getMovieById(id: Int): Movie?

    fun toggleGenre(current: Genre?, tapped: Genre): Genre?
}
