package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ChecklistItem
import com.example.ui.theme.DeepOnyx
import com.example.ui.theme.IosYellow
import com.example.ui.theme.NoteBodyColor
import com.example.ui.theme.NoteTitleColor
import com.example.ui.theme.SearchPlaceholderColor

@Composable
fun ChecklistEditor(
    modifier: Modifier = Modifier,
    items: List<ChecklistItem>,
    onItemsChange: (List<ChecklistItem>) -> Unit
) {
    var newItemText by remember { mutableStateOf("") }
    val completedCount = items.count { it.isDone }
    val totalCount = items.size
    val progress = if (totalCount > 0) completedCount.toFloat() / totalCount.toFloat() else 0f
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "progress")

    Column(modifier = modifier.fillMaxWidth()) {
        if (totalCount > 0) {
            // Checklist Header & Progress Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Checklist ($completedCount/$totalCount)",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = IosYellow
                )
            }

            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = IosYellow,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))
        }

        // Checklist Items List
        items.forEachIndexed { index, item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // iOS-style Circular Checkbox
                val checkColor by animateColorAsState(
                    targetValue = if (item.isDone) IosYellow else Color.Transparent,
                    label = "checkColor"
                )

                Surface(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .clickable {
                            val updated = items.toMutableList()
                            updated[index] = item.copy(isDone = !item.isDone)
                            onItemsChange(updated)
                        }
                        .testTag("checklist_checkbox_$index"),
                    shape = CircleShape,
                    color = checkColor,
                    border = if (!item.isDone) androidx.compose.foundation.BorderStroke(
                        1.5.dp,
                        MaterialTheme.colorScheme.outline
                    ) else null
                ) {
                    if (item.isDone) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Completed",
                            tint = Color.White,
                            modifier = Modifier.padding(3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Item Text Field with animated strikethrough line
                val animatedItemTextColor by animateColorAsState(
                    targetValue = if (item.isDone) NoteBodyColor.copy(alpha = 0.6f) else NoteTitleColor,
                    animationSpec = tween(durationMillis = 250),
                    label = "textColor"
                )
                val strikethroughProgress by animateFloatAsState(
                    targetValue = if (item.isDone) 1f else 0f,
                    animationSpec = tween(durationMillis = 300),
                    label = "strikethrough"
                )

                TextField(
                    value = item.text,
                    onValueChange = { newText ->
                        val updated = items.toMutableList()
                        updated[index] = item.copy(text = newText)
                        onItemsChange(updated)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .drawWithContent {
                            drawContent()
                            if (strikethroughProgress > 0f) {
                                val strokeWidth = 2.dp.toPx()
                                val y = size.height / 2f
                                drawLine(
                                    color = animatedItemTextColor,
                                    start = Offset(0f, y),
                                    end = Offset(size.width * strikethroughProgress, y),
                                    strokeWidth = strokeWidth
                                )
                            }
                        },
                    textStyle = TextStyle(
                        fontSize = 16.sp,
                        color = animatedItemTextColor
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = animatedItemTextColor,
                        unfocusedTextColor = animatedItemTextColor,
                        cursorColor = DeepOnyx,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true
                )

                // Remove Item Button
                IconButton(
                    onClick = {
                        val updated = items.toMutableList()
                        updated.removeAt(index)
                        onItemsChange(updated)
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Delete item",
                        tint = SearchPlaceholderColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Add New Checklist Item Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add item",
                tint = IosYellow,
                modifier = Modifier.size(22.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            TextField(
                value = newItemText,
                onValueChange = { newItemText = it },
                placeholder = {
                    Text(
                        "Add checklist item...",
                        color = SearchPlaceholderColor
                    )
                },
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    color = NoteTitleColor
                ),
                modifier = Modifier
                    .weight(1f)
                    .testTag("new_checklist_input"),
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
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    if (newItemText.isNotBlank()) {
                        val updated = items.toMutableList()
                        updated.add(ChecklistItem(text = newItemText.trim(), isDone = false))
                        onItemsChange(updated)
                        newItemText = ""
                    }
                }),
                singleLine = true
            )
        }
    }
}
