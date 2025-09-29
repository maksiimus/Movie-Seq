package com.example.sequeniamovies.data.network

import com.example.sequeniamovies.data.network.dto.FilmsResponseDto
import retrofit2.http.GET

interface MoviesApi {
    @GET("sequeniatesttask/films.json")
    suspend fun getFilms(): FilmsResponseDto
}