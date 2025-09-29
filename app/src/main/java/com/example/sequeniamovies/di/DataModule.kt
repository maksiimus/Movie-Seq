package com.example.sequeniamovies.di

import android.util.Log
import com.example.sequeniamovies.data.network.MoviesApi
import com.example.sequeniamovies.data.network.mapper.MovieMapper
import com.example.sequeniamovies.data.repository.MoviesRepositoryImpl
import com.example.sequeniamovies.domain.repository.MoviesRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
class DataModule {

    @Provides @Singleton
    fun provideMoshi(): Moshi =
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

    @Provides @Singleton
    fun provideOkHttp(): OkHttpClient {
        val httpLogger = HttpLoggingInterceptor { msg -> Log.d("HTTP", msg) }
            .apply { level = HttpLoggingInterceptor.Level.BODY }
        return OkHttpClient.Builder()
            .addInterceptor(httpLogger)
            .build()
    }

    @Provides @Singleton
    fun provideRetrofit(okHttp: OkHttpClient, moshi: Moshi): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://s3-eu-west-1.amazonaws.com/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okHttp)
            .build()

    @Provides @Singleton
    fun provideMoviesApi(retrofit: Retrofit): MoviesApi =
        retrofit.create(MoviesApi::class.java)

    @Provides @Singleton
    fun provideMovieMapper(): MovieMapper = MovieMapper()

    @Provides @Singleton
    fun provideMoviesRepository(
        api: MoviesApi,
        mapper: MovieMapper
    ): MoviesRepository = MoviesRepositoryImpl(api, mapper)
}
