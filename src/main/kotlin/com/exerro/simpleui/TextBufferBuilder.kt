package com.exerro.simpleui

import com.exerro.simpleui.colour.Colour
import com.exerro.simpleui.colour.Colours
import kotlin.time.Duration
import kotlin.time.TimeMark

/** Used to create [TextBuffer]s in a nice way. */
@DrawContextDSL
interface TextBufferBuilder<Colour> {
    /** Default text colour of this builder. */
    val defaultColour: Colour

    /** Emit a coloured text segment. If [colour] is null, [defaultColour] is
     *  used. */
    fun emitText(text: String, colour: Colour? = null)

    /** Emit a multitude of text segments by splitting the [text] at newlines
     *  and spaces and emitting line breaks and whitespace as appropriate.
     *  This can be used to facilitate word-wrapping, since word-wrapping wraps
     *  on segment boundaries. */
    fun emitTextSegments(text: String, colour: Colour? = null) {
        for ((lineNumber, line) in text.split('\n').withIndex()) {
            if (lineNumber > 0) emitLineBreak()
            for ((index, word) in line.split(' ').withIndex()) {
                if (index > 0) emitWhitespace()
                emitText(word, colour)
            }
        }
    }

    /** Emit [length] characters of whitespace. [length] defaults to 1. */
    fun emitWhitespace(length: Int = 1)

    /** Emit a line break, adjusting the indentation of the next line by
     *  [relativeIndentation] (relative to this line). [relativeIndentation]
     *  defaults to 0 (same indentation for the next line). */
    fun emitLineBreak(relativeIndentation: Int = 0)

    /** Emit a cursor at the current position in the text. If [colour] is null,
     *  [defaultColour] is used. [resetAt] is used to facilitate cursor blinking
     *  and represents when the cursor was "reset" (e.g. moved, created).
     *  [blinkRate] describes the rate at which the cursor toggles from visible
     *  to invisible. */
    fun emitCursor(colour: Colour? = null, resetAt: TimeMark? = null, blinkRate: Duration = Duration.seconds(0.5))

    /** Begin a type decoration from the next character. Decorations can stack
     *  and multiple of any type can be active at any given time, although only
     *  the most recent decoration of a given type is visually active at a time. */
    fun beginDecoration(decoration: TextBuffer.Decoration, colour: Colour? = null)

    /** Complete and stop the last decoration of the same type that was begun. */
    fun stopDecoration(decoration: TextBuffer.Decoration)

    /** A default implementation of a [TextBufferBuilder]. See also: [invoke]. */
    class Implementation<Colour>(
        override val defaultColour: Colour,
        initialIndentation: Int = 0,
    ): TextBufferBuilder<Colour> {
        /** Complete the buffer and return it. Note: this is safe to call
         *  multiple times, if further modifications are made. This method does
         *  not reset the accumulated buffer when called. */
        fun build(): TextBuffer<Colour> {
            return TextBuffer(lines + buildLine())
        }

        ////////////////////////////////////////////////////////////////////////

        override fun emitText(text: String, colour: Colour?) {
            activeLineContent.add(TextBuffer.ContentSegment.Text(text, colour ?: defaultColour))
            thisLineLength += text.length
        }

        override fun emitWhitespace(length: Int) {
            activeLineContent.add(TextBuffer.ContentSegment.Whitespace(length))
            thisLineLength += length
        }

        override fun emitLineBreak(relativeIndentation: Int) {
            lines.add(buildAndResetLine())
            indentation += relativeIndentation
        }

        override fun emitCursor(colour: Colour?, resetAt: TimeMark?, blinkRate: Duration) {
            activeLineCursors.add(TextBuffer.Cursor(thisLineLength, colour ?: defaultColour, resetAt, blinkRate))
        }

        override fun beginDecoration(decoration: TextBuffer.Decoration, colour: Colour?) {
            insertCurrentDecoration(decoration)
            activeDecorationColours[decoration]!!.add(colour ?: defaultColour)
        }

        override fun stopDecoration(decoration: TextBuffer.Decoration) {
            insertCurrentDecoration(decoration)
            activeDecorationColours[decoration]!!.removeLastOrNull()
        }

        ////////////////////////////////////////////////////////////////////////

        @UndocumentedInternal
        private fun insertCurrentDecoration(decoration: TextBuffer.Decoration) {
            if (activeDecorationColours[decoration]!!.isEmpty()) {
                activeDecorationOffsets[decoration] = thisLineLength
                return
            }

            val offset = activeDecorationOffsets[decoration]!!

            // if there's no content decorated, return and do nothing
            if (thisLineLength <= offset) return

            activeLineDecorations.add(TextBuffer.DecorationSegment(
                decoration = decoration,
                offset = offset,
                length = thisLineLength - offset,
                colour = activeDecorationColours[decoration]!!.last(),
            ))

            activeDecorationOffsets[decoration] = thisLineLength
        }

        @UndocumentedInternal
        private fun buildLine(): TextBuffer.Line<Colour> {
            for (decoration in TextBuffer.Decoration.values())
                insertCurrentDecoration(decoration)

            return TextBuffer.Line(
                indentation = indentation,
                contentSegments = activeLineContent.toList(), // copy the list
                decorationSegments = activeLineDecorations.toSet(), // copy the set
                cursors = activeLineCursors.toSet(), // copy the set
            )
        }

        @UndocumentedInternal
        private fun buildAndResetLine(): TextBuffer.Line<Colour> {
            val line = buildLine()

            activeLineContent.clear()
            activeLineDecorations.clear()
            activeLineCursors.clear()
            thisLineLength = 0

            for (decoration in TextBuffer.Decoration.values())
                activeDecorationOffsets[decoration] = 0

            return line
        }

        private val lines = mutableListOf<TextBuffer.Line<Colour>>()
        private val activeLineContent = mutableListOf<TextBuffer.ContentSegment<Colour>>()
        private val activeLineDecorations = mutableSetOf<TextBuffer.DecorationSegment<Colour>>()
        private val activeLineCursors = mutableSetOf<TextBuffer.Cursor<Colour>>()
        private val activeDecorationColours = TextBuffer.Decoration.values().associateWith { mutableListOf<Colour>() }
        private val activeDecorationOffsets = TextBuffer.Decoration.values().associateWith { 0 } .toMutableMap()
        private var thisLineLength = 0
        private var indentation = initialIndentation
    }

    companion object {
        /** Construct a [TextBuffer] using [builder]. */
        operator fun <Colour> invoke(
            defaultColour: Colour,
            initialIndentation: Int = 0,
            builder: TextBufferBuilder<Colour>.() -> Unit,
        ): TextBuffer<Colour> {
            val builderInstance = Implementation(
                defaultColour = defaultColour,
                initialIndentation = initialIndentation,
            )
            builderInstance.builder()
            return builderInstance.build()
        }

        /** Construct a [TextBuffer] using [builder]. */
        operator fun invoke(
            initialIndentation: Int = 0,
            builder: TextBufferBuilder<Colour>.() -> Unit,
        ) = invoke(
            defaultColour = Colours.white,
            initialIndentation = initialIndentation,
            builder = builder,
        )

        /** Construct a [TextBuffer] using [text] and [colour]. If
         *  [splitSegments] is true, the text is split on newlines and spaces,
         *  enabling word wrapping. Otherwise, a single segment is produced. */
        operator fun <Colour> invoke(
            text: String,
            colour: Colour,
            splitSegments: Boolean = true,
        ) = invoke(defaultColour = colour) {
            if (splitSegments) emitTextSegments(text, colour)
            else emitText(text)
        }

        /** Construct a [TextBuffer] using [text]. If [splitSegments] is true,
         *  the text is split on newlines and spaces, enabling word wrapping.
         *  Otherwise, a single segment is produced. */
        operator fun invoke(
            text: String,
            splitSegments: Boolean = true,
        ) = invoke(text = text, colour = Colours.white, splitSegments = splitSegments)
    }
}
