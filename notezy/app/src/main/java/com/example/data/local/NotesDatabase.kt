package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [NoteEntity::class, FolderEntity::class], version = 2, exportSchema = false)
abstract class NotesDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDao

    companion object {
        @Volatile
        private var INSTANCE: NotesDatabase? = null

        fun getDatabase(context: Context): NotesDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NotesDatabase::class.java,
                    "notes_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback(context))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(private val context: Context) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialData(database.noteDao())
                    }
                }
            }
        }

        private suspend fun populateInitialData(dao: NoteDao) {
            // Populate Folders
            val defaultFolders = listOf(
                FolderEntity("notes", "Notes", "folder", true, 0),
                FolderEntity("quick_notes", "Quick Notes", "flash_on", true, 1),
                FolderEntity("work", "Work & Projects", "work", false, 2),
                FolderEntity("personal", "Personal", "person", false, 3),
                FolderEntity("ideas", "Ideas & Drafts", "lightbulb", false, 4),
                FolderEntity("vault", "Locked Vault", "lock", true, 5)
            )
            for (folder in defaultFolders) {
                dao.insertFolder(folder)
            }

            // Populate Initial Notes (iOS Notes Style initial experience)
            val now = System.currentTimeMillis()
            val sampleNotes = listOf(
                NoteEntity(
                    id = 1,
                    title = "Welcome to Notes 📝",
                    content = "Experience the smoothest iOS-inspired notes app on Android!\n\n✨ Features to explore:\n• Interactive Checklists with animated strikethroughs\n• Freehand Sketching & Canvas Drawings\n• Voice Memo Attachments with live playback\n• Password Protected Vault Notes\n• Folders & Fast Search filtering\n• Custom Pastel Note Themes",
                    folderId = "notes",
                    isPinned = true,
                    isLocked = false,
                    updatedAt = now,
                    createdAt = now,
                    colorHex = "#FFF9E6",
                    checklistsJson = """[{"id":"1","text":"Try toggling a checklist item","isDone":true},{"id":"2","text":"Create your first sketch note","isDone":false},{"id":"3","text":"Lock a sensitive note in Vault","isDone":false}]"""
                ),
                NoteEntity(
                    id = 2,
                    title = "Weekend Shopping & Meal Plan 🛒",
                    content = "Things to pick up from the organic farmer's market this Saturday morning.",
                    folderId = "quick_notes",
                    isPinned = true,
                    isLocked = false,
                    updatedAt = now - 3600000,
                    createdAt = now - 86400000,
                    colorHex = "#E8F5E9",
                    checklistsJson = """[{"id":"10","text":"Fresh sourdough bread","isDone":true},{"id":"11","text":"Organic avocado & lime","isDone":true},{"id":"12","text":"Greek yogurt & granola","isDone":false},{"id":"13","text":"Wildflower honey","isDone":false}]"""
                ),
                NoteEntity(
                    id = 3,
                    title = "Mobile App Design System Ideas 💡",
                    content = "Design system guidelines inspired by sleek minimalist aesthetics. Focus on fluid gesture feedback, high contrast typography, and gentle tactile ripples.",
                    folderId = "work",
                    isPinned = false,
                    isLocked = false,
                    updatedAt = now - 7200000,
                    createdAt = now - 172800000,
                    colorHex = "#E3F2FD",
                    drawingData = "sample_canvas"
                ),
                NoteEntity(
                    id = 4,
                    title = "Personal Vault & Confidential 🔒",
                    content = "This note is secured with a PIN lock. Passcode for demo: 1234.\n\nKeep private journal entries, account access logs, and important passcodes safely protected.",
                    folderId = "vault",
                    isPinned = false,
                    isLocked = true,
                    pinCode = "1234",
                    updatedAt = now - 14400000,
                    createdAt = now - 259200000,
                    colorHex = "#F3E5F5"
                ),
                NoteEntity(
                    id = 5,
                    title = "Voice Memo: Team Huddle Notes 🎙️",
                    content = "Key takeaways from morning sync meeting: finalized sprint goals and scheduled user testing sessions.",
                    folderId = "work",
                    isPinned = false,
                    isLocked = false,
                    updatedAt = now - 28800000,
                    createdAt = now - 345600000,
                    audioDurationSec = 42,
                    audioTitle = "Voice Memo 01",
                    colorHex = "#FFF3E0"
                )
            )

            for (note in sampleNotes) {
                dao.insertNote(note)
            }
        }
    }
}
