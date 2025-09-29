package com.example.sequeniamovies.di

import android.app.Application
import com.example.sequeniamovies.presentation.MainActivity
import com.example.sequeniamovies.presentation.movies.MoviesFragment
import com.example.sequeniamovies.presentation.details.MovieDetailsFragment
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        DataModule::class,
        DomainModule::class,
        ViewModelModule::class
    ]
)
interface ApplicationComponent {

    fun inject(activity: MainActivity)
    fun inject(fragment: MoviesFragment)
    fun inject(fragment: MovieDetailsFragment)

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance application: Application): ApplicationComponent
    }
}
