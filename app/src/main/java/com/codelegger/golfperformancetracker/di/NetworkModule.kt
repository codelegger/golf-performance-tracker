package com.codelegger.golfperformancetracker.di

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
 * Provides the networking stack (OkHttp + Moshi + Retrofit) as application-scoped singletons.
 *
 * The concrete API service interface is added with the first data slice; this module sets up
 * everything needed to create it. [BASE_URL] is a placeholder pointing at MockAPI and will be
 * finalized when the API contract is defined.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // TODO(data-slice): replace with the real MockAPI base URL once the schema is hosted.
    private const val BASE_URL = "https://000000000000000000000000.mockapi.io/api/v1/"

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
}
