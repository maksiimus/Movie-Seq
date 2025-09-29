package com.example.sequeniamovies.domain.usecase

import com.example.sequeniamovies.domain.entity.Genre
import com.example.sequeniamovies.domain.repository.MoviesRepository
import javax.inject.Inject

class ToggleGenreUseCase @Inject constructor(
    private val repository: MoviesRepository
) {
    operator fun invoke(current: Genre?, tapped: Genre): Genre? =
        repository.toggleGenre(current, tapped)
}
