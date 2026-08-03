package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.FolderEntity
import com.example.data.local.NoteEntity
import com.example.data.local.NotesDatabase
import com.example.repository.NotesRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ViewMode {
    LIST, GRID
}

class NotesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: NotesRepository

    init {
        val noteDao = NotesDatabase.getDatabase(application).noteDao()
        repository = NotesRepository(noteDao)
    }

    private val _selectedFolderId = MutableStateFlow("all")
    val selectedFolderId: StateFlow<String> = _selectedFolderId.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _viewMode = MutableStateFlow(ViewMode.LIST)
    val viewMode: StateFlow<ViewMode> = _viewMode.asStateFlow()

    private val _unlockedNoteIds = MutableStateFlow<Set<Long>>(emptySet())
    val unlockedNoteIds: StateFlow<Set<Long>> = _unlockedNoteIds.asStateFlow()

    val folders: StateFlow<List<FolderEntity>> = repository.folders
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val notes: StateFlow<List<NoteEntity>> = combine(
        _selectedFolderId,
        _searchQuery
    ) { folderId, query ->
        Pair(folderId, query)
    }.flatMapLatest { (folderId, query) ->
        if (query.isNotBlank()) {
            repository.searchNotes(query)
        } else if (folderId == "archive") {
            repository.archivedNotes
        } else if (folderId == "trash") {
            repository.trashNotes
        } else {
            repository.getNotesByFolder(folderId)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun selectFolder(folderId: String) {
        _selectedFolderId.value = folderId
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleViewMode() {
        _viewMode.value = if (_viewMode.value == ViewMode.LIST) ViewMode.GRID else ViewMode.LIST
    }

    fun togglePin(noteId: Long, currentPinStatus: Boolean) {
        viewModelScope.launch {
            repository.togglePin(noteId, !currentPinStatus)
        }
    }

    fun toggleLock(noteId: Long, isLocked: Boolean, pinCode: String = "1234") {
        viewModelScope.launch {
            repository.toggleLock(noteId, isLocked, pinCode)
            if (!isLocked) {
                // Remove from unlocked set if locked
                _unlockedNoteIds.value = _unlockedNoteIds.value - noteId
            }
        }
    }

    fun unlockNote(noteId: Long) {
        _unlockedNoteIds.value = _unlockedNoteIds.value + noteId
    }

    fun isNoteUnlocked(noteId: Long): Boolean {
        return _unlockedNoteIds.value.contains(noteId)
    }

    fun moveToTrash(noteId: Long, onLocked: (() -> Unit)? = null) {
        viewModelScope.launch {
            val note = repository.getNoteByIdDirect(noteId)
            if (note?.isLocked == true) {
                onLocked?.invoke()
                return@launch
            }
            repository.moveToTrash(noteId, true)
        }
    }

    fun toggleArchive(noteId: Long, currentArchiveStatus: Boolean) {
        viewModelScope.launch {
            repository.toggleArchive(noteId, !currentArchiveStatus)
        }
    }

    fun restoreFromTrash(noteId: Long) {
        viewModelScope.launch {
            repository.moveToTrash(noteId, false)
        }
    }

    fun deletePermanently(noteId: Long, onLocked: (() -> Unit)? = null) {
        viewModelScope.launch {
            val note = repository.getNoteByIdDirect(noteId)
            if (note?.isLocked == true) {
                onLocked?.invoke()
                return@launch
            }
            repository.deleteNoteById(noteId)
        }
    }

    fun saveNote(note: NoteEntity, onSaved: (Long) -> Unit = {}) {
        viewModelScope.launch {
            val noteId = repository.insertNote(note)
            onSaved(if (note.id == 0L) noteId else note.id)
        }
    }

    fun createFolder(name: String) {
        viewModelScope.launch {
            repository.addFolder(name)
        }
    }

    fun getNoteById(id: Long, onResult: (NoteEntity?) -> Unit) {
        viewModelScope.launch {
            val note = repository.getNoteByIdDirect(id)
            onResult(note)
        }
    }
}
