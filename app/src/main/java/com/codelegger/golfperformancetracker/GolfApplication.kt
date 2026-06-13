package com.codelegger.golfperformancetracker

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.codelegger.golfperformancetracker.work.SyncScheduler
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
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

    override fun onCreate() {
        super.onCreate()
        // Structured logging. In a release build this would be gated / swapped for a
        // crash-reporting tree; a take-home keeps it simple with the debug tree.
        Timber.plant(Timber.DebugTree())
        // Schedule the periodic, connectivity-aware background sync.
        SyncScheduler.schedule(this)
        Timber.d("GolfApplication started; periodic player sync scheduled")
    }
}

