package com.example.sequeniamovies.data.network.mapper

import com.example.sequeniamovies.data.network.dto.MovieDto
import com.example.sequeniamovies.domain.entity.Genre
import com.example.sequeniamovies.domain.entity.Movie

class MovieMapper {

    fun map(dto: MovieDto): Movie = Movie(
        id            = dto.id,
        localizedName = dto.localizedName.orEmpty(),
        originalName  = dto.originalName.orEmpty(),
        year          = dto.year ?: 0,
        rating        = dto.rating,
        imageUrl      = dto.imageUrl,
        description   = dto.description,
        genres        = (dto.genres ?: emptyList())
            .mapNotNull { it.trim().takeIf(String::isNotBlank) }
            .distinctBy { it.lowercase() }
            .map(::Genre)
    )
}