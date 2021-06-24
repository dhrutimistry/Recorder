package com.kotlin.mvvm.structure.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Recording::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recordingDao(): RecordingDao
}