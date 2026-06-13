package com.codelegger.golfperformancetracker.di

import android.content.Context
import androidx.room.Room
import com.codelegger.golfperformancetracker.data.local.GolfDao
import com.codelegger.golfperformancetracker.data.local.GolfDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** Provides the Room database and its DAO as application-scoped singletons. */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private const val DATABASE_NAME = "golf.db"

    @Provides
    @Singleton
    fun provideGolfDatabase(@ApplicationContext context: Context): GolfDatabase =
        Room.databaseBuilder(context, GolfDatabase::class.java, DATABASE_NAME)
            .build()

    @Provides
    fun provideGolfDao(database: GolfDatabase): GolfDao = database.golfDao()
}
