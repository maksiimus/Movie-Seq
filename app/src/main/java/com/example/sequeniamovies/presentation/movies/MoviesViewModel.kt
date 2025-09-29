package com.example.sequeniamovies.presentation.movies

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sequeniamovies.R
import com.example.sequeniamovies.domain.entity.Genre
import com.example.sequeniamovies.domain.entity.Movie
import com.example.sequeniamovies.domain.usecase.BuildGenresUseCase
import com.example.sequeniamovies.domain.usecase.FilterMoviesByGenreUseCase
import com.example.sequeniamovies.domain.usecase.LoadMoviesUseCase
import com.example.sequeniamovies.domain.usecase.ToggleGenreUseCase
import com.example.sequeniamovies.presentation.adapter.UiItem
import kotlinx.coroutines.launch
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject


class MoviesViewModel @Inject constructor(
    private val loadMovies: LoadMoviesUseCase,
    private val buildGenres: BuildGenresUseCase,
    private val toggleGenre: ToggleGenreUseCase,
    private val filterByGenre: FilterMoviesByGenreUseCase
) : ViewModel() {

    private val _state = MutableLiveData<MoviesScreenState>(MoviesScreenState.Loading)
    val state: LiveData<MoviesScreenState> = _state

    fun loadIfNeeded() {
        if (_state.value is MoviesScreenState.Content) return
        load()
    }

    private fun load() {
        viewModelScope.launch {
            _state.value = MoviesScreenState.Loading

            val prevSelectedName = (state.value as? MoviesScreenState.Content)?.selected?.name

            runCatching { loadMovies() }
                .onSuccess { movies ->
                    val genres = buildGenres(movies)
                    val restoredSelected = prevSelectedName?.let { name ->
                        genres.firstOrNull { it.name.equals(name, ignoreCase = true) }
                    }
                    val visible = filterByGenre(movies, restoredSelected)

                    _state.value = MoviesScreenState.Content(
                        movies = movies,
                        genres = genres,
                        selected = restoredSelected,
                        items = buildUiItems(genres, restoredSelected, visible)
                    )
                }
                .onFailure { e ->
                    _state.value = MoviesScreenState.Error(mapError(e))
                }
        }
    }

    fun onGenreTapped(genre: Genre) {
        val cur = _state.value as? MoviesScreenState.Content ?: return
        val newSelected = toggleGenre(cur.selected, genre)
        val visible = filterByGenre(cur.movies, newSelected)

        _state.value = cur.copy(
            selected = newSelected,
            items = buildUiItems(cur.genres, newSelected, visible)
        )
    }

    private fun buildUiItems(
        genres: List<Genre>,
        selected: Genre?,
        movies: List<Movie>
    ): List<UiItem> {
        val list = ArrayList<UiItem>(genres.size + movies.size + 2)
        if (genres.isNotEmpty()) {
            list += UiItem.Header(R.string.genres)
            genres.forEach { g ->
                list += UiItem.GenreRow(
                    g,
                    isSelected = selected?.name?.equals(g.name, true) == true
                )
            }
        }
        list += UiItem.Header(R.string.title_movies)
        movies.forEach { m -> list += UiItem.MovieCard(m) }
        return list
    }

    private fun mapError(t: Throwable): String = when (t) {
        is UnknownHostException, is SocketTimeoutException, is ConnectException ->
            "Ошибка подключения сети"

        else -> "Не удалось загрузить данные"
    }
}
