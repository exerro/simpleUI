package com.exerro.simpleui.extensions

import com.exerro.simpleui.TextDrawContext
import com.exerro.simpleui.colour.Colour

/** Write [text] in the given [colour]. If [splitAtSpaces] is true, [text] will
 *  be split at space characters to allow word wrapping. */
fun TextDrawContext<Unit>.text(text: String, colour: Colour, splitAtSpaces: Boolean = true): Unit =
    textTagged(text = text, colour = colour, tag = Unit, splitAtSpaces = splitAtSpaces)

/** Write [length] spaces. Note that this is slightly more efficient than using
 *  [text] since nothing will be rendered unless there is active highlighting,
 *  strike-through, or underlining. */
fun TextDrawContext<Unit>.whitespace(length: Int = 1) =
    whitespaceTagged(length = length, tag = Unit)
