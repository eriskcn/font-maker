package com.fontmaker.core.model

/**
 * 2D Point representation in normalized coordinate space [0..1] or font unit space.
 */
data class GlyphPoint(
    val x: Float,
    val y: Float,
    val isControlPoint: Boolean = false
)

/**
 * Single continuous stroke drawn by user on canvas.
 */
data class RawStroke(
    val points: List<GlyphPoint>,
    val strokeWidth: Float = 12f
)

/**
 * Vector representation of a drawn glyph composed of multiple strokes.
 */
data class RawGlyphData(
    val characterKey: String,
    val strokes: List<RawStroke> = emptyList(),
    val baselineY: Float = 0.8f,
    val capHeightY: Float = 0.2f,
    val xHeightY: Float = 0.45f
)
