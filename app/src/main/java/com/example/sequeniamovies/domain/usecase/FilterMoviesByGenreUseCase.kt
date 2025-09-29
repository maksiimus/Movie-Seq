package com.example.sequeniamovies.domain.usecase

import com.example.sequeniamovies.domain.entity.Genre
import com.example.sequeniamovies.domain.entity.Movie
import com.example.sequeniamovies.domain.repository.MoviesRepository
import javax.inject.Inject

class FilterMoviesByGenreUseCase @Inject constructor(
    private val repository: MoviesRepository
) {
    operator fun invoke(movies: List<Movie>, genre: Genre?): List<Movie> =
        repository.filterMoviesByGenre(movies, genre)
}
