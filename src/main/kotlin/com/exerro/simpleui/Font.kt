package com.exerro.simpleui

/** Description of font information. */
data class Font(
    /** Size of the font, represented as the height of a line of text in pixels. */
    val lineHeight: Float,
    /** Whether the monospace or default font should be used. */
    val isMonospaced: Boolean,
) {
    companion object {
        /** Default font used for general text. */
        val default = Font(lineHeight = 22f, isMonospaced = false)

        /** Default font used for monospace text. */
        val monospace = Font(lineHeight = 18f, isMonospaced = true)

        /** Default font used for heading text. */
        val heading = Font(lineHeight = 28f, isMonospaced = true)
    }
}
