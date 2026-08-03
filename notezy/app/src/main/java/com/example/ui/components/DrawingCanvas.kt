package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.IosYellow

data class PathData(
    val path: Path,
    val color: Color,
    val strokeWidth: Float,
    val isEraser: Boolean = false
)

@Composable
fun DrawingCanvas(
    modifier: Modifier = Modifier,
    initialPaths: List<PathData> = emptyList(),
    onClose: () -> Unit,
    onSaveDrawing: (String) -> Unit
) {
    val paths = remember { mutableStateListOf<PathData>().apply { addAll(initialPaths) } }
    var currentPath by remember { mutableStateOf<Path?>(null) }
    var currentColor by remember { mutableStateOf(Color.Black) }
    var currentStrokeWidth by remember { mutableFloatStateOf(8f) }
    var isEraser by remember { mutableStateOf(false) }

    val colors = listOf(
        Color.Black,
        IosYellow,
        Color(0xFFE53935), // Red
        Color(0xFF1E88E5), // Blue
        Color(0xFF43A047), // Green
        Color(0xFF8E24AA), // Purple
        Color(0xFFFB8C00)  // Orange
    )

    Surface(
        modifier = modifier
            .fillMaxSize()
            .testTag("drawing_canvas_screen"),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onClose,
                    modifier = Modifier.testTag("cancel_drawing_btn")
                ) {
                    Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Create,
                        contentDescription = "Canvas Sketch",
                        tint = IosYellow,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Sketch Canvas",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                TextButton(
                    onClick = {
                        onSaveDrawing("has_drawing_${paths.size}")
                        onClose()
                    },
                    modifier = Modifier.testTag("done_drawing_btn")
                ) {
                    Text("Done", color = IosYellow, style = MaterialTheme.typography.titleMedium)
                }
            }

            // Interactive Canvas Board
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .shadow(4.dp, RoundedCornerShape(20.dp))
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFFAFAFA))
                    .pointerInput(isEraser, currentColor, currentStrokeWidth) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                val p = Path().apply { moveTo(offset.x, offset.y) }
                                currentPath = p
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                currentPath?.let { p ->
                                    val last = change.position
                                    p.lineTo(last.x, last.y)
                                    // Trigger recomposition
                                    currentPath = Path().apply { addPath(p) }
                                }
                            },
                            onDragEnd = {
                                currentPath?.let { p ->
                                    paths.add(
                                        PathData(
                                            path = p,
                                            color = if (isEraser) Color(0xFFFAFAFA) else currentColor,
                                            strokeWidth = if (isEraser) currentStrokeWidth * 2.5f else currentStrokeWidth,
                                            isEraser = isEraser
                                        )
                                    )
                                    currentPath = null
                                }
                            }
                        )
                    }
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Draw committed paths
                    paths.forEach { pathData ->
                        drawPath(
                            path = pathData.path,
                            color = pathData.color,
                            style = Stroke(
                                width = pathData.strokeWidth,
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    }
                    // Draw active path
                    currentPath?.let { activePath ->
                        drawPath(
                            path = activePath,
                            color = if (isEraser) Color(0xFFFAFAFA) else currentColor,
                            style = Stroke(
                                width = if (isEraser) currentStrokeWidth * 2.5f else currentStrokeWidth,
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    }
                }
            }

            // Toolbar Controls
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Color Palette
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        colors.forEach { color ->
                            val isSelected = !isEraser && currentColor == color
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(
                                        width = if (isSelected) 3.dp else 1.dp,
                                        color = if (isSelected) IosYellow else Color.Gray.copy(alpha = 0.3f),
                                        shape = CircleShape
                                    )
                                    .clickable {
                                        currentColor = color
                                        isEraser = false
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = if (color == Color.Black || color == Color(0xFF8E24AA)) Color.White else Color.Black,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        // Eraser Tool
                        IconButton(
                            onClick = { isEraser = !isEraser },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(if (isEraser) IosYellow else MaterialTheme.colorScheme.surface)
                                .testTag("eraser_tool")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoFixHigh,
                                contentDescription = "Eraser",
                                tint = if (isEraser) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // Undo Tool
                        IconButton(
                            onClick = {
                                if (paths.isNotEmpty()) paths.removeAt(paths.size - 1)
                            },
                            enabled = paths.isNotEmpty(),
                            modifier = Modifier.testTag("undo_sketch_btn")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Undo,
                                contentDescription = "Undo",
                                tint = if (paths.isNotEmpty()) MaterialTheme.colorScheme.onSurface else Color.Gray
                            )
                        }

                        // Clear All Tool
                        IconButton(
                            onClick = { paths.clear() },
                            enabled = paths.isNotEmpty(),
                            modifier = Modifier.testTag("clear_sketch_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Clear All",
                                tint = if (paths.isNotEmpty()) MaterialTheme.colorScheme.error else Color.Gray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Stroke Size Slider
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isEraser) "Eraser Size" else "Line Width",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Slider(
                            value = currentStrokeWidth,
                            onValueChange = { currentStrokeWidth = it },
                            valueRange = 4f..32f,
                            modifier = Modifier.weight(1f),
                            colors = SliderDefaults.colors(
                                thumbColor = IosYellow,
                                activeTrackColor = IosYellow
                            )
                        )
                    }
                }
            }
        }
    }
}
