package com.exerro.simpleui

import com.exerro.simpleui.colour.Colour

@Undocumented
class TextBuffer<out Tag>(
    val font: Font,
    val totalHeight: Float,
    val maximumWidth: Float,
    val lines: List<Line<Tag>>,
) {
    @Undocumented
    data class Line<out Tag>(
        val horizontalOffset: Float,
        val segments: List<Segment<Tag>>,
    ) {
        val totalWidth by lazy { segments.fold(0f) { a, b -> a + b.textWidth } }
    }

    @Undocumented
    data class Segment<out Tag>(
        val tag: Tag,
        val horizontalOffset: Float,
        val textWidth: Float,
        val text: String,
        val textColour: Colour,
        val highlightColour: Colour?,
        val strikeThroughColour: Colour?,
        val underlineColour: Colour?,
        val isWhitespace: Boolean,
    )
}
