package com.exerro.simpleui.extensions

import com.exerro.simpleui.*
import com.exerro.simpleui.colour.Colour
import com.exerro.simpleui.colour.Colours

/** Draw a text input box, with [textBuffer] used as its content. [focused]
 *  describes whether this input has focus (e.g. keyboard focus). An icon can
 *  optionally be passed and will be drawn at the left of the input.
 *
 *  **Note:** The standard height for inputs is 48px. Width should be at least
 *  192px. */
fun DrawContext.textInput(
    textBuffer: TextBuffer<Colour>,
    backgroundColour: Colour,
    highlightColour: Colour,
    shadowColour: Colour = Colours.black,
    focused: Boolean = false,
    icon: String? = null,
    iconColour: Colour? = null,
    iconIsResource: Boolean = true,
) {
    val textRegion = region.withPadding(left = (if (icon != null) region.height else 16f).px)

    shadow(colour = shadowColour, cornerRadius = 4.px)
    roundedRectangle(4.px, backgroundColour)

    if (icon != null) {
        val iconRegion = region.resizeTo(width = region.height.px, horizontalAlignment = 0f)
        iconRegion.withPadding(12.px).draw { image(icon, iconColour, iconIsResource) }
    }

    textRegion.draw { write(textBuffer, horizontalAlignment = 0f) }

    if (focused) {
        region.resizeTo(height = 2.px, width = 100.percent - 32.px, verticalAlignment = 1f).draw(clip = true) {
            region.resizeTo(height = 32.px, verticalAlignment = 0f).draw {
                roundedRectangle(cornerRadius = 4.px, colour = highlightColour)
            }
        }
    }
}
