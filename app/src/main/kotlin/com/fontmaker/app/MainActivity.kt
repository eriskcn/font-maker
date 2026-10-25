package com.fontmaker.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fontmaker.core.vietnamese.VietnameseRepertoire
import com.fontmaker.core.vietnamese.VietnameseTestCorpus

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FontMakerApp()
                }
            }
        }
    }
}

@Composable
fun FontMakerApp() {
    Scaffold { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            item {
                Text(
                    text = "Font Maker - Vietnamese Support Stub",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Total Vietnamese precomposed letters: ${VietnameseRepertoire.PRECOMPOSED_VIETNAMESE_LETTERS.size}",
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Divider()
                Text(
                    text = "Sample Sentences (Test Corpus):",
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            items(VietnameseTestCorpus.REAL_SENTENCES) { sentence ->
                val normalized = VietnameseRepertoire.normalizeToNfc(sentence)
                Text(
                    text = "• $normalized",
                    fontSize = 15.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            item {
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                Text(
                    text = "Base Vowels + Tone Series:",
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            items(VietnameseTestCorpus.TONE_SERIES) { row ->
                Text(
                    text = row.joinToString("   "),
                    fontSize = 16.sp,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }
    }
}
