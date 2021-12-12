package com.exerro.simpleui

/** A [Graphics] object contains graphics-related methods that are not draw
 *  commands. */
interface Graphics {
    /** Return a copy of [buffer] word-wrapped to fit the [availableWidth]. */
    fun <Colour> wordWrap(
        buffer: TextBuffer<Colour>,
        availableWidth: Float,
        font: Font = Font.default,
        indentationSize: Int = 4,
    ): TextBuffer<Colour>

    /** Return the width and height in pixels of [buffer] (including any
     *  indentation). */
    fun textBufferSize(
        buffer: TextBuffer<*>,
        font: Font = Font.default,
        indentationSize: Int = 4,
    ): Pair<Float, Float>
}
