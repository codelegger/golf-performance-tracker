package com.codelegger.golfperformancetracker.di

import com.codelegger.golfperformancetracker.data.remote.GolfApi
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

/**
 * Provides the networking stack (OkHttp + Moshi + Retrofit + [GolfApi]) as singletons.
 *
 * [BASE_URL] must point at a MockAPI project exposing `/players` and `/players/{id}/shots`.
 * Replace the placeholder below with your MockAPI base URL (see README). The app is
 * offline-first, so with an unreachable URL it simply shows cached data (empty on first run).
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // MockAPI project exposing /players and /players/{id}/shots.
    private const val BASE_URL = "https://6a2c5b9a3e2b60ab038fb5c0.mockapi.io/api/v1/"

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            // BASIC keeps logs lean; raise to BODY locally when debugging payloads.
            level = HttpLoggingInterceptor.Level.BASIC
        }

    @Provides
    @Singleton
    fun provideOkHttpClient(logging: HttpLoggingInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

    @Provides
    @Singleton
    fun provideMoshi(): Moshi =
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient, moshi: Moshi): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    @Provides
    @Singleton
    fun provideGolfApi(retrofit: Retrofit): GolfApi = retrofit.create(GolfApi::class.java)
}
