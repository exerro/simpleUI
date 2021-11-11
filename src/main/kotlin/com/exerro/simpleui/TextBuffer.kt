package com.exerro.simpleui

@Undocumented
data class TextBuffer<out Colour>(
    val lines: List<Line<Colour>>
) {
    @Undocumented
    data class Line<out Colour>(
        val indentation: Int,
        val contentSegments: List<ContentSegment<Colour>>,
        val decorationSegments: Set<DecorationSegment<Colour>>,
    )

    @Undocumented
    sealed interface ContentSegment<out Colour> {
        @Undocumented
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
