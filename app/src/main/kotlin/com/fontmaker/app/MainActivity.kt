package com.fontmaker.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fontmaker.app.ui.canvas.GlyphDrawingCanvas
import com.fontmaker.app.ui.preview.FontPreviewScreen
import com.fontmaker.core.model.RawStroke

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

data class DrawPromptItem(
    val key: String,
    val displayLabel: String,
    val ghostLetter: String
)

// Complete Repertoire:
// 26 Lowercase + 26 Uppercase + 7 Vietnamese special vowels + 8 diacritics + 10 Digits + 31 Punctuation & Symbols
val REQUIRED_ITEMS = buildList {
    // 1. Basic Latin Lowercase (a-z: 26)
    for (c in 'a'..'z') {
        add(DrawPromptItem(c.toString(), c.toString(), c.toString()))
    }

    // 2. Basic Latin Uppercase (A-Z: 26)
    for (c in 'A'..'Z') {
        add(DrawPromptItem(c.toString(), c.toString(), c.toString()))
    }

    // 3. Special Vietnamese vowels & consonants (7 lowercase)
    add(DrawPromptItem("ă", "ă", "ă"))
    add(DrawPromptItem("â", "â", "â"))
    add(DrawPromptItem("ê", "ê", "ê"))
    add(DrawPromptItem("ô", "ô", "ô"))
    add(DrawPromptItem("ơ", "ơ", "ơ"))
    add(DrawPromptItem("ư", "ư", "ư"))
    add(DrawPromptItem("đ", "đ", "đ"))

    // 4. Special Vietnamese vowels & consonants (7 uppercase)
    add(DrawPromptItem("Ă", "Ă", "Ă"))
    add(DrawPromptItem("Â", "Â", "Â"))
    add(DrawPromptItem("Ê", "Ê", "Ê"))
    add(DrawPromptItem("Ô", "Ô", "Ô"))
    add(DrawPromptItem("Ơ", "Ơ", "Ơ"))
    add(DrawPromptItem("Ư", "Ư", "Ư"))
    add(DrawPromptItem("Đ", "Đ", "Đ"))

    // 5. Standalone Diacritics (8)
    add(DrawPromptItem("acute", "Dấu Sắc (´)", "´"))
    add(DrawPromptItem("grave", "Dấu Huyền (`)", "`"))
    add(DrawPromptItem("hook", "Dấu Hỏi ( ̉)", " ̉"))
    add(DrawPromptItem("tilde", "Dấu Ngã (~)", "~"))
    add(DrawPromptItem("dot", "Dấu Nặng (.)", "."))
    add(DrawPromptItem("circumflex", "Dấu Mũ (^)", "^"))
    add(DrawPromptItem("breve", "Dấu Trăng (˘)", "˘"))
    add(DrawPromptItem("horn", "Dấu Móc (̛)", "̛"))

    // 6. Digits (0-9: 10)
    for (d in '0'..'9') {
        add(DrawPromptItem(d.toString(), "Số $d", d.toString()))
    }

    // 7. Full Standard ASCII Punctuation & Symbols (31)
    val punctuations = listOf(
        "." to "Dấu Chấm (.)",
        "," to "Dấu Phẩy (,)",
        "?" to "Dấu Chấm Hỏi (?)",
        "!" to "Dấu Chấm Than (!)",
        ":" to "Dấu Hai Chấm (:)",
        ";" to "Dấu Chấm Phẩy (;)",
        "'" to "Dấu Nháy Đơn (')",
        "\"" to "Dấu Ngoặc Kép (\")",
        "-" to "Dấu Gạch Nối (-)",
        "_" to "Gạch Dưới (_)",
        "/" to "Gạch Chéo Phải (/)",
        "\\" to "Gạch Chéo Trái (\\)",
        "|" to "Thanh Đứng (|)",
        "@" to "Ký tự @",
        "#" to "Dấu Thăng (#)",
        "$" to "Ký hiệu Đôla ($)",
        "%" to "Phần Trăm (%)",
        "&" to "Ký tự Và (&)",
        "*" to "Dấu Sao (*)",
        "+" to "Dấu Cộng (+)",
        "=" to "Dấu Bằng (=)",
        "<" to "Nhỏ Hơn (<)",
        ">" to "Lớn Hơn (>)",
        "(" to "Mở Ngoặc Đơn (()",
        ")" to "Đóng Ngoặc Đơn ())",
        "[" to "Mở Ngoặc Vuông ([)",
        "]" to "Đóng Ngoặc Vuông (])",
        "{" to "Mở Ngoặc Nhọn ({)",
        "}" to "Đóng Ngoặc Nhọn (})",
        "~" to "Dấu Ngã Sóng (~)",
        "`" to "Dấu Huyền Đơn (`)"
    )
    for ((sym, label) in punctuations) {
        add(DrawPromptItem(sym, label, sym))
    }
}

@Composable
fun AppNavigation() {
    var showPreview by remember { mutableStateOf(false) }
    val capturedGlyphs = remember { mutableStateMapOf<String, List<RawStroke>>() }

    if (showPreview) {
        FontPreviewScreen(
            capturedGlyphs = capturedGlyphs,
            onBackToDraw = { showPreview = false }
        )
    } else {
        HandwritingCaptureScreen(
            capturedGlyphs = capturedGlyphs,
            onNavigateToPreview = { showPreview = true }
        )
    }
}

@Composable
fun HandwritingCaptureScreen(
    capturedGlyphs: MutableMap<String, List<RawStroke>>,
    onNavigateToPreview: () -> Unit
) {
    var currentIndex by remember { mutableIntStateOf(0) }
    val currentItem = REQUIRED_ITEMS[currentIndex]
    val currentStrokes = remember { mutableStateListOf<RawStroke>() }

    // Load existing strokes when moving between characters
    fun loadCurrentItemStrokes(index: Int) {
        currentStrokes.clear()
        val existing = capturedGlyphs[REQUIRED_ITEMS[index].key]
        if (existing != null) {
            currentStrokes.addAll(existing)
        }
    }

    // Save current strokes to map
    fun saveCurrentStrokes() {
        if (currentStrokes.isNotEmpty()) {
            capturedGlyphs[currentItem.key] = currentStrokes.toList()
        }
    }

    val isLastItem = currentIndex == REQUIRED_ITEMS.size - 1

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ký tự (${currentIndex + 1}/${REQUIRED_ITEMS.size})",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray
                )
                if (capturedGlyphs.isNotEmpty()) {
                    OutlinedButton(
                        onClick = {
                            saveCurrentStrokes()
                            onNavigateToPreview()
                        }
                    ) {
                        Text("Xem thử (${capturedGlyphs.size}/${REQUIRED_ITEMS.size})")
                    }
                }
            }

            Text(
                text = currentItem.displayLabel,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Text(
                text = "Vẽ đè lên chữ mờ để đúng tỷ lệ",
                fontSize = 13.sp,
                color = Color(0xFF64748B),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Compact canvas area with ghost character
            GlyphDrawingCanvas(
                modifier = Modifier.size(260.dp),
                ghostChar = currentItem.ghostLetter,
                strokes = currentStrokes,
                onStrokesChanged = {
                    saveCurrentStrokes()
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Action buttons: Undo, Clear
            Row(
                modifier = Modifier.fillMaxWidth(0.9f),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OutlinedButton(
                    onClick = {
                        if (currentStrokes.isNotEmpty()) {
                            currentStrokes.removeAt(currentStrokes.size - 1)
                            saveCurrentStrokes()
                        }
                    },
                    enabled = currentStrokes.isNotEmpty()
                ) {
                    Text("Hoàn tác")
                }

                OutlinedButton(
                    onClick = {
                        currentStrokes.clear()
                        capturedGlyphs.remove(currentItem.key)
                    },
                    enabled = currentStrokes.isNotEmpty(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
                ) {
                    Text("Xoá nét")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider()
            Spacer(modifier = Modifier.height(16.dp))

            // Navigation: Previous / Next / Submit
            Row(
                modifier = Modifier.fillMaxWidth(0.9f),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        if (currentIndex > 0) {
                            saveCurrentStrokes()
                            currentIndex--
                            loadCurrentItemStrokes(currentIndex)
                        }
                    },
                    enabled = currentIndex > 0
                ) {
                    Text("Trước")
                }

                if (!isLastItem) {
                    Button(
                        onClick = {
                            saveCurrentStrokes()
                            currentIndex++
                            loadCurrentItemStrokes(currentIndex)
                        }
                    ) {
                        Text("Tiếp theo")
                    }
                } else {
                    Button(
                        onClick = {
                            saveCurrentStrokes()
                            onNavigateToPreview()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A))
                    ) {
                        Text("Hoàn tất & Preview")
                    }
                }
            }
        }
    }
}
