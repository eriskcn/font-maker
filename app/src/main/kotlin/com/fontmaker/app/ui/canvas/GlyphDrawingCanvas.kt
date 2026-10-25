package com.fontmaker.app.ui.canvas

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fontmaker.core.model.GlyphPoint
import com.fontmaker.core.model.RawStroke

data class CanvasMetrics(
    val baselineRatio: Float = 0.75f,
    val capHeightRatio: Float = 0.25f,
    val xHeightRatio: Float = 0.50f,
    val ascenderRatio: Float = 0.15f,
    val descenderRatio: Float = 0.90f
)

@Composable
fun GlyphDrawingCanvas(
    modifier: Modifier = Modifier,
    ghostChar: String = "",
    metrics: CanvasMetrics = CanvasMetrics(),
    strokes: MutableList<RawStroke>,
    currentStrokeWidth: Float = 14f,
    onStrokesChanged: () -> Unit = {}
) {
    val activeStrokePoints = remember { mutableStateListOf<GlyphPoint>() }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFFCFCFD))
            .border(1.5.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        activeStrokePoints.clear()
                        activeStrokePoints.add(
                            GlyphPoint(
                                x = offset.x / size.width,
                                y = offset.y / size.height
                            )
                        )
                    },
                    onDrag = { change, _ ->
                        change.consume()
                        activeStrokePoints.add(
                            GlyphPoint(
                                x = change.position.x / size.width,
                                y = change.position.y / size.height
                            )
                        )
                    },
                    onDragEnd = {
                        if (activeStrokePoints.isNotEmpty()) {
                            strokes.add(
                                RawStroke(
                                    points = activeStrokePoints.toList(),
                                    strokeWidth = currentStrokeWidth
                                )
                            )
                            activeStrokePoints.clear()
                            onStrokesChanged()
                        }
                    },
                    onDragCancel = {
                        activeStrokePoints.clear()
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        // Ghost character behind guide lines and user strokes
        if (ghostChar.isNotBlank()) {
            Text(
                text = ghostChar,
                fontSize = 150.sp,
                fontFamily = FontFamily.SansSerif,
                color = Color(0x18000000)
            )
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // --- Guide Lines ---
            val dashEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)

            // Ascender line
            drawLine(
                color = Color(0xFFCFD8DC),
                start = Offset(0f, height * metrics.ascenderRatio),
                end = Offset(width, height * metrics.ascenderRatio),
                strokeWidth = 1.5f,
                pathEffect = dashEffect
            )

            // Cap-height line
            drawLine(
                color = Color(0xFFB0BEC5),
                start = Offset(0f, height * metrics.capHeightRatio),
                end = Offset(width, height * metrics.capHeightRatio),
                strokeWidth = 2f
            )

            // x-Height line
            drawLine(
                color = Color(0xFFECEFF1),
                start = Offset(0f, height * metrics.xHeightRatio),
                end = Offset(width, height * metrics.xHeightRatio),
                strokeWidth = 1.5f,
                pathEffect = dashEffect
            )

            // Baseline (prominent guide)
            drawLine(
                color = Color(0xFF64B5F6),
                start = Offset(0f, height * metrics.baselineRatio),
                end = Offset(width, height * metrics.baselineRatio),
                strokeWidth = 2.5f
            )

            // Descender line
            drawLine(
                color = Color(0xFFCFD8DC),
                start = Offset(0f, height * metrics.descenderRatio),
                end = Offset(width, height * metrics.descenderRatio),
                strokeWidth = 1.5f,
                pathEffect = dashEffect
            )

            // --- Render Saved Strokes ---
            for (stroke in strokes) {
                if (stroke.points.size > 1) {
                    val path = Path()
                    val first = stroke.points.first()
                    path.moveTo(first.x * width, first.y * height)
                    for (i in 1 until stroke.points.size) {
                        val pt = stroke.points[i]
                        path.lineTo(pt.x * width, pt.y * height)
                    }
                    drawPath(
                        path = path,
                        color = Color(0xFF1E293B),
                        style = Stroke(
                            width = stroke.strokeWidth,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }
            }

            // --- Render Active Stroke ---
            if (activeStrokePoints.size > 1) {
                val path = Path()
                val first = activeStrokePoints.first()
                path.moveTo(first.x * width, first.y * height)
                for (i in 1 until activeStrokePoints.size) {
                    val pt = activeStrokePoints[i]
                    path.lineTo(pt.x * width, pt.y * height)
                }
                drawPath(
                    path = path,
                    color = Color(0xFF334155),
                    style = Stroke(
                        width = currentStrokeWidth,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }
        }
    }
}
