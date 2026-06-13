package com.codelegger.golfperformancetracker.di

import com.codelegger.golfperformancetracker.data.BuildConfig
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
 * The base URL comes from [BuildConfig.BASE_URL], wired from the `GOLF_BASE_URL` Gradle
 * property (see :data build script and README) so it's environment configuration rather than
 * a hard-coded constant. It must point at a MockAPI project exposing `/players` and
 * `/players/{id}/shots`. The app is offline-first, so with an unreachable URL it simply shows
 * cached data (empty on first run).
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

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
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    @Provides
    @Singleton
    fun provideGolfApi(retrofit: Retrofit): GolfApi = retrofit.create(GolfApi::class.java)
}
