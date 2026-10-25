package com.fontmaker.core.vietnamese

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.text.Normalizer

class VietnameseRepertoireTest {

    @Test
    fun testRepertoireCount() {
        // Spec requirement: 134 precomposed Vietnamese letters
        assertEquals(134, VietnameseRepertoire.PRECOMPOSED_VIETNAMESE_LETTERS.size)
    }

    @Test
    fun testCompositionTableFullCoverage() {
        // Every single precomposed character must have an explicit composition recipe
        assertEquals(
            134,
            VietnameseCompositionTable.RECIPES.size,
            "Composition table must cover all 134 precomposed characters"
        )

        for (ch in VietnameseRepertoire.PRECOMPOSED_VIETNAMESE_LETTERS) {
            val recipe = VietnameseCompositionTable.RECIPES[ch]
            assertTrue(recipe != null, "Missing composition recipe for character '$ch' (U+${Integer.toHexString(ch.code)})")
            assertEquals(ch, recipe?.targetChar)
        }
    }

    @Test
    fun testNormalizationEquivalence() {
        // "ế" precomposed vs decomposed
        val nfc = "ế"
        val nfd = "ế" // e + circumflex + acute

        assertEquals(Normalizer.normalize(nfd, Normalizer.Form.NFC), nfc)
        assertEquals(VietnameseRepertoire.normalizeToNfc(nfd), nfc)

        val cpListNfc = VietnameseRepertoire.toNfcCodePoints(nfc)
        val cpListNfd = VietnameseRepertoire.toNfcCodePoints(nfd)

        assertEquals(cpListNfc, cpListNfd)
        assertEquals(1, cpListNfc.size)
        assertEquals(nfc.first().code, cpListNfc.first())
    }

    @Test
    fun testCorpusSentencesAllCovered() {
        for (sentence in VietnameseTestCorpus.REAL_SENTENCES) {
            val nfc = VietnameseRepertoire.normalizeToNfc(sentence)
            for (ch in nfc) {
                if (ch.isLetter()) {
                    val isAsciiLetter = (ch in 'a'..'z') || (ch in 'A'..'Z')
                    val isVietnameseLetter = VietnameseRepertoire.isSupportedVietnameseLetter(ch)
                    assertTrue(
                        isAsciiLetter || isVietnameseLetter,
                        "Character '$ch' (U+${Integer.toHexString(ch.code)}) in sentence \"$sentence\" must be supported"
                    )
                }
            }
        }
    }
}
