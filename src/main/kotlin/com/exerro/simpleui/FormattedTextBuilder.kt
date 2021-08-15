package com.exerro.simpleui

@Undocumented
interface FormattedTextBuilder<Extra> {
    @Undocumented
    fun String.coloured(colour: PaletteColour) = FormattedText.text(this, colour)

    val red get() = PaletteColour.Red()
    val orange get() = PaletteColour.Orange()
    val yellow get() = PaletteColour.Yellow()
    val green get() = PaletteColour.Green()
    val teal get() = PaletteColour.Teal()
    val blue get() = PaletteColour.Blue()
    val purple get() = PaletteColour.Purple()
    val pink get() = PaletteColour.Pink()

    @Undocumented
    val empty get() = FormattedText<Nothing>(false, listOf(emptyList()))

    @Undocumented
    val whitespace get() = FormattedText(false, listOf(listOf(FormattedText.Segment.Whitespace(1))))

    @Undocumented
    val lineBreak get() = FormattedText(true, listOf(listOf(FormattedText.Segment.LineBreak(0))))

    @Undocumented
    fun whitespace(length: Int = 1) = FormattedText(false, listOf(listOf(FormattedText.Segment.Whitespace(length))))

    @Undocumented
    fun lineBreak(relativeIndent: Int = 0) = FormattedText(true, listOf(listOf(FormattedText.Segment.LineBreak(relativeIndent))))
}
