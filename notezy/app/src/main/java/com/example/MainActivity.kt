package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.screens.NoteDetailScreen
import com.example.ui.screens.NotesListScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.NotesViewModel

class MainActivity : ComponentActivity() {

    private val notesViewModel: NotesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    NotesAppNavigation(notesViewModel = notesViewModel)
                }
            }
        }
    }
}

@Composable
fun NotesAppNavigation(notesViewModel: NotesViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "notes_list",
        enterTransition = { fadeIn() },
        exitTransition = { fadeOut() }
    ) {
        composable("notes_list") {
            NotesListScreen(
                viewModel = notesViewModel,
                onNavigateToDetail = { noteId ->
                    navController.navigate("note_detail/$noteId")
                },
                onNavigateToNewNote = {
                    navController.navigate("note_detail/0")
                }
            )
        }

        composable(
            route = "note_detail/{noteId}",
            arguments = listOf(navArgument("noteId") { type = NavType.LongType })
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getLong("noteId") ?: 0L
            NoteDetailScreen(
                noteId = noteId,
                viewModel = notesViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

