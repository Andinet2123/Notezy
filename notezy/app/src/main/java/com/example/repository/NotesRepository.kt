package com.example.repository

import com.example.data.local.FolderEntity
import com.example.data.local.NoteDao
import com.example.data.local.NoteEntity
import kotlinx.coroutines.flow.Flow

class NotesRepository(private val noteDao: NoteDao) {

    val activeNotes: Flow<List<NoteEntity>> = noteDao.getActiveNotes()
    val archivedNotes: Flow<List<NoteEntity>> = noteDao.getArchivedNotes()
    val trashNotes: Flow<List<NoteEntity>> = noteDao.getTrashNotes()
    val folders: Flow<List<FolderEntity>> = noteDao.getAllFolders()

    fun getNotesByFolder(folderId: String): Flow<List<NoteEntity>> {
        return when (folderId) {
            "all", "" -> noteDao.getActiveNotes()
            "archive" -> noteDao.getArchivedNotes()
            "trash" -> noteDao.getTrashNotes()
            else -> noteDao.getNotesByFolder(folderId)
        }
    }

    fun searchNotes(query: String): Flow<List<NoteEntity>> {
        return noteDao.searchNotes(query)
    }

    fun getNoteById(id: Long): Flow<NoteEntity?> {
        return noteDao.getNoteById(id)
    }

    suspend fun getNoteByIdDirect(id: Long): NoteEntity? {
        return noteDao.getNoteByIdDirect(id)
    }

    suspend fun insertNote(note: NoteEntity): Long {
        return noteDao.insertNote(note)
    }

    suspend fun updateNote(note: NoteEntity) {
        noteDao.updateNote(note)
    }

    suspend fun deleteNote(note: NoteEntity) {
        noteDao.deleteNote(note)
    }

    suspend fun deleteNoteById(id: Long) {
        noteDao.deleteNoteById(id)
    }

    suspend fun togglePin(id: Long, isPinned: Boolean) {
        noteDao.updatePinStatus(id, isPinned)
    }

    suspend fun toggleLock(id: Long, isLocked: Boolean, pinCode: String = "1234") {
        noteDao.updateLockStatus(id, isLocked, pinCode)
    }

    suspend fun moveToTrash(id: Long, isInTrash: Boolean) {
        noteDao.updateTrashStatus(id, isInTrash)
    }

    suspend fun toggleArchive(id: Long, isArchived: Boolean) {
        noteDao.updateArchiveStatus(id, isArchived)
    }

    suspend fun addFolder(name: String, iconName: String = "folder") {
        val folderId = name.lowercase().replace(" ", "_")
        val folder = FolderEntity(
            id = folderId,
            name = name,
            iconName = iconName,
            isSystemFolder = false
        )
        noteDao.insertFolder(folder)
    }
}
