package com.exerro.simpleui.extensions

import com.exerro.simpleui.DrawContext
import com.exerro.simpleui.colour.Colour
import com.exerro.simpleui.colour.Colours
import com.exerro.simpleui.percent
import com.exerro.simpleui.px

/** Draw a standard button, with [text] being drawn in the centre. [focused]
 *  describes whether this button has focus (e.g. keyboard focus). An icon can
 *  optionally be passed and will be drawn at the left of the button.
 *
 *  **Note:** The standard size for buttons is (192px, 32px). */
fun DrawContext.button(
    text: String,
    backgroundColour: Colour,
    highlightColour: Colour = Colours.teal,
    textColour: Colour = Colours.white,
    shadowColour: Colour = Colours.black,
    focused: Boolean = false,
    icon: String? = null,
    iconIsResource: Boolean = true,
) {
    shadow(cornerRadius = 6.px, colour = shadowColour, radius = 10.px)
    roundedRectangle(cornerRadius = 6.px, colour = backgroundColour)
    write(text, textColour)

    if (focused) {
        region.resizeTo(height = 2.px, width = 80.percent, verticalAlignment = 1f).draw(clip = true) {
            region.resizeTo(height = 32.px, verticalAlignment = 0f).draw {
                roundedRectangle(cornerRadius = 6.px, colour = highlightColour)
            }
        }
    }

    if (icon != null) {
        val iconRegion = region.resizeTo(width = region.height.px, horizontalAlignment = 0f)
        iconRegion.withPadding(6.px).draw { image(icon, textColour, iconIsResource) }
    }
}

/** Draw a standard icon button, with [icon] being drawn in the centre.
 *  [focused] describes whether this button has focus (e.g. keyboard focus).
 *
 *  **Note:** The standard size for icon buttons is (48px, 48px). */
fun DrawContext.iconButton(
    icon: String,
    backgroundColour: Colour,
    highlightColour: Colour = Colours.teal,
    iconColour: Colour = Colours.white,
    shadowColour: Colour = Colours.black,
    focused: Boolean = false,
    iconIsResource: Boolean = true,
) {
    shadow(cornerRadius = 50.percent, colour = shadowColour, radius = 10.px)
    roundedRectangle(cornerRadius = 50.percent, colour = backgroundColour)

    if (focused) {
        val thisRegion = region
        region.resizeTo(height = 5.px, width = 100.percent, verticalAlignment = 1f).draw(clip = true) {
            thisRegion.withPadding(1.px).draw {
                roundedRectangle(cornerRadius = 50.percent, colour = backgroundColour, borderColour = highlightColour, borderWidth = 2.px)
            }
        }
    }

    region.withPadding(12.px).draw { image(icon, iconColour, iconIsResource) }
}
