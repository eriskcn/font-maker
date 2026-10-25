# Vietnamese Language Support Specification Implementation

## Character Coverage
Defined in [VietnameseRepertoire.kt](core/src/main/kotlin/com/fontmaker/core/vietnamese/VietnameseRepertoire.kt):
- All 134 precomposed Vietnamese uppercase and lowercase characters.
- Basic Latin (A-Z, a-z), digits, punctuation, and whitespace.

## Unicode Normalization Strategy
- **NFC Target:** Input text is canonicalized to NFC via standard `java.text.Normalizer`.
- **NFD Handling:** Canonical equivalence guaranteed; decomposed sequences map to the exact same code point and glyph metrics.

## Glyph Composition Engine
Defined in [GlyphComposition.kt](core/src/main/kotlin/com/fontmaker/core/vietnamese/GlyphComposition.kt):
- Base letters + reusable diacritics (`ACUTE`, `GRAVE`, `HOOK_ABOVE`, `TILDE`, `DOT_BELOW`, `CIRCUMFLEX`, `BREVE`, `HORN`, `STROKE`).
- Relative offset matrix handling stacked diacritics and cap-height/ascender boundaries.
- User manual drawing override supported per glyph.

## Test Corpus
Defined in [VietnameseTestCorpus.kt](core/src/main/kotlin/com/fontmaker/core/vietnamese/VietnameseTestCorpus.kt) and verified via JUnit tests in [VietnameseRepertoireTest.kt](core/src/test/kotlin/com/fontmaker/core/vietnamese/VietnameseRepertoireTest.kt).
