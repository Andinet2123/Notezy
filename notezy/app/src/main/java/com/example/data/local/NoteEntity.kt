package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val folderId: String = "notes",
    val isPinned: Boolean = false,
    val isLocked: Boolean = false,
    val pinCode: String = "1234",
    val updatedAt: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis(),
    val drawingData: String? = null,
    val audioDurationSec: Int? = null,
    val audioTitle: String? = null,
    val checklistsJson: String? = null,
    val colorHex: String? = null,
    val isInTrash: Boolean = false,
    val isArchived: Boolean = false
)
