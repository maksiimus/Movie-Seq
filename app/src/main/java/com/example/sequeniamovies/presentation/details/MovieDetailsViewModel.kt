package com.example.sequeniamovies.presentation.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sequeniamovies.domain.entity.Movie
import com.example.sequeniamovies.domain.usecase.GetMovieByIdUseCase
import javax.inject.Inject
import kotlinx.coroutines.launch

class MovieDetailsViewModel @Inject constructor(
    private val getById: GetMovieByIdUseCase
) : ViewModel() {

    private val _movie = MutableLiveData<Movie?>()
    val movie: LiveData<Movie?> = _movie

    fun load(movieId: Int) {
        viewModelScope.launch {
            val m = runCatching { getById(movieId) }.getOrNull()
            _movie.value = m
        }
    }
}
