package com.example.sequeniamovies.presentation.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sequeniamovies.domain.usecase.GetMovieByIdUseCase
import javax.inject.Inject
import kotlinx.coroutines.launch

class MovieDetailsViewModel @Inject constructor(
    private val getById: GetMovieByIdUseCase
) : ViewModel() {

    private val _state = MutableLiveData<MovieDetailsState>(MovieDetailsState.Loading)
    val state: LiveData<MovieDetailsState> = _state

    fun load(movieId: Int) {
        viewModelScope.launch {
            _state.value = MovieDetailsState.Loading
            runCatching { getById(movieId) }
                .onSuccess {
                    if (it == null) {
                        _state.value = MovieDetailsState.Error("Фильм не найден")
                    }
                    else {
                        _state.value = MovieDetailsState.Content(it)
                    }
                }
                .onFailure {
                    _state.value = MovieDetailsState.Error("Не удалось загрузить данные")
                }
        }
    }
}
