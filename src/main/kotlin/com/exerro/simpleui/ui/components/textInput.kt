package com.exerro.simpleui.ui.components

import com.exerro.simpleui.TextBuffer
import com.exerro.simpleui.UndocumentedExperimentalUI
import com.exerro.simpleui.colour.Colour
import com.exerro.simpleui.percent
import com.exerro.simpleui.px
import com.exerro.simpleui.ui.*
import com.exerro.simpleui.ui.extensions.noChildrenDefineDefaultSize

// TODO!
@UndocumentedExperimentalUI
inline fun <reified Width: WhoDefinesMe, reified Height: WhoDefinesMe> ComponentChildrenContext<*, Width, Height>.textInput(
    textBuffer: TextBuffer<Colour>,
    type: TextInputType = TextInputType.Default,
    focused: Boolean = false,
    icon: Image? = null,
) = component("textInput") {
    val backgroundColour = model.style[Style.ElementBackgroundColour]
    val foregroundColour = model.style[Style.ForegroundColour]
    val focusColour = model.style[when (type) {
        TextInputType.Default -> Style.PrimaryBackgroundColour
        TextInputType.Disabled -> Style.DisabledBackgroundColour
        TextInputType.Invalid -> Style.ErrorBackgroundColour
    }]
    val shadowColour = model.style[Style.ShadowColour]
    val cornerRadius = model.style[Style.TextInputCornerRadius]
    val focusUnderlineThickness = model.style[Style.FocusUnderlineThickness].px
    val shadowRadius = model.style[Style.ShadowRadius].px
    val shadowOffset = model.style[Style.ShadowOffset].px

    onDraw {
        val textRegion = region.withPadding(left = (if (icon != null) region.height else 16f).px)

        shadow(colour = shadowColour, cornerRadius = cornerRadius, radius = shadowRadius, offset = shadowOffset)
        roundedRectangle(cornerRadius, backgroundColour)

        if (icon != null) {
            val iconRegion = region
                .resizeTo(width = region.height.px, horizontalAlignment = 0f)
                .withPadding(12.px)

            withRegion(iconRegion) { image(icon.image, foregroundColour, icon.imageIsResource) }
        }

        withRegion(textRegion) { write(textBuffer, horizontalAlignment = 0f) }

        if (focused) {
            withRegion(region.resizeTo(height = focusUnderlineThickness, width = 100.percent - 32.px, verticalAlignment = 1f), clip = true) {
                withRegion(region.resizeTo(height = 32.px, verticalAlignment = 0f)) {
                    roundedRectangle(cornerRadius = cornerRadius, colour = focusColour)
                }
            }
        }
    }

    noChildrenDefineDefaultSize(width = 192f, height = 48f)
}
