package com.fontmaker.core.vietnamese

/**
 * Reusable diacritic components for composition.
 */
enum class DiacriticType {
    ACUTE,         // Sắc (´)
    GRAVE,         // Huyền (`)
    HOOK_ABOVE,    // Hỏi ( ̉)
    TILDE,         // Ngã (~)
    DOT_BELOW,     // Nặng (.)
    CIRCUMFLEX,    // Mũ (^: â, ê, ô)
    BREVE,         // Trăng (˘: ă)
    HORN,          // Móc (̛: ơ, ư)
    STROKE         // Gạch ngang (đ, Đ)
}

/**
 * Relative positioning anchors for attaching diacritics onto base glyphs.
 * xOffsetFraction: relative offset from center (-0.5 to 0.5)
 * yOffsetFraction: relative vertical anchor (positive = upward above glyph/cap, negative = downward below baseline)
 */
data class DiacriticOffset(
    val xOffsetFraction: Float,
    val yOffsetFraction: Float
)

data class GlyphCompositionRecipe(
    val targetChar: Char,
    val baseChar: Char,
    val diacritics: List<Pair<DiacriticType, DiacriticOffset>>,
    val isUppercase: Boolean = targetChar.isUpperCase()
)

object VietnameseCompositionTable {

    // Standard single-tone relative offsets
    private val TONE_ABOVE = DiacriticOffset(0.0f, 0.22f)
    private val TONE_BELOW = DiacriticOffset(0.0f, -0.20f)

    // Stacked tone offsets (e.g., above circumflex or breve)
    private val STACKED_ACUTE = DiacriticOffset(0.12f, 0.38f)
    private val STACKED_GRAVE = DiacriticOffset(-0.12f, 0.38f)
    private val STACKED_HOOK = DiacriticOffset(0.05f, 0.38f)
    private val STACKED_TILDE = DiacriticOffset(0.0f, 0.40f)

    // Horn side offset (for ơ, ư)
    private val HORN_OFFSET = DiacriticOffset(0.35f, 0.18f)

    private fun buildTonedSeries(
        baseChar: Char,
        acute: Char, grave: Char, hook: Char, tilde: Char, dot: Char,
        isStacked: Boolean = false
    ): List<GlyphCompositionRecipe> {
        val acuteOff = if (isStacked) STACKED_ACUTE else TONE_ABOVE
        val graveOff = if (isStacked) STACKED_GRAVE else TONE_ABOVE
        val hookOff = if (isStacked) STACKED_HOOK else TONE_ABOVE
        val tildeOff = if (isStacked) STACKED_TILDE else TONE_ABOVE

        return listOf(
            GlyphCompositionRecipe(acute, baseChar, listOf(DiacriticType.ACUTE to acuteOff)),
            GlyphCompositionRecipe(grave, baseChar, listOf(DiacriticType.GRAVE to graveOff)),
            GlyphCompositionRecipe(hook, baseChar, listOf(DiacriticType.HOOK_ABOVE to hookOff)),
            GlyphCompositionRecipe(tilde, baseChar, listOf(DiacriticType.TILDE to tildeOff)),
            GlyphCompositionRecipe(dot, baseChar, listOf(DiacriticType.DOT_BELOW to TONE_BELOW))
        )
    }

    /**
     * Complete mapping for all 134 precomposed Vietnamese characters.
     */
    val RECIPES: Map<Char, GlyphCompositionRecipe> = buildMap {
        // --- Lowercase ---
        // a
        buildTonedSeries('a', 'á', 'à', 'ả', 'ã', 'ạ').forEach { put(it.targetChar, it) }

        // ă (base 'a' + breve)
        put('ă', GlyphCompositionRecipe('ă', 'a', listOf(DiacriticType.BREVE to TONE_ABOVE)))
        buildTonedSeries('ă', 'ắ', 'ằ', 'ẳ', 'ẵ', 'ặ', isStacked = true).forEach { put(it.targetChar, it) }

        // â (base 'a' + circumflex)
        put('â', GlyphCompositionRecipe('â', 'a', listOf(DiacriticType.CIRCUMFLEX to TONE_ABOVE)))
        buildTonedSeries('â', 'ấ', 'ầ', 'ẩ', 'ẫ', 'ậ', isStacked = true).forEach { put(it.targetChar, it) }

        // e
        buildTonedSeries('e', 'é', 'è', 'ẻ', 'ẽ', 'ẹ').forEach { put(it.targetChar, it) }

        // ê (base 'e' + circumflex)
        put('ê', GlyphCompositionRecipe('ê', 'e', listOf(DiacriticType.CIRCUMFLEX to TONE_ABOVE)))
        buildTonedSeries('ê', 'ế', 'ề', 'ể', 'ễ', 'ệ', isStacked = true).forEach { put(it.targetChar, it) }

        // i
        buildTonedSeries('i', 'í', 'ì', 'ỉ', 'ĩ', 'ị').forEach { put(it.targetChar, it) }

        // o
        buildTonedSeries('o', 'ó', 'ò', 'ỏ', 'õ', 'ọ').forEach { put(it.targetChar, it) }

        // ô (base 'o' + circumflex)
        put('ô', GlyphCompositionRecipe('ô', 'o', listOf(DiacriticType.CIRCUMFLEX to TONE_ABOVE)))
        buildTonedSeries('ô', 'ố', 'ồ', 'ổ', 'ỗ', 'ộ', isStacked = true).forEach { put(it.targetChar, it) }

        // ơ (base 'o' + horn)
        put('ơ', GlyphCompositionRecipe('ơ', 'o', listOf(DiacriticType.HORN to HORN_OFFSET)))
        buildTonedSeries('ơ', 'ớ', 'ờ', 'ở', 'ỡ', 'ợ', isStacked = true).forEach { put(it.targetChar, it) }

        // u
        buildTonedSeries('u', 'ú', 'ù', 'ủ', 'ũ', 'ụ').forEach { put(it.targetChar, it) }

        // ư (base 'u' + horn)
        put('ư', GlyphCompositionRecipe('ư', 'u', listOf(DiacriticType.HORN to HORN_OFFSET)))
        buildTonedSeries('ư', 'ứ', 'ừ', 'ử', 'ữ', 'ự', isStacked = true).forEach { put(it.targetChar, it) }

        // y
        buildTonedSeries('y', 'ý', 'ỳ', 'ỷ', 'ỹ', 'ỵ').forEach { put(it.targetChar, it) }

        // đ
        put('đ', GlyphCompositionRecipe('đ', 'd', listOf(DiacriticType.STROKE to DiacriticOffset(0.0f, 0.0f))))

        // --- Uppercase ---
        // A
        buildTonedSeries('A', 'Á', 'À', 'Ả', 'Ã', 'Ạ').forEach { put(it.targetChar, it) }

        // Ă
        put('Ă', GlyphCompositionRecipe('Ă', 'A', listOf(DiacriticType.BREVE to TONE_ABOVE)))
        buildTonedSeries('Ă', 'Ắ', 'Ằ', 'Ẳ', 'Ẵ', 'Ặ', isStacked = true).forEach { put(it.targetChar, it) }

        // Â
        put('Â', GlyphCompositionRecipe('Â', 'A', listOf(DiacriticType.CIRCUMFLEX to TONE_ABOVE)))
        buildTonedSeries('Â', 'Ấ', 'Ầ', 'Ẩ', 'Ẫ', 'Ậ', isStacked = true).forEach { put(it.targetChar, it) }

        // E
        buildTonedSeries('E', 'É', 'È', 'Ẻ', 'Ẽ', 'Ẹ').forEach { put(it.targetChar, it) }

        // Ê
        put('Ê', GlyphCompositionRecipe('Ê', 'E', listOf(DiacriticType.CIRCUMFLEX to TONE_ABOVE)))
        buildTonedSeries('Ê', 'Ế', 'Ề', 'Ể', 'Ễ', 'Ệ', isStacked = true).forEach { put(it.targetChar, it) }

        // I
        buildTonedSeries('I', 'Í', 'Ì', 'Ỉ', 'Ĩ', 'Ị').forEach { put(it.targetChar, it) }

        // O
        buildTonedSeries('O', 'Ó', 'Ò', 'Ỏ', 'Õ', 'Ọ').forEach { put(it.targetChar, it) }

        // Ô
        put('Ô', GlyphCompositionRecipe('Ô', 'O', listOf(DiacriticType.CIRCUMFLEX to TONE_ABOVE)))
        buildTonedSeries('Ô', 'Ố', 'Ồ', 'Ổ', 'Ỗ', 'Ộ', isStacked = true).forEach { put(it.targetChar, it) }

        // Ơ
        put('Ơ', GlyphCompositionRecipe('Ơ', 'O', listOf(DiacriticType.HORN to HORN_OFFSET)))
        buildTonedSeries('Ơ', 'Ớ', 'Ờ', 'Ở', 'Ỡ', 'Ợ', isStacked = true).forEach { put(it.targetChar, it) }

        // U
        buildTonedSeries('U', 'Ú', 'Ù', 'Ủ', 'Ũ', 'Ụ').forEach { put(it.targetChar, it) }

        // Ư
        put('Ư', GlyphCompositionRecipe('Ư', 'U', listOf(DiacriticType.HORN to HORN_OFFSET)))
        buildTonedSeries('Ư', 'Ứ', 'Ừ', 'Ử', 'Ữ', 'Ự', isStacked = true).forEach { put(it.targetChar, it) }

        // Y
        buildTonedSeries('Y', 'Ý', 'Ỳ', 'Ỷ', 'Ỹ', 'Ỵ').forEach { put(it.targetChar, it) }

        // Đ
        put('Đ', GlyphCompositionRecipe('Đ', 'D', listOf(DiacriticType.STROKE to DiacriticOffset(0.0f, 0.0f))))
    }
}
