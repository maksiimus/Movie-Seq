package com.example.sequeniamovies.domain.usecase

import com.example.sequeniamovies.domain.entity.Movie
import com.example.sequeniamovies.domain.repository.MoviesRepository
import javax.inject.Inject

class LoadMoviesUseCase @Inject constructor(
    private val repository: MoviesRepository
) {
    suspend operator fun invoke(): List<Movie> = repository.getMovies()
}
