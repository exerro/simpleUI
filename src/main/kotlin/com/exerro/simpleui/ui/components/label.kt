package com.exerro.simpleui.ui.components

import com.exerro.simpleui.*
import com.exerro.simpleui.colour.Colour
import com.exerro.simpleui.ui.*

@UndocumentedExperimental
fun ComponentChildrenContext<*, *, *, *, *>.label(
    text: TextBuffer<Colour>,
    font: Font = Font.default,
    horizontalAlignment: Alignment = 0.5f,
    verticalAlignment: Alignment = 0.5f,
    icon: Image? = null,
) = rawComponent("button") {
    onDraw {
        write(text, font = font, horizontalAlignment = horizontalAlignment, verticalAlignment = verticalAlignment)

        if (icon != null) region
            .resizeTo(width = region.height.px, horizontalAlignment = 0f)
            .withPadding(6.px)
            .draw { image(icon.image, model.style[Style.ForegroundColour], icon.imageIsResource) }
    }

    noChildrenDeclareDefaultSize(width = 192f, height = font.lineHeight * text.lines.size)
}

@UndocumentedExperimental
fun ComponentChildrenContext<*, *, *, *, *>.label(
    text: String,
    font: Font = Font.default,
    horizontalAlignment: Alignment = 0.5f,
    verticalAlignment: Alignment = 0.5f,
    colour: Colour? = null,
    icon: Image? = null,
) = label(
    text = TextBufferBuilder(text, colour ?: model.style[Style.ForegroundColour]),
    font = font,
    horizontalAlignment = horizontalAlignment,
    verticalAlignment = verticalAlignment,
    icon = icon,
)
