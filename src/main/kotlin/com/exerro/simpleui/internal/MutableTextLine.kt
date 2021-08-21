package com.exerro.simpleui.internal

import com.exerro.simpleui.colour.Colour
import com.exerro.simpleui.UndocumentedInternal
import com.exerro.simpleui.colour.Greyscale

@UndocumentedInternal
class MutableTextLine {
    @UndocumentedInternal
    fun finishLine(): Triple<String, List<Segment>, List<Cursor>> {
        val line = content.toString()
        val returnSegments = segments
        val returnCursors = cursors

        content.clear()
        segments = mutableListOf()
        cursors = mutableListOf()

        return Triple(line, returnSegments, returnCursors)
    }

    @UndocumentedInternal
    fun pushText(text: String, colour: Colour) {
        segments.add(Segment(content.length, text, colour,
            activeHighlightColours.lastOrNull(), activeUnderlineColours.lastOrNull(),
            activeStrikeThroughColours.lastOrNull(), false))
        content.append(text)
    }

    @UndocumentedInternal
    fun pushWhitespace(length: Int) {
        if (activeHighlightColours.isNotEmpty() || activeUnderlineColours.isNotEmpty() || activeStrikeThroughColours.isNotEmpty()) {
            segments.add(Segment(content.length, " ", Greyscale(1f),
                activeHighlightColours.lastOrNull(), activeUnderlineColours.lastOrNull(),
                activeStrikeThroughColours.lastOrNull(), true))
            content.append(" ".repeat(length))
        }
        else
            content.append(" ".repeat(length))
    }

    @UndocumentedInternal
    fun pushCursor(colour: Colour) {
        cursors.add(Cursor(content.length, colour))
    }

    @UndocumentedInternal
    fun pushHighlightColour(colour: Colour) {
        activeHighlightColours.add(colour)
    }

    @UndocumentedInternal
    fun pushUnderlineColour(colour: Colour) {
        activeUnderlineColours.add(colour)
    }

    @UndocumentedInternal
    fun pushStrikeThroughColour(colour: Colour) {
        activeStrikeThroughColours.add(colour)
    }

    @UndocumentedInternal
    fun popHighlightColour() {
        activeHighlightColours.removeLast()
    }

    @UndocumentedInternal
    fun popUnderlineColour() {
        activeUnderlineColours.removeLast()
    }

    @UndocumentedInternal
    fun popStrikeThroughColour() {
        activeStrikeThroughColours.removeLast()
    }

    ////////////////////////////////////////////////////////////////////////////

    @UndocumentedInternal
    data class Segment(
        val startCharIndex: Int,
        val text: String,
        val textColour: Colour,
        val highlightColour: Colour?,
        val underlineColour: Colour?,
        val strikeThroughColour: Colour?,
        val isWhitespace: Boolean,
    )

    @UndocumentedInternal
    data class Cursor(
        val startCharIndex: Int,
        val colour: Colour,
    )

    ////////////////////////////////////////////////////////////////////////////

    @UndocumentedInternal
    private val activeHighlightColours = mutableListOf<Colour>()

    @UndocumentedInternal
    private val activeUnderlineColours = mutableListOf<Colour>()

    @UndocumentedInternal
    private val activeStrikeThroughColours = mutableListOf<Colour>()

    @UndocumentedInternal
    private val content = StringBuilder()

    @UndocumentedInternal
    private var segments = mutableListOf<Segment>()

    @UndocumentedInternal
    private var cursors = mutableListOf<Cursor>()
}
