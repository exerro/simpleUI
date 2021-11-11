package com.exerro.simpleui

import com.exerro.simpleui.colour.Colour
import com.exerro.simpleui.colour.Colours

@Undocumented
@DrawContextDSL
interface TextBufferBuilder<Colour> {
    @Undocumented
    val defaultColour: Colour

    @Undocumented
    fun emitText(text: String, colour: Colour? = null)

    @Undocumented
    fun emitTextSegments(text: String, colour: Colour? = null) {
        for ((lineNumber, line) in text.split('\n').withIndex()) {
            if (lineNumber > 0) emitLineBreak()
            for ((index, word) in line.split(' ').withIndex()) {
                if (index > 0) emitWhitespace()
                emitText(word, colour)
            }
        }
    }

    @Undocumented
    fun emitWhitespace(length: Int = 1)

    @Undocumented
    fun emitLineBreak(relativeIndentation: Int = 0)

    @Undocumented
    fun beginDecoration(decoration: TextBuffer.Decoration, colour: Colour? = null)

    @Undocumented
    fun stopDecoration(decoration: TextBuffer.Decoration)

    @Undocumented
    class Implementation<Colour>(
        override val defaultColour: Colour,
        initialIndentation: Int = 0,
    ): TextBufferBuilder<Colour> {
        @Undocumented
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
            )
        }

        @UndocumentedInternal
        private fun buildAndResetLine(): TextBuffer.Line<Colour> {
            val line = buildLine()

            activeLineContent.clear()
            activeLineDecorations.clear()
            thisLineLength = 0

            for (decoration in TextBuffer.Decoration.values())
                activeDecorationOffsets[decoration] = 0

            return line
        }

        private val lines = mutableListOf<TextBuffer.Line<Colour>>()
        private val activeLineContent = mutableListOf<TextBuffer.ContentSegment<Colour>>()
        private val activeLineDecorations = mutableSetOf<TextBuffer.DecorationSegment<Colour>>()
        private val activeDecorationColours = TextBuffer.Decoration.values().associateWith { mutableListOf<Colour>() }
        private val activeDecorationOffsets = TextBuffer.Decoration.values().associateWith { 0 } .toMutableMap()
        private var thisLineLength = 0
        private var indentation = initialIndentation
    }

    companion object {
        @Undocumented
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

        @Undocumented
        operator fun invoke(
            initialIndentation: Int = 0,
            builder: TextBufferBuilder<Colour>.() -> Unit,
        ) = invoke(
            defaultColour = Colours.white,
            initialIndentation = initialIndentation,
            builder = builder,
        )

        @Undocumented
        operator fun <Colour> invoke(
            text: String,
            colour: Colour,
            splitSegments: Boolean = true,
        ) = invoke(defaultColour = colour) {
            if (splitSegments) emitTextSegments(text, colour)
            else emitText(text)
        }

        @Undocumented
        operator fun invoke(
            text: String,
            splitSegments: Boolean = true,
        ) = invoke(text = text, colour = Colours.white, splitSegments = splitSegments)
    }
}
