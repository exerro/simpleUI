package com.exerro.simpleui

/** A context used to provide text rendering capabilities. */
interface TextDrawContext {
    /** Begin a new line, indented relative to the current line by
     *  [relativeIndentation] "virtual tabs". */
    fun lineBreak(relativeIndentation: Int = 0)

    /** Write [length] spaces. Note that this is slightly more efficient than
     *  using [text] since nothing will be rendered unless there is active
     *  highlighting or underlining. */
    fun whitespace(length: Int = 1)

    /** Write [text] in the given [colour]. If [splitAtSpaces] is true, [text]
     *  will be split at space characters to allow word wrapping. */
    fun text(text: String, colour: RGB, splitAtSpaces: Boolean = true)

    @Undocumented
    fun verticalCursor(colour: RGB)

    /** Start underlining text in the given [colour]. Any text written after
     *  this call (or until the [stopUnderlining]) will be underlined in this
     *  [colour]. Colours stack, so underlining in blue then red, then stopping
     *  underlining, will continue underlying in the previous colour blue. */
    fun beginUnderlining(colour: RGB)

    /** Stop underlining with the last-set underline colour. */
    fun stopUnderlining()

    /** Start highlighting text in the given [colour]. Any text written after
     *  this call (or until the [stopHighlighting]) will be highlighted in this
     *  [colour]. Colours stack, so highlighting in blue then red, then stopping
     *  highlighting, will continue highlighting in the previous colour blue. */
    fun beginHighlighting(colour: RGB)

    /** Stop highlighting with the last-set highlight colour. */
    fun stopHighlighting()
}
