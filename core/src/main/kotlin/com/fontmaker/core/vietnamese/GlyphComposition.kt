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
 */
data class DiacriticOffset(
    val xOffsetFraction: Float, // horizontal adjustment (-0.5 to 0.5 relative to base width)
    val yOffsetFraction: Float  // vertical adjustment (e.g. above cap-height or below baseline)
)

data class GlyphCompositionRecipe(
    val targetChar: Char,
    val baseChar: Char,
    val diacritics: List<Pair<DiacriticType, DiacriticOffset>>,
    val isUppercase: Boolean = targetChar.isUpperCase()
)

object VietnameseCompositionTable {
    // Map of precomposed characters to composition breakdown
    val RECIPES: Map<Char, GlyphCompositionRecipe> = buildMap {
        // Example: 'á' -> base 'a' + ACUTE above
        put('á', GlyphCompositionRecipe('á', 'a', listOf(DiacriticType.ACUTE to DiacriticOffset(0.0f, 0.2f))))
        put('à', GlyphCompositionRecipe('à', 'a', listOf(DiacriticType.GRAVE to DiacriticOffset(0.0f, 0.2f))))
        put('ả', GlyphCompositionRecipe('ả', 'a', listOf(DiacriticType.HOOK_ABOVE to DiacriticOffset(0.0f, 0.2f))))
        put('ã', GlyphCompositionRecipe('ã', 'a', listOf(DiacriticType.TILDE to DiacriticOffset(0.0f, 0.2f))))
        put('ạ', GlyphCompositionRecipe('ạ', 'a', listOf(DiacriticType.DOT_BELOW to DiacriticOffset(0.0f, -0.2f))))

        // Stacked diacritic example: 'ấ' -> base 'â' + ACUTE or base 'a' + CIRCUMFLEX + ACUTE
        put('ấ', GlyphCompositionRecipe('ấ', 'â', listOf(DiacriticType.ACUTE to DiacriticOffset(0.1f, 0.35f))))
        put('ầ', GlyphCompositionRecipe('ầ', 'â', listOf(DiacriticType.GRAVE to DiacriticOffset(-0.1f, 0.35f))))
        put('ẩ', GlyphCompositionRecipe('ẩ', 'â', listOf(DiacriticType.HOOK_ABOVE to DiacriticOffset(0.0f, 0.35f))))
        put('ẫ', GlyphCompositionRecipe('ẫ', 'â', listOf(DiacriticType.TILDE to DiacriticOffset(0.0f, 0.35f))))
        put('ậ', GlyphCompositionRecipe('ậ', 'â', listOf(DiacriticType.DOT_BELOW to DiacriticOffset(0.0f, -0.2f))))

        // đ / Đ
        put('đ', GlyphCompositionRecipe('đ', 'd', listOf(DiacriticType.STROKE to DiacriticOffset(0.0f, 0.0f))))
        put('Đ', GlyphCompositionRecipe('Đ', 'D', listOf(DiacriticType.STROKE to DiacriticOffset(0.0f, 0.0f))))
    }
}
