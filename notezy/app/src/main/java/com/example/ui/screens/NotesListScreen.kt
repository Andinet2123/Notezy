package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.NoteEntity
import com.example.ui.components.PinLockDialog
import com.example.ui.theme.IosYellow
import com.example.ui.theme.NoteBodyColor
import com.example.ui.theme.NoteTimeColor
import com.example.ui.theme.NoteTitleColor
import com.example.ui.theme.SearchPlaceholderColor
import com.example.ui.theme.SearchTextColor
import com.example.ui.theme.SpringGreen
import com.example.ui.viewmodel.NotesViewModel
import com.example.ui.viewmodel.ViewMode
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesListScreen(
    viewModel: NotesViewModel,
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToNewNote: () -> Unit
) {
    val notes by viewModel.notes.collectAsState()
    val folders by viewModel.folders.collectAsState()
    val selectedFolderId by viewModel.selectedFolderId.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val viewMode by viewModel.viewMode.collectAsState()

    var showFolderSheet by remember { mutableStateOf(false) }
    var noteToUnlock by remember { mutableStateOf<NoteEntity?>(null) }
    var filterTag by remember { mutableStateOf("all") } // all, pinned, locked, checklist, sketch

    val currentFolder = folders.find { it.id == selectedFolderId }
    val currentFolderName = when (selectedFolderId) {
        "all" -> "All Notes"
        "archive" -> "Archive"
        "trash" -> "Trash"
        else -> currentFolder?.name ?: "Notes"
    }

    val pinnedNotes = notes.filter { it.isPinned }
    val otherNotes = notes.filter { !it.isPinned }

    // Lock PIN Dialog prompt when opening a locked note
    noteToUnlock?.let { note ->
        PinLockDialog(
            targetPin = note.pinCode,
            onDismiss = { noteToUnlock = null },
            onPinSuccess = {
                viewModel.unlockNote(note.id)
                noteToUnlock = null
                onNavigateToDetail(note.id)
            }
        )
    }

    if (showFolderSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFolderSheet = false },
            sheetState = rememberModalBottomSheetState()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = "Folders",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // "All Notes" item
                FolderSheetItem(
                    name = "All Notes",
                    iconName = "folder",
                    isSelected = selectedFolderId == "all",
                    onClick = {
                        viewModel.selectFolder("all")
                        showFolderSheet = false
                    }
                )

                // "Archive" item
                FolderSheetItem(
                    name = "Archive",
                    iconName = "archive",
                    isSelected = selectedFolderId == "archive",
                    onClick = {
                        viewModel.selectFolder("archive")
                        showFolderSheet = false
                    }
                )

                folders.forEach { folder ->
                    FolderSheetItem(
                        name = folder.name,
                        iconName = folder.iconName,
                        isSelected = selectedFolderId == folder.id,
                        onClick = {
                            viewModel.selectFolder(folder.id)
                            showFolderSheet = false
                        }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("notes_list_screen"),
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToNewNote,
                containerColor = IosYellow,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier
                    .shadow(8.dp, CircleShape)
                    .testTag("create_note_fab")
            ) {
                Icon(
                    imageVector = Icons.Default.Create,
                    contentDescription = "New Note",
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Top Bar & iOS Collapsible Title Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 28.dp, bottom = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Folder Selector Pill Button
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { showFolderSheet = true }
                            .testTag("folder_selector_pill"),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Folder,
                                contentDescription = "Folder",
                                tint = IosYellow,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = currentFolderName,
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    // View Mode Toggle (List vs Grid)
                    IconButton(
                        onClick = { viewModel.toggleViewMode() },
                        modifier = Modifier.testTag("toggle_view_mode_btn")
                    ) {
                        Icon(
                            imageVector = if (viewMode == ViewMode.LIST) Icons.Default.GridView else Icons.Default.List,
                            contentDescription = "Toggle View Mode",
                            tint = IosYellow
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Large Bold Header
                Text(
                    text = currentFolderName,
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = { Text("Search notes or checklists...", fontSize = 15.sp, color = SearchPlaceholderColor) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = SearchPlaceholderColor
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear search",
                                    tint = SearchPlaceholderColor
                                )
                            }
                        }
                    },
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 15.sp,
                        color = SearchTextColor
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("notes_search_input"),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = SearchTextColor,
                        unfocusedTextColor = SearchTextColor,
                        focusedPlaceholderColor = SearchPlaceholderColor,
                        unfocusedPlaceholderColor = SearchPlaceholderColor,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedBorderColor = IosYellow,
                        unfocusedBorderColor = Color.Transparent,
                        cursorColor = SearchTextColor
                    ),
                    singleLine = true
                )
            }

            // Notes List / Grid
            if (notes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        shape = RoundedCornerShape(28.dp),
                        color = MaterialTheme.colorScheme.surface,
                        shadowElevation = 2.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_empty_notes),
                                contentDescription = "No Notes",
                                modifier = Modifier
                                    .size(160.dp)
                                    .clip(RoundedCornerShape(24.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            val emptyTitle = when {
                                searchQuery.isNotEmpty() -> "No matching notes found"
                                selectedFolderId == "archive" -> "No Archived Notes"
                                selectedFolderId == "trash" -> "Trash is Empty"
                                else -> "No notes yet"
                            }
                            val emptySubtitle = when {
                                searchQuery.isNotEmpty() -> "Try searching with a different keyword or filter"
                                selectedFolderId == "archive" -> "Archived notes will appear here. Archive notes to keep your main list tidy."
                                selectedFolderId == "trash" -> "Deleted notes will appear here."
                                else -> "Capture ideas, checklists, voice memos, or sketches by tapping the pencil button below."
                            }

                            Text(
                                text = emptyTitle,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 22.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = emptySubtitle,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                textAlign = TextAlign.Center,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }
            } else if (viewMode == ViewMode.LIST) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (pinnedNotes.isNotEmpty()) {
                        item {
                            SectionHeader(title = "PINNED")
                        }
                        items(pinnedNotes, key = { it.id }) { note ->
                            NoteCard(
                                note = note,
                                isUnlocked = viewModel.isNoteUnlocked(note.id),
                                onClick = {
                                    if (note.isLocked && !viewModel.isNoteUnlocked(note.id)) {
                                        noteToUnlock = note
                                    } else {
                                        onNavigateToDetail(note.id)
                                    }
                                },
                                onTogglePin = { viewModel.togglePin(note.id, note.isPinned) },
                                onToggleArchive = { viewModel.toggleArchive(note.id, note.isArchived) },
                                onDelete = { viewModel.moveToTrash(note.id) }
                            )
                        }
                    }

                    if (otherNotes.isNotEmpty()) {
                        item {
                            SectionHeader(title = if (pinnedNotes.isNotEmpty()) "NOTES" else "ALL NOTES")
                        }
                        items(otherNotes, key = { it.id }) { note ->
                            NoteCard(
                                note = note,
                                isUnlocked = viewModel.isNoteUnlocked(note.id),
                                onClick = {
                                    if (note.isLocked && !viewModel.isNoteUnlocked(note.id)) {
                                        noteToUnlock = note
                                    } else {
                                        onNavigateToDetail(note.id)
                                    }
                                },
                                onTogglePin = { viewModel.togglePin(note.id, note.isPinned) },
                                onToggleArchive = { viewModel.toggleArchive(note.id, note.isArchived) },
                                onDelete = { viewModel.moveToTrash(note.id) }
                            )
                        }
                    }

                    // Footer count bar
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${notes.size} ${if (notes.size == 1) "Note" else "Notes"}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                // Grid View
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(notes, key = { it.id }) { note ->
                        NoteCardGrid(
                            note = note,
                            isUnlocked = viewModel.isNoteUnlocked(note.id),
                            onClick = {
                                if (note.isLocked && !viewModel.isNoteUnlocked(note.id)) {
                                    noteToUnlock = note
                                } else {
                                    onNavigateToDetail(note.id)
                                }
                            },
                            onTogglePin = { viewModel.togglePin(note.id, note.isPinned) },
                            onToggleArchive = { viewModel.toggleArchive(note.id, note.isArchived) },
                            onDelete = { viewModel.moveToTrash(note.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        ),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(start = 4.dp, top = 8.dp, bottom = 4.dp)
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteCard(
    note: NoteEntity,
    isUnlocked: Boolean,
    onClick: () -> Unit,
    onTogglePin: () -> Unit,
    onToggleArchive: () -> Unit = {},
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val formattedDate = formatDate(note.updatedAt)

    val cardBg = if (note.colorHex != null) {
        try {
            Color(android.graphics.Color.parseColor(note.colorHex))
        } catch (e: Exception) {
            MaterialTheme.colorScheme.surface
        }
    } else {
        MaterialTheme.colorScheme.surface
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .combinedClickable(
                onClick = onClick,
                onLongClick = { showMenu = true }
            )
            .testTag("note_item_card_${note.id}"),
        shape = RoundedCornerShape(16.dp),
        color = cardBg,
        tonalElevation = 2.dp
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (note.isLocked && !isUnlocked) "Locked Note 🔒" else (note.title.ifBlank { "Untitled Note" }),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = NoteTitleColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (note.isPinned) {
                            Icon(
                                imageVector = Icons.Default.PushPin,
                                contentDescription = "Pinned",
                                tint = IosYellow,
                                modifier = Modifier
                                    .size(18.dp)
                                    .padding(end = 4.dp)
                            )
                        }
                        if (note.isLocked) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Locked",
                                tint = NoteBodyColor,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                if (note.isLocked && !isUnlocked) {
                    Text(
                        text = "Passcode required to view contents",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = NoteBodyColor.copy(alpha = 0.8f)
                    )
                } else {
                    if (note.content.isNotBlank()) {
                        Text(
                            text = note.content,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Normal),
                            color = NoteBodyColor,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Feature Indicators
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = formattedDate,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = NoteTimeColor
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (!note.checklistsJson.isNullOrBlank()) {
                                Icon(
                                    imageVector = Icons.Default.Checklist,
                                    contentDescription = "Checklist",
                                    tint = IosYellow,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            if (!note.drawingData.isNullOrBlank()) {
                                Icon(
                                    imageVector = Icons.Default.Create,
                                    contentDescription = "Sketch",
                                    tint = IosYellow,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            if (note.audioDurationSec != null) {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = "Voice Memo",
                                    tint = IosYellow,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Quick Actions Dropdown Menu
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text(if (note.isPinned) "Unpin Note" else "Pin Note") },
                    onClick = {
                        showMenu = false
                        onTogglePin()
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.PushPin,
                            contentDescription = "Pin"
                        )
                    }
                )
                DropdownMenuItem(
                    text = { Text(if (note.isArchived) "Unarchive Note" else "Archive Note") },
                    onClick = {
                        showMenu = false
                        onToggleArchive()
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Archive,
                            contentDescription = "Archive"
                        )
                    }
                )
                DropdownMenuItem(
                    text = { Text("Move to Trash") },
                    onClick = {
                        showMenu = false
                        if (note.isLocked) {
                            Toast.makeText(context, "Locked notes cannot be deleted. Unlock the note first.", Toast.LENGTH_SHORT).show()
                        } else {
                            onDelete()
                        }
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = if (note.isLocked) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f) else MaterialTheme.colorScheme.error
                        )
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteCardGrid(
    note: NoteEntity,
    isUnlocked: Boolean,
    onClick: () -> Unit,
    onTogglePin: () -> Unit = {},
    onToggleArchive: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    var showMenu by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val cardBg = if (note.colorHex != null) {
        try {
            Color(android.graphics.Color.parseColor(note.colorHex))
        } catch (e: Exception) {
            MaterialTheme.colorScheme.surface
        }
    } else {
        MaterialTheme.colorScheme.surface
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clip(RoundedCornerShape(16.dp))
            .combinedClickable(
                onClick = onClick,
                onLongClick = { showMenu = true }
            ),
        shape = RoundedCornerShape(16.dp),
        color = cardBg,
        tonalElevation = 2.dp
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (note.isLocked && !isUnlocked) "Locked 🔒" else (note.title.ifBlank { "Untitled" }),
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = NoteTitleColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        if (note.isPinned) {
                            Icon(
                                imageVector = Icons.Default.PushPin,
                                contentDescription = "Pinned",
                                tint = IosYellow,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    if (!note.isLocked || isUnlocked) {
                        Text(
                            text = note.content,
                            style = MaterialTheme.typography.bodySmall,
                            color = NoteBodyColor,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Text(
                    text = formatDate(note.updatedAt),
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = NoteTimeColor
                )
            }

            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text(if (note.isPinned) "Unpin Note" else "Pin Note") },
                    onClick = {
                        showMenu = false
                        onTogglePin()
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.PushPin,
                            contentDescription = "Pin"
                        )
                    }
                )
                DropdownMenuItem(
                    text = { Text(if (note.isArchived) "Unarchive Note" else "Archive Note") },
                    onClick = {
                        showMenu = false
                        onToggleArchive()
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Archive,
                            contentDescription = "Archive"
                        )
                    }
                )
                DropdownMenuItem(
                    text = { Text("Move to Trash") },
                    onClick = {
                        showMenu = false
                        if (note.isLocked) {
                            Toast.makeText(context, "Locked notes cannot be deleted. Unlock the note first.", Toast.LENGTH_SHORT).show()
                        } else {
                            onDelete()
                        }
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = if (note.isLocked) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f) else MaterialTheme.colorScheme.error
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun FolderSheetItem(
    name: String,
    iconName: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) IosYellow.copy(alpha = 0.15f) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Folder,
            contentDescription = name,
            tint = if (isSelected) IosYellow else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.titleMedium,
            color = if (isSelected) IosYellow else MaterialTheme.colorScheme.onSurface
        )
    }
}

private fun formatDate(timestamp: Long): String {
    val date = Date(timestamp)
    val now = Date()
    val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
    return if (sdf.format(date) == sdf.format(now)) {
        SimpleDateFormat("h:mm a", Locale.getDefault()).format(date)
    } else {
        SimpleDateFormat("MMM d", Locale.getDefault()).format(date)
    }
}
