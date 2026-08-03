package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "folders")
data class FolderEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val iconName: String = "folder",
    val isSystemFolder: Boolean = false,
    val orderIndex: Int = 0
)
