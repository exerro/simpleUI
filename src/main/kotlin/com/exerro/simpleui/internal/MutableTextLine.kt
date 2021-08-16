package com.exerro.simpleui.internal

import com.exerro.simpleui.PaletteColour
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
    fun pushText(text: String, colour: PaletteColour) {
        segments.add(Segment(content.length, text, colour,
            activeHighlightColours.lastOrNull(), activeUnderlineColours.lastOrNull(), false))
        content.append(text)
    }

    @UndocumentedInternal
    fun pushWhitespace(length: Int) {
        if (activeHighlightColours.isNotEmpty() || activeUnderlineColours.isNotEmpty()) {
            segments.add(Segment(content.length, " ", PaletteColour.White(),
                activeHighlightColours.lastOrNull(), activeUnderlineColours.lastOrNull(), true))
            content.append(" ".repeat(length))
        }
        else
            content.append(" ".repeat(length))
    }

    @UndocumentedInternal
    fun pushCursor(colour: PaletteColour) {
        cursors.add(Cursor(content.length, colour))
    }

    @UndocumentedInternal
    fun pushHighlightColour(colour: PaletteColour) {
        activeHighlightColours.add(colour)
    }

    @UndocumentedInternal
    fun pushUnderlineColour(colour: PaletteColour) {
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
        val textColour: PaletteColour,
        val highlightColour: PaletteColour?,
        val underlineColour: PaletteColour?,
        val isWhitespace: Boolean,
    )

    @UndocumentedInternal
    data class Cursor(
        val startCharIndex: Int,
        val colour: PaletteColour,
    )

    ////////////////////////////////////////////////////////////////////////////

    @UndocumentedInternal
    private val activeHighlightColours = mutableListOf<PaletteColour>()

    @UndocumentedInternal
    private val activeUnderlineColours = mutableListOf<PaletteColour>()

    @UndocumentedInternal
    private val content = StringBuilder()

    @UndocumentedInternal
    private var segments = mutableListOf<Segment>()

    @UndocumentedInternal
    private var cursors = mutableListOf<Cursor>()
}
