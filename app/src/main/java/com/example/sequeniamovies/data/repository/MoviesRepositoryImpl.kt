package com.example.sequeniamovies.data.repository

import com.example.sequeniamovies.data.network.MoviesApi
import com.example.sequeniamovies.data.network.mapper.MovieMapper
import com.example.sequeniamovies.domain.entity.Genre
import com.example.sequeniamovies.domain.entity.Movie
import com.example.sequeniamovies.domain.repository.MoviesRepository
import java.text.Collator
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MoviesRepositoryImpl @Inject constructor(
    private val api: MoviesApi,
    private val mapper: MovieMapper
) : MoviesRepository {

    @Volatile private var movies: List<Movie>? = null

    override suspend fun getMovies(): List<Movie> {
        movies?.let { return it }

        val dto = api.getFilms()
        val movies = dto.films.map { mapper.map(it) }

        val sorted = movies.sortedBy { it.localizedName.lowercase() }
        this@MoviesRepositoryImpl.movies = sorted
        return sorted
    }

    override fun buildGenres(movies: List<Movie>): List<Genre> {
        val locale = Locale.getDefault()
        val collator = Collator.getInstance(locale).apply {
            strength = Collator.PRIMARY // без учёта регистра/диакритики
        }

        // Собираем уникальные жанры и сортируем коллатором
        val names = movies.asSequence()
            .flatMap { it.genres.asSequence() }
            .map { it.name.trim() }
            .filter { it.isNotEmpty() }
            .map { it.lowercase(locale) }
            .distinct()                          // убрали дубли
            .sortedWith { a, b -> collator.compare(a, b) } // локалезависимая сортировка

        return names.map(::Genre).toList()
    }

    override fun filterMoviesByGenre(movies: List<Movie>, genre: Genre?): List<Movie> {
        if (genre == null) return movies
        val key = genre.name.lowercase()
        return movies.filter { m -> m.genres.any { g -> g.name.equals(key, ignoreCase = true) } }
    }

    override suspend fun getMovieById(id: Int): Movie? {
        val list = movies ?: getMovies()
        return list.firstOrNull { it.id == id }
    }

    override fun toggleGenre(current: Genre?, tapped: Genre): Genre? {
        return if (current?.name.equals(tapped.name, ignoreCase = true)) null else tapped
    }
}
