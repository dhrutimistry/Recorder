package com.kotlin.mvvm.structure.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RecordingDao {
    @Query("SELECT * FROM recording")
    fun getAll(): List<Recording>

    @Insert
    fun insertAll(recordings: ArrayList<Recording>)

    @Insert
    fun insert(recording: Recording)

    @Query("DELETE FROM recording")
    fun removeAll()
}