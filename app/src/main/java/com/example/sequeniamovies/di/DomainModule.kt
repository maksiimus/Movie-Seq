package com.example.sequeniamovies.di

import com.example.sequeniamovies.domain.repository.MoviesRepository
import com.example.sequeniamovies.domain.usecase.BuildGenresUseCase
import com.example.sequeniamovies.domain.usecase.FilterMoviesByGenreUseCase
import com.example.sequeniamovies.domain.usecase.GetMovieByIdUseCase
import com.example.sequeniamovies.domain.usecase.LoadMoviesUseCase
import com.example.sequeniamovies.domain.usecase.ToggleGenreUseCase
import dagger.Module
import dagger.Provides

@Module
class DomainModule {

    @Provides
    fun provideLoadMovies(repo: MoviesRepository): LoadMoviesUseCase =
        LoadMoviesUseCase(repo)

    @Provides
    fun provideBuildGenres(repo: MoviesRepository): BuildGenresUseCase =
        BuildGenresUseCase(repo)

    @Provides
    fun provideToggleGenre(repo: MoviesRepository): ToggleGenreUseCase =
        ToggleGenreUseCase(repo)

    @Provides
    fun provideFilterMoviesByGenre(repo: MoviesRepository): FilterMoviesByGenreUseCase =
        FilterMoviesByGenreUseCase(repo)

    @Provides
    fun provideGetMovieById(repo: MoviesRepository): GetMovieByIdUseCase =
        GetMovieByIdUseCase(repo)
}
