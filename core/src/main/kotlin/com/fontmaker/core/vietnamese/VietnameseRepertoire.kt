package com.fontmaker.core.vietnamese

import java.text.Normalizer

enum class ToneMark {
    NONE, ACUTE, GRAVE, HOOK_ABOVE, TILDE, DOT_BELOW
}

enum class BaseVowel {
    A, A_BREVE, A_CIRCUMFLEX,
    E, E_CIRCUMFLEX,
    I,
    O, O_CIRCUMFLEX, O_HORN,
    U, U_HORN,
    Y
}

data class VietnameseVowelInfo(
    val char: Char,
    val uppercase: Boolean,
    val baseVowel: BaseVowel,
    val tone: ToneMark
)

object VietnameseRepertoire {
    const val BASIC_UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    const val BASIC_LOWERCASE = "abcdefghijklmnopqrstuvwxyz"
    const val DIGITS = "0123456789"
    const val PUNCTUATION_AND_WHITESPACE = " !\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~\n\t"

    // 12 base vowels lower + upper
    val BASE_VOWELS = listOf(
        'a', 'ă', 'â', 'e', 'ê', 'i', 'o', 'ô', 'ơ', 'u', 'ư', 'y',
        'A', 'Ă', 'Â', 'E', 'Ê', 'I', 'O', 'Ô', 'Ơ', 'U', 'Ư', 'Y'
    )

    // Additional consonants
    val CONSONANTS = listOf('đ', 'Đ')

    // 134 precomposed Vietnamese letters (67 lowercase + 67 uppercase)
    val PRECOMPOSED_VIETNAMESE_LETTERS: Set<Char> = setOf(
        // a
        'á', 'à', 'ả', 'ã', 'ạ',
        // ă
        'ă', 'ắ', 'ằ', 'ẳ', 'ẵ', 'ặ',
        // â
        'â', 'ấ', 'ầ', 'ẩ', 'ẫ', 'ậ',
        // e
        'é', 'è', 'ẻ', 'ẽ', 'ẹ',
        // ê
        'ê', 'ế', 'ề', 'ể', 'ễ', 'ệ',
        // i
        'í', 'ì', 'ỉ', 'ĩ', 'ị',
        // o
        'ó', 'ò', 'ỏ', 'õ', 'ọ',
        // ô
        'ô', 'ố', 'ồ', 'ổ', 'ỗ', 'ộ',
        // ơ
        'ơ', 'ớ', 'ờ', 'ở', 'ỡ', 'ợ',
        // u
        'ú', 'ù', 'ủ', 'ũ', 'ụ',
        // ư
        'ư', 'ứ', 'ừ', 'ử', 'ữ', 'ự',
        // y
        'ý', 'ỳ', 'ỷ', 'ỹ', 'ỵ',
        // đ
        'đ',

        // A
        'Á', 'À', 'Ả', 'Ã', 'Ạ',
        // Ă
        'Ă', 'Ắ', 'Ằ', 'Ẳ', 'Ẵ', 'Ặ',
        // Â
        'Â', 'Ấ', 'Ầ', 'Ẩ', 'Ẫ', 'Ậ',
        // E
        'É', 'È', 'Ẻ', 'Ẽ', 'Ẹ',
        // Ê
        'Ê', 'Ế', 'Ề', 'Ể', 'Ễ', 'Ệ',
        // I
        'Í', 'Ì', 'Ỉ', 'Ĩ', 'Ị',
        // O
        'Ó', 'Ò', 'Ỏ', 'Õ', 'Ọ',
        // Ô
        'Ô', 'Ố', 'Ồ', 'Ổ', 'Ỗ', 'Ộ',
        // Ơ
        'Ơ', 'Ớ', 'Ờ', 'Ở', 'Ỡ', 'Ợ',
        // U
        'Ú', 'Ù', 'Ủ', 'Ũ', 'Ụ',
        // Ư
        'Ư', 'Ứ', 'Ừ', 'Ử', 'Ữ', 'Ự',
        // Y
        'Ý', 'Ỳ', 'Ỷ', 'Ỹ', 'Ỵ',
        // Đ
        'Đ'
    )

    /**
     * Normalize input string to NFC for consistent glyph lookup.
     */
    fun normalizeToNfc(input: String): String {
        return Normalizer.normalize(input, Normalizer.Form.NFC)
    }

    /**
     * Map decomposed/composed sequence to single NFC codepoint if supported.
     */
    fun toNfcCodePoints(input: String): List<Int> {
        val nfc = normalizeToNfc(input)
        val codePoints = mutableListOf<Int>()
        var offset = 0
        while (offset < nfc.length) {
            val cp = nfc.codePointAt(offset)
            codePoints.add(cp)
            offset += Character.charCount(cp)
        }
        return codePoints
    }

    /**
     * Verify if character is within required Vietnamese set.
     */
    fun isSupportedVietnameseLetter(c: Char): Boolean {
        return PRECOMPOSED_VIETNAMESE_LETTERS.contains(c)
    }
}
