package com.example.sequeniamovies.domain.entity

data class Movie(
    val id: Int,
    val localizedName: String,
    val originalName: String,
    val year: Int,
    val rating: Double?,
    val imageUrl: String?,
    val description: String?,
    val genres: List<Genre>
)