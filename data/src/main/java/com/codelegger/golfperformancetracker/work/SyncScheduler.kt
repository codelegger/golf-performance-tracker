package com.codelegger.golfperformancetracker.work

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import java.util.concurrent.TimeUnit

/** Schedules the periodic, connectivity-aware player sync. */
object SyncScheduler {

    private const val SYNC_INTERVAL_HOURS = 6L

    fun schedule(context: Context) {
        // Only run when connected — WorkManager defers the job until connectivity is restored,
        // which is exactly the "sync when back online" behaviour the brief asks for.
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = PeriodicWorkRequestBuilder<PlayerSyncWorker>(SYNC_INTERVAL_HOURS, TimeUnit.HOURS)
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS,
            )
            .build()

        // KEEP: don't reset the schedule/backoff on every app launch.
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            PlayerSyncWorker.UNIQUE_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request,
        )
    }
}
