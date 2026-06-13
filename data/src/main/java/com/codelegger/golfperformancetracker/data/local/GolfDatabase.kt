package com.codelegger.golfperformancetracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.codelegger.golfperformancetracker.data.local.entity.PlayerEntity
import com.codelegger.golfperformancetracker.data.local.entity.ShotEntity

/**
 * The app's Room database. `exportSchema = true` writes the schema JSON to /schemas so
 * migrations stay reviewable and testable as the model evolves.
 */
@Database(
    entities = [PlayerEntity::class, ShotEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class GolfDatabase : RoomDatabase() {
    abstract fun golfDao(): GolfDao
}
