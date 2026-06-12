package com.codelegger.golfperformancetracker

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Application entry point.
 *
 * - [HiltAndroidApp] generates the Hilt dependency graph for the whole app.
 * - Implements [Configuration.Provider] so WorkManager is initialized with Hilt's
 *   [HiltWorkerFactory], enabling `@HiltWorker` workers to receive injected dependencies.
 */
@HiltAndroidApp
class GolfApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}
