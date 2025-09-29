package com.example.sequeniamovies.data.network.dto

import com.squareup.moshi.Json

data class FilmsResponseDto(
    @Json(name = "films") val films: List<MovieDto>
)