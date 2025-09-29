package com.example.sequeniamovies.domain.entity

@JvmInline
value class Genre(val name: String) {
    init {
        require(name.isNotBlank()) { "Genre must be non-blank" }
    }
}