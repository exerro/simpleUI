package com.exerro.simpleui

import com.exerro.simpleui.TextBuffer.ContentSegment.Text
import com.exerro.simpleui.TextBuffer.ContentSegment.Whitespace
import com.exerro.simpleui.TextBuffer.Decoration.*
import com.exerro.simpleui.TextBuffer.Line
import kotlin.time.Duration
import kotlin.time.TimeMark

/** A buffer of decorated text. Contains a list of [lines][Line], each of which
 *  contains coloured text segments, decorations (e.g. highlighting/underlining),
 *  and cursors. */
data class TextBuffer<out Colour>(
    val lines: List<Line<Colour>>
) {
    /** A line of text with an indentation. Contains
     *  [coloured text segments][ContentSegment],
     *  [decorations][DecorationSegment] such as highlighted blocks/underlining,
     *  and [cursors][Cursor]. */
    data class Line<out Colour>(
        val indentation: Int,
        val contentSegments: List<ContentSegment<Colour>>,
        val decorationSegments: Set<DecorationSegment<Colour>>,
        val cursors: Set<Cursor<Colour>>,
    )

    /** A section of text, either [whitespace][Whitespace] or [text][Text]. */
    sealed interface ContentSegment<out Colour> {
        /** Length of this segment in characters. */
        val length: Int

        /** A segment of no text content, equal to [length] space characters. */
        data class Whitespace(
            override val length: Int,
        ): ContentSegment<Nothing>

        /** A segment of coloured text. */
        data class Text<out Colour>(
            val text: String,
            val colour: Colour,
        ): ContentSegment<Colour> {
            override val length = text.length
        }
    }

    /** A segment of decoration, type determined by [decoration]. [offset]
     *  specifies the horizontal offset of the decoration in characters
     *  (relative to any alignment and indentation). [length] defines the length
     *  in characters (not pixels). [colour] defines the final colour of the
     *  decoration. */
    data class DecorationSegment<out Colour>(
        val decoration: Decoration,
        val offset: Int,
        val length: Int,
        val colour: Colour,
    )

    /** A cursor offset into the text by [offset] characters. If [resetAt] is
     *  not null, it represents the time when this cursor was "reset" e.g. moved
     *  or initialised, and is used to calculate cursor blinking. [blinkRate]
     *  describes the rate at which the cursor toggles from visible to
     *  invisible. */
    data class Cursor<out Colour>(
        val offset: Int,
        val colour: Colour,
        val resetAt: TimeMark?,
        val blinkRate: Duration,
    ) {
        /** Return true if this cursor is currently visible, false otherwise. */
        fun isVisible(): Boolean {
            if (resetAt == null) return true

            val time = resetAt.elapsedNow()
            val blinkRateNanos = blinkRate.inWholeNanoseconds
            val timeModBlinkRate = time.inWholeNanoseconds % (blinkRateNanos * 2L)

            return timeModBlinkRate < blinkRateNanos
        }

        /** Return the time until the visibility of this cursor changes. If the
         *  cursor is constantly active, [Duration.INFINITE] is returned. */
        fun timeTillVisibilityChanged(): Duration {
            if (resetAt == null) return Duration.INFINITE

            val time = resetAt.elapsedNow()
            val blinkRateNanos = blinkRate.inWholeNanoseconds
            val timeModBlinkRate = time.inWholeNanoseconds % (blinkRateNanos * 2L)

            return if (timeModBlinkRate < blinkRateNanos) {
                Duration.nanoseconds(blinkRateNanos - timeModBlinkRate)
            }
            else {
                Duration.nanoseconds(blinkRateNanos * 2 - timeModBlinkRate)
            }
        }
    }

    /** Enumeration of possible text decorations. Text decoration is some
     *  graphical decoration alongside text.
     *  * [Underline] shows a coloured line below the text.
     *  * [Strikethrough] shows a coloured line passing through the vertical
     *    centre of the text.
     *  * [Highlight] shows a block of colour behind the text spanning
     *    its full height.
     *
     *  If a member defines [background] as true, the decoration is rendered
     *  behind the text. Otherwise, it is rendered in front. */
    enum class Decoration(val background: Boolean) {
        /** See [Decoration] */
        Underline(false),
        /** See [Decoration] */
        Strikethrough(false),
        /** See [Decoration] */
        Highlight(true),
    }
}
