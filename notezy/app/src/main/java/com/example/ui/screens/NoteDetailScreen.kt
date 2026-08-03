package com.example.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ChecklistItem
import com.example.data.local.NoteEntity
import com.example.ui.components.AudioRecorderPlayer
import com.example.ui.components.ChecklistEditor
import com.example.ui.components.DrawingCanvas
import com.example.ui.components.PinLockDialog
import com.example.ui.theme.DeepOnyx
import com.example.ui.theme.IosYellow
import com.example.ui.theme.NoteBodyColor
import com.example.ui.theme.NoteTimeColor
import com.example.ui.theme.NoteTitleColor
import com.example.ui.theme.SearchPlaceholderColor
import com.example.ui.viewmodel.NotesViewModel
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(
    noteId: Long,
    viewModel: NotesViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    var currentNoteId by remember { mutableStateOf(noteId) }
    var loadedNote by remember { mutableStateOf<NoteEntity?>(null) }
    var isInitialLoaded by remember { mutableStateOf(noteId == 0L) }

    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var isPinned by remember { mutableStateOf(false) }
    var isArchived by remember { mutableStateOf(false) }
    var isLocked by remember { mutableStateOf(false) }
    var pinCode by remember { mutableStateOf("1234") }
    var colorHex by remember { mutableStateOf("#F2F0E4") }
    var folderId by remember { mutableStateOf("notes") }

    var checklists by remember { mutableStateOf<List<ChecklistItem>>(emptyList()) }
    var drawingData by remember { mutableStateOf<String?>(null) }
    var audioDurationSec by remember { mutableStateOf<Int?>(null) }
    var audioTitle by remember { mutableStateOf<String?>(null) }

    var showDrawingCanvas by remember { mutableStateOf(false) }
    var showPinSetupDialog by remember { mutableStateOf(false) }
    var showColorPicker by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }

    // Check if the note has any real content
    fun isNoteEmpty(): Boolean {
        return title.isBlank() &&
               content.isBlank() &&
               drawingData.isNullOrBlank() &&
               audioDurationSec == null &&
               (checklists.isEmpty() || checklists.all { it.text.isBlank() })
    }

    // Load initial note data
    LaunchedEffect(noteId) {
        if (noteId != 0L) {
            viewModel.getNoteById(noteId) { note ->
                note?.let {
                    loadedNote = it
                    title = it.title
                    content = it.content
                    isPinned = it.isPinned
                    isArchived = it.isArchived
                    isLocked = it.isLocked
                    pinCode = it.pinCode
                    colorHex = it.colorHex ?: "#F2F0E4"
                    folderId = it.folderId
                    drawingData = it.drawingData
                    audioDurationSec = it.audioDurationSec
                    audioTitle = it.audioTitle

                    // Parse checklists JSON
                    if (!it.checklistsJson.isNullOrEmpty()) {
                        checklists = parseChecklistsJson(it.checklistsJson)
                    }
                }
                isInitialLoaded = true
            }
        } else {
            // New Note: automatically request focus and open keyboard
            delay(100)
            focusRequester.requestFocus()
            keyboardController?.show()
        }
    }

    // Auto-save function on edit or back navigation
    fun saveCurrentNote() {
        if (isNoteEmpty()) {
            if (currentNoteId != 0L) {
                viewModel.deletePermanently(currentNoteId)
                currentNoteId = 0L
            }
            return
        }

        val jsonChecklist = serializeChecklistsJson(checklists)
        val noteToSave = NoteEntity(
            id = currentNoteId,
            title = title,
            content = content,
            folderId = folderId,
            isPinned = isPinned,
            isArchived = isArchived,
            isLocked = isLocked,
            pinCode = pinCode,
            colorHex = colorHex,
            updatedAt = System.currentTimeMillis(),
            createdAt = loadedNote?.createdAt ?: System.currentTimeMillis(),
            drawingData = drawingData,
            audioDurationSec = audioDurationSec,
            audioTitle = audioTitle,
            checklistsJson = jsonChecklist
        )
        viewModel.saveNote(noteToSave) { newId ->
            if (currentNoteId == 0L) {
                currentNoteId = newId
            }
        }
    }

    // Auto-save in background as user types/edits
    LaunchedEffect(
        title, content, checklists, drawingData, audioDurationSec, audioTitle, colorHex, isPinned, isArchived, isLocked, isInitialLoaded
    ) {
        if (isInitialLoaded) {
            delay(300)
            saveCurrentNote()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            saveCurrentNote()
        }
    }

    val paperColor = try {
        Color(android.graphics.Color.parseColor(colorHex))
    } catch (e: Exception) {
        MaterialTheme.colorScheme.surface
    }

    // Fullscreen Sketch Canvas overlay
    if (showDrawingCanvas) {
        DrawingCanvas(
            onClose = { showDrawingCanvas = false },
            onSaveDrawing = { sketchPath ->
                drawingData = sketchPath
                saveCurrentNote()
            }
        )
        return
    }

    // PIN Setup Dialog for Lock feature
    if (showPinSetupDialog) {
        PinLockDialog(
            isSetupMode = true,
            onDismiss = { showPinSetupDialog = false },
            onPinSuccess = { newPin ->
                pinCode = newPin
                isLocked = true
                showPinSetupDialog = false
                saveCurrentNote()
            }
        )
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("note_detail_screen"),
        containerColor = paperColor,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 8.dp, top = 24.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        saveCurrentNote()
                        onNavigateBack()
                    },
                    modifier = Modifier.testTag("back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = IosYellow
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Pin Toggle Action
                    IconButton(
                        onClick = {
                            isPinned = !isPinned
                            saveCurrentNote()
                        },
                        modifier = Modifier.testTag("pin_note_action")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PushPin,
                            contentDescription = "Pin Note",
                            tint = if (isPinned) IosYellow else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    }

                    // Lock Toggle Action
                    IconButton(
                        onClick = {
                            if (isLocked) {
                                isLocked = false
                            } else {
                                showPinSetupDialog = true
                            }
                        },
                        modifier = Modifier.testTag("lock_note_action")
                    ) {
                        Icon(
                            imageVector = if (isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                            contentDescription = "Lock Note",
                            tint = if (isLocked) IosYellow else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    }

                    // Share Text Action
                    IconButton(
                        onClick = {
                            val shareIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, title)
                                putExtra(Intent.EXTRA_TEXT, "$title\n\n$content")
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share Note"))
                        },
                        modifier = Modifier.testTag("share_note_action")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = IosYellow
                        )
                    }

                    // Options Menu
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More Options",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(if (isArchived) "Unarchive Note" else "Archive Note") },
                                onClick = {
                                    showMenu = false
                                    isArchived = !isArchived
                                    saveCurrentNote()
                                    onNavigateBack()
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
                                    if (isLocked) {
                                        Toast.makeText(context, "Locked notes cannot be deleted. Unlock the note first.", Toast.LENGTH_SHORT).show()
                                    } else {
                                        if (currentNoteId != 0L) {
                                            viewModel.moveToTrash(currentNoteId)
                                        }
                                        onNavigateBack()
                                    }
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = if (isLocked) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f) else MaterialTheme.colorScheme.error
                                    )
                                }
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            // iOS Bottom Editor Toolbar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding(),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                tonalElevation = 6.dp
            ) {
                Column {
                    // Quick Paper Tint Selector bar
                    AnimatedVisibility(visible = showColorPicker) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            val tints = listOf(
                                "#F2F0E4", // Natural Warm Cream
                                "#E8EEF2", // Soft Slate Blue
                                "#FFF7ED", // Warm Peach
                                "#F0FDF4", // Soft Mint
                                "#FAF5FF", // Soft Lavender
                                "#FFFFFF"  // Pure White
                            )
                            tints.forEach { hex ->
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(Color(android.graphics.Color.parseColor(hex)))
                                        .border(
                                            width = if (colorHex == hex) 2.dp else 1.dp,
                                            color = if (colorHex == hex) IosYellow else Color.Gray.copy(
                                                alpha = 0.3f
                                            ),
                                            shape = CircleShape
                                        )
                                        .clickable {
                                            colorHex = hex
                                            saveCurrentNote()
                                        }
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Checklist button
                        IconButton(
                            onClick = {
                                if (checklists.isEmpty()) {
                                    checklists = listOf(ChecklistItem(text = ""))
                                } else {
                                    checklists = checklists + ChecklistItem(text = "")
                                }
                            },
                            modifier = Modifier.testTag("toolbar_add_checklist")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Checklist,
                                contentDescription = "Checklist",
                                tint = IosYellow
                            )
                        }

                        // Sketch Canvas button
                        IconButton(
                            onClick = { showDrawingCanvas = true },
                            modifier = Modifier.testTag("toolbar_open_sketch")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Create,
                                contentDescription = "Sketch",
                                tint = IosYellow
                            )
                        }

                        // Color Picker Toggle
                        IconButton(
                            onClick = { showColorPicker = !showColorPicker },
                            modifier = Modifier.testTag("toolbar_color_picker")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ColorLens,
                                contentDescription = "Paper Color",
                                tint = IosYellow
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
            // Timestamp Header
            Text(
                text = formatFullDate(loadedNote?.updatedAt ?: System.currentTimeMillis()),
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                ),
                color = NoteTimeColor,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Large Title Input
            TextField(
                value = title,
                onValueChange = { title = it },
                placeholder = {
                    Text(
                        "Title",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = SearchPlaceholderColor
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("note_title_input"),
                textStyle = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = NoteTitleColor
                ),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = NoteTitleColor,
                    unfocusedTextColor = NoteTitleColor,
                    focusedPlaceholderColor = SearchPlaceholderColor,
                    unfocusedPlaceholderColor = SearchPlaceholderColor,
                    cursorColor = DeepOnyx,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Body Content Input
            TextField(
                value = content,
                onValueChange = { content = it },
                placeholder = {
                    Text(
                        "Start writing your note here...",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = SearchPlaceholderColor
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .testTag("note_body_input"),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 17.sp,
                    lineHeight = 24.sp,
                    color = NoteBodyColor
                ),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = NoteBodyColor,
                    unfocusedTextColor = NoteBodyColor,
                    focusedPlaceholderColor = SearchPlaceholderColor,
                    unfocusedPlaceholderColor = SearchPlaceholderColor,
                    cursorColor = DeepOnyx,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            // Embedded Checklist Editor
            if (checklists.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                ChecklistEditor(
                    items = checklists,
                    onItemsChange = { updated ->
                        checklists = updated
                        saveCurrentNote()
                    }
                )
            }

            // Embedded Voice Memo Section
            if (audioDurationSec != null) {
                Spacer(modifier = Modifier.height(16.dp))
                AudioRecorderPlayer(
                    audioDurationSec = audioDurationSec,
                    audioTitle = audioTitle ?: "Voice Memo",
                    onAudioSaved = { duration, titleStr ->
                        audioDurationSec = duration
                        audioTitle = titleStr
                        saveCurrentNote()
                    },
                    onAudioDeleted = {
                        audioDurationSec = null
                        audioTitle = null
                        saveCurrentNote()
                    }
                )
            }

            // Embedded Sketch Preview Card
            if (drawingData != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { showDrawingCanvas = true },
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Create,
                                contentDescription = "Sketch Attached",
                                tint = IosYellow,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Attached Sketch (Tap to Edit Canvas)",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(64.dp))
        }
    }
}

private fun formatFullDate(timestamp: Long): String {
    return SimpleDateFormat("MMMM d, yyyy 'at' h:mm a", Locale.getDefault()).format(Date(timestamp))
}

private fun parseChecklistsJson(json: String?): List<ChecklistItem> {
    if (json.isNullOrBlank()) return emptyList()
    return try {
        val moshi = Moshi.Builder().build()
        val type = Types.newParameterizedType(List::class.java, ChecklistItem::class.java)
        val adapter = moshi.adapter<List<ChecklistItem>>(type)
        adapter.fromJson(json) ?: emptyList()
    } catch (e: Exception) {
        emptyList()
    }
}

private fun serializeChecklistsJson(items: List<ChecklistItem>): String? {
    if (items.isEmpty()) return null
    return try {
        val moshi = Moshi.Builder().build()
        val type = Types.newParameterizedType(List::class.java, ChecklistItem::class.java)
        val adapter = moshi.adapter<List<ChecklistItem>>(type)
        adapter.toJson(items)
    } catch (e: Exception) {
        null
    }
}
