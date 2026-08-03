package com.example.data.local

data class ChecklistItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    var text: String = "",
    var isDone: Boolean = false
)
