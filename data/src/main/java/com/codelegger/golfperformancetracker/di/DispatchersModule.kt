package com.codelegger.golfperformancetracker.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier

/** Marks the dispatcher used for IO-bound work (network, disk, Room). */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IoDispatcher

/** Marks the default dispatcher used for CPU-bound work (parsing, mapping). */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DefaultDispatcher

/**
 * Provides [CoroutineDispatcher]s through Hilt rather than referencing [Dispatchers]
 * directly in repositories/use-cases. This keeps them swappable for a test dispatcher,
 * which is essential for deterministic coroutine tests.
 */
@Module
@InstallIn(SingletonComponent::class)
object DispatchersModule {

    @Provides
    @IoDispatcher
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @DefaultDispatcher
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default
}
