package com.fontmaker.app.ui.preview

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fontmaker.core.model.RawStroke
import com.fontmaker.core.vietnamese.DiacriticType
import com.fontmaker.core.vietnamese.VietnameseCompositionTable
import com.fontmaker.core.vietnamese.VietnameseRepertoire
import com.fontmaker.core.vietnamese.VietnameseTestCorpus
import kotlin.math.max

fun getDiacriticKey(type: DiacriticType): String = when (type) {
    DiacriticType.ACUTE -> "acute"
    DiacriticType.GRAVE -> "grave"
    DiacriticType.HOOK_ABOVE -> "hook"
    DiacriticType.TILDE -> "tilde"
    DiacriticType.DOT_BELOW -> "dot"
    DiacriticType.CIRCUMFLEX -> "circumflex"
    DiacriticType.BREVE -> "breve"
    DiacriticType.HORN -> "horn"
    DiacriticType.STROKE -> "acute"
}

// Proportional width factor for natural text spacing
fun getCharacterWidthFactor(ch: Char): Float = when (ch) {
    'i', 'l', '!', '|', ':', ';', '\'', '.', ' ' -> 0.45f
    'm', 'w', 'M', 'W', '@', '%' -> 1.15f
    'r', 't', 'f', '(', ')', '[', ']', '-', 'j' -> 0.65f
    in 'A'..'Z', 'Đ' -> 0.95f
    else -> 0.80f
}

@Composable
fun NaturalGlyph(
    ch: Char,
    capturedGlyphs: Map<String, List<RawStroke>>,
    fontSizeDp: Dp = 28.dp
) {
    val factor = getCharacterWidthFactor(ch)
    val charWidth = fontSizeDp * factor

    if (ch == ' ') {
        Spacer(modifier = Modifier.width(charWidth))
        return
    }

    val exactKey = ch.toString()
    val lowerKey = ch.lowercaseChar().toString()
    val recipe = VietnameseCompositionTable.RECIPES[ch]

    val baseStrokes = if (capturedGlyphs.containsKey(exactKey)) {
        capturedGlyphs[exactKey] ?: emptyList()
    } else if (capturedGlyphs.containsKey(lowerKey)) {
        capturedGlyphs[lowerKey] ?: emptyList()
    } else if (recipe != null) {
        val baseExact = recipe.baseChar.toString()
        val baseLower = recipe.baseChar.lowercaseChar().toString()
        capturedGlyphs[baseExact] ?: capturedGlyphs[baseLower] ?: emptyList()
    } else {
        emptyList()
    }

    if (baseStrokes.isEmpty()) {
        Text(
            text = ch.toString(),
            fontSize = (fontSizeDp.value * 0.75f).sp,
            color = Color(0xFF64748B),
            modifier = Modifier.widthIn(min = charWidth)
        )
    } else {
        Canvas(
            modifier = Modifier
                .width(charWidth)
                .height(fontSizeDp)
        ) {
            val w = size.width
            val h = size.height

            // 1. Draw Base character
            for (stroke in baseStrokes) {
                if (stroke.points.size > 1) {
                    val path = Path()
                    path.moveTo(stroke.points[0].x * w, stroke.points[0].y * h)
                    for (i in 1 until stroke.points.size) {
                        path.lineTo(stroke.points[i].x * w, stroke.points[i].y * h)
                    }
                    drawPath(
                        path = path,
                        color = Color(0xFF0F172A),
                        style = Stroke(
                            width = 3.2f,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }
            }

            // 2. Draw Composed Diacritics
            recipe?.diacritics?.forEach { (diacriticType, offset) ->
                val diaKey = getDiacriticKey(diacriticType)
                val diaStrokes = capturedGlyphs[diaKey] ?: emptyList()

                for (stroke in diaStrokes) {
                    if (stroke.points.size > 1) {
                        val path = Path()
                        val scale = 0.55f
                        val dx = offset.xOffsetFraction * w
                        val dy = -offset.yOffsetFraction * h

                        fun transformPt(nx: Float, ny: Float): Offset {
                            val cx = (nx - 0.5f) * scale + 0.5f
                            val cy = (ny - 0.5f) * scale + 0.5f
                            return Offset(cx * w + dx, cy * h + dy)
                        }

                        val p0 = transformPt(stroke.points[0].x, stroke.points[0].y)
                        path.moveTo(p0.x, p0.y)
                        for (i in 1 until stroke.points.size) {
                            val pi = transformPt(stroke.points[i].x, stroke.points[i].y)
                            path.lineTo(pi.x, pi.y)
                        }

                        drawPath(
                            path = path,
                            color = Color(0xFF0F172A),
                            style = Stroke(
                                width = 2.8f,
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    }
                }
            }
        }
    }
}

/**
 * Natural text paragraph layout wrapping words smoothly across lines like a real document.
 */
@Composable
fun FlowTextParagraph(
    text: String,
    capturedGlyphs: Map<String, List<RawStroke>>,
    fontSizeDp: Dp = 28.dp,
    modifier: Modifier = Modifier
) {
    val normalized = VietnameseRepertoire.normalizeToNfc(text)
    val words = normalized.split(" ")

    Layout(
        content = {
            words.forEachIndexed { wordIdx, word ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    word.forEach { ch ->
                        NaturalGlyph(ch = ch, capturedGlyphs = capturedGlyphs, fontSizeDp = fontSizeDp)
                    }
                    if (wordIdx < words.size - 1) {
                        Spacer(modifier = Modifier.width(fontSizeDp * 0.45f))
                    }
                }
            }
        },
        modifier = modifier
    ) { measurables, constraints ->
        val placeables = measurables.map { it.measure(constraints.copy(minWidth = 0, minHeight = 0)) }
        var currentLineWidth = 0
        var currentLineHeight = 0
        val lineHeights = mutableListOf<Int>()
        val linePlaceables = mutableListOf<MutableList<androidx.compose.ui.layout.Placeable>>()
        var currentLine = mutableListOf<androidx.compose.ui.layout.Placeable>()

        for (placeable in placeables) {
            if (currentLineWidth + placeable.width > constraints.maxWidth && currentLine.isNotEmpty()) {
                lineHeights.add(currentLineHeight)
                linePlaceables.add(currentLine)
                currentLine = mutableListOf()
                currentLineWidth = 0
                currentLineHeight = 0
            }
            currentLine.add(placeable)
            currentLineWidth += placeable.width
            currentLineHeight = max(currentLineHeight, placeable.height)
        }
        if (currentLine.isNotEmpty()) {
            lineHeights.add(currentLineHeight)
            linePlaceables.add(currentLine)
        }

        val totalHeight = lineHeights.sum() + (lineHeights.size - 1).coerceAtLeast(0) * 12
        layout(constraints.maxWidth, totalHeight) {
            var yOffset = 0
            for (lineIdx in linePlaceables.indices) {
                var xOffset = 0
                val line = linePlaceables[lineIdx]
                val h = lineHeights[lineIdx]
                for (placeable in line) {
                    placeable.placeRelative(xOffset, yOffset + (h - placeable.height) / 2)
                    xOffset += placeable.width
                }
                yOffset += h + 12
            }
        }
    }
}

@Composable
fun FontPreviewScreen(
    capturedGlyphs: Map<String, List<RawStroke>>,
    onBackToDraw: () -> Unit
) {
    var previewText by remember {
        mutableStateOf("Tiếng Việt có đầy đủ dấu thanh và chữ cái đặc biệt.")
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Xem trước Phông chữ",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Button(onClick = onBackToDraw) {
                    Text("Vẽ tiếp")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Text input to test custom phrases
            OutlinedTextField(
                value = previewText,
                onValueChange = { previewText = it },
                label = { Text("Nhập đoạn văn bản thử nghiệm") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Handwritten Text Rendering Preview Sheet (Document style)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDF9)),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "VĂN BẢN MẪU (CHỮ VIẾT TAY):",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF94A3B8),
                        modifier = Modifier.padding(bottom = 14.dp)
                    )

                    FlowTextParagraph(
                        text = previewText,
                        capturedGlyphs = capturedGlyphs,
                        fontSizeDp = 30.dp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Divider()
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Các đoạn văn bản kiểm thử chuẩn:",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color(0xFF334155)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // English pangram test
            val englishSample = "The quick brown fox jumps over the lazy dog."
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                border = androidx.compose.foundation.BorderStroke(0.5.dp, Color(0xFFE2E8F0))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(text = "English Pangram:", fontSize = 11.sp, color = Color(0xFF64748B))
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowTextParagraph(
                        text = englishSample,
                        capturedGlyphs = capturedGlyphs,
                        fontSizeDp = 26.dp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            VietnameseTestCorpus.REAL_SENTENCES.forEach { sentence ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, Color(0xFFE2E8F0))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(text = "Tiếng Việt chuẩn:", fontSize = 11.sp, color = Color(0xFF64748B))
                        Spacer(modifier = Modifier.height(8.dp))
                        FlowTextParagraph(
                            text = sentence,
                            capturedGlyphs = capturedGlyphs,
                            fontSizeDp = 26.dp,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}
