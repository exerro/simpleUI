package com.exerro.simpleui

import java.text.Format
import kotlin.experimental.ExperimentalTypeInference

@Undocumented
class FormattedText<out Extra> internal constructor(
    internal val isNewline: Boolean,
    internal val lines: List<List<Segment<Extra>>>
): Iterable<FormattedText.Segment<Extra>> {
    @Undocumented
    fun <NewExtra> withExtra(
        extra: NewExtra
    ): FormattedText<NewExtra> = FormattedText(isNewline, lines.map { l -> l.map { when (it) {
        is Segment.LineBreak -> it
        is Segment.Text -> Segment.Text(it.text, it.colour, extra)
        is Segment.Whitespace -> it
    } } })

    // TODO: improve this!
    override fun iterator() = lines.flatten().iterator()

    @Undocumented
    sealed interface Segment<out Extra> {
        @Undocumented
        data class Text<out Extra>(
            val text: String,
            val colour: PaletteColour,
            val extra: Extra
        ): Segment<Extra>

        @Undocumented
        data class Whitespace(
            val length: Int
        ): Segment<Nothing>

        @Undocumented
        data class LineBreak(
            val relativeIndent: Int
        ): Segment<Nothing>
    }

    companion object {
        @Undocumented
        val empty = FormattedText<Nothing>(false, listOf(emptyList()))

        @Undocumented
        val whitespace = FormattedText(false, listOf(listOf(Segment.Whitespace(1))))

        @Undocumented
        fun whitespace(length: Int = 1) = FormattedText(false, listOf(listOf(Segment.Whitespace(length))))

        @Undocumented
        val lineBreak = FormattedText(true, listOf(listOf(Segment.LineBreak(0))))

        @Undocumented
        fun lineBreak(relativeIndent: Int = 0) = FormattedText(true, listOf(listOf(Segment.LineBreak(relativeIndent))))

        @Undocumented
        fun <Extra> text(
            text: String,
            colour: PaletteColour,
            extra: Extra
        ) = FormattedText(false, listOf(listOf(Segment.Text(text, colour, extra))))

        @Undocumented
        fun text(
            text: String,
            colour: PaletteColour,
        ) = text(text, colour, Unit)

        @Undocumented
        fun <Extra> builder(
            defaultExtra: Extra,
            builder: FormattedTextBuilder<Extra>.() -> FormattedText<Extra>
        ) = object: FormattedTextBuilder<Extra> {} .builder()

        @Undocumented
        fun builder(
            builder: FormattedTextBuilder<Unit>.() -> FormattedText<Unit>
        ) = builder(defaultExtra = Unit, builder)
    }
}

@Undocumented
operator fun <Extra> FormattedText<Extra>.plus(
    other: FormattedText<Extra>
): FormattedText<Extra> = FormattedText(other.isNewline, when {
    isNewline -> lines + other.lines
    other.lines.isEmpty() -> lines
    else -> lines.dropLast(1) + listOf(lines.last() + other.lines.first()) + other.lines.drop(1)
})

@Undocumented
fun <Extra> List<FormattedText<Extra>>.flatten(
    delim: FormattedText<Extra> = FormattedText.empty,
): FormattedText<Extra> = when {
    isEmpty() -> FormattedText.empty
    else -> drop(1).fold(this[0]) { a, b -> a + delim + b }
}
