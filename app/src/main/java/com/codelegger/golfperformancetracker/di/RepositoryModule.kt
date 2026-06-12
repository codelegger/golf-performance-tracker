package com.codelegger.golfperformancetracker.di

import com.codelegger.golfperformancetracker.data.repository.PlayerRepositoryImpl
import com.codelegger.golfperformancetracker.data.repository.ShotRepositoryImpl
import com.codelegger.golfperformancetracker.domain.repository.PlayerRepository
import com.codelegger.golfperformancetracker.domain.repository.ShotRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Binds repository interfaces to their implementations. `@Binds` is more efficient than
 * `@Provides` for interface→impl wiring (no factory method body generated), and depending on
 * the [PlayerRepository] interface (not the impl) keeps the ViewModels testable with fakes.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPlayerRepository(impl: PlayerRepositoryImpl): PlayerRepository

    @Binds
    @Singleton
    abstract fun bindShotRepository(impl: ShotRepositoryImpl): ShotRepository
}
