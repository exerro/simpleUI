package com.exerro.simpleui

import com.exerro.simpleui.colour.Colour

/** A context used to provide text rendering capabilities. */
@DrawContextDSL
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
    fun text(text: String, colour: Colour, splitAtSpaces: Boolean = true)

    @Undocumented
    fun verticalCursor(colour: Colour)

    /** Start underlining text in the given [colour]. Any text written after
     *  this call (or until the [stopUnderlining]) will be underlined in this
     *  [colour]. Colours stack, so underlining in blue then red, then stopping
     *  underlining, will continue underlying in the previous colour (blue). */
    fun beginUnderlining(colour: Colour)

    /** Stop underlining with the last-set underline colour. */
    fun stopUnderlining()

    /** Start striking through text in the given [colour]. Any text written
     *  after this call (or until the [stopStrikingThrough]) will be struck
     *  through in this [colour]. Colours stack, so striking through in blue
     *  then red, then stopping striking through, will continue striking through
     *  in the previous colour (blue). */
    fun beginStrikingThrough(colour: Colour)

    /** Stop striking through the text with the last-set strikethrough colour. */
    fun stopStrikingThrough()

    /** Start highlighting text in the given [colour]. Any text written after
     *  this call (or until the [stopHighlighting]) will be highlighted in this
     *  [colour]. Colours stack, so highlighting in blue then red, then stopping
     *  highlighting, will continue highlighting in the previous colour (blue). */
    fun beginHighlighting(colour: Colour)

    /** Stop highlighting with the last-set highlight colour. */
    fun stopHighlighting()
}
