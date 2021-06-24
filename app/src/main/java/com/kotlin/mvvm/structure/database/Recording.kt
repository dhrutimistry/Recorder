package com.kotlin.mvvm.structure.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.File

@Entity
data class Recording(
    @PrimaryKey(autoGenerate = true) var id: Int,

    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "time") val time: String?,
    @ColumnInfo(name = "file") val file: String?
)