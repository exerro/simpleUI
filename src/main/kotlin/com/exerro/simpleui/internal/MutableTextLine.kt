package com.exerro.simpleui.internal

import com.exerro.simpleui.RGB
import com.exerro.simpleui.UndocumentedInternal

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
    fun pushText(text: String, colour: RGB) {
        segments.add(Segment(content.length, text, colour,
            activeHighlightColours.lastOrNull(), activeUnderlineColours.lastOrNull(), false))
        content.append(text)
    }

    @UndocumentedInternal
    fun pushWhitespace(length: Int) {
        if (activeHighlightColours.isNotEmpty() || activeUnderlineColours.isNotEmpty()) {
            segments.add(Segment(content.length, " ", RGB(1f),
                activeHighlightColours.lastOrNull(), activeUnderlineColours.lastOrNull(), true))
            content.append(" ".repeat(length))
        }
        else
            content.append(" ".repeat(length))
    }

    @UndocumentedInternal
    fun pushCursor(colour: RGB) {
        cursors.add(Cursor(content.length, colour))
    }

    @UndocumentedInternal
    fun pushHighlightColour(colour: RGB) {
        activeHighlightColours.add(colour)
    }

    @UndocumentedInternal
    fun pushUnderlineColour(colour: RGB) {
        activeUnderlineColours.add(colour)
    }

    @UndocumentedInternal
    fun popHighlightColour() {
        activeHighlightColours.removeLast()
    }

    @UndocumentedInternal
    fun popUnderlineColour() {
        activeUnderlineColours.removeLast()
    }

    ////////////////////////////////////////////////////////////////////////////

    @UndocumentedInternal
    data class Segment(
        val startCharIndex: Int,
        val text: String,
        val textColour: RGB,
        val highlightColour: RGB?,
        val underlineColour: RGB?,
        val isWhitespace: Boolean,
    )

    @UndocumentedInternal
    data class Cursor(
        val startCharIndex: Int,
        val colour: RGB,
    )

    ////////////////////////////////////////////////////////////////////////////

    @UndocumentedInternal
    private val activeHighlightColours = mutableListOf<RGB>()

    @UndocumentedInternal
    private val activeUnderlineColours = mutableListOf<RGB>()

    @UndocumentedInternal
    private val content = StringBuilder()

    @UndocumentedInternal
    private var segments = mutableListOf<Segment>()

    @UndocumentedInternal
    private var cursors = mutableListOf<Cursor>()
}
