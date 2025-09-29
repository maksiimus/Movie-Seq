package com.example.sequeniamovies

import android.app.Application
import com.example.sequeniamovies.di.ApplicationComponent
import com.example.sequeniamovies.di.DaggerApplicationComponent

class MoviesApp : Application() {
    lateinit var appComponent: ApplicationComponent
        private set

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerApplicationComponent.factory().create(this)
    }
}