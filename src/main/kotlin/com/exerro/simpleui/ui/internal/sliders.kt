package com.exerro.simpleui.ui.internal

import com.exerro.simpleui.DrawContext
import com.exerro.simpleui.Pixels
import com.exerro.simpleui.colour.Colour
import com.exerro.simpleui.colour.Colours
import com.exerro.simpleui.percent
import com.exerro.simpleui.px

/** Draw a slider where [value] describes how far along the control should be
 *  (0-1 inclusive, where 1 is right and "complete" or "full").
 *
 *  **Note:** The standard size for sliders is (192px, 16px). */
internal fun DrawContext.slider(
    value: Float,
    backgroundColour: Colour,
    highlightColour: Colour? = null,
    shadowColour: Colour = Colours.black,
) {
    withRegion(region.resizeTo(height = 6.px)) {
        shadow(cornerRadius = 50.percent, colour = shadowColour)
        roundedRectangle(cornerRadius = 50.percent, colour = backgroundColour)
    }

    withRegion(region.resizeTo(height = 16.px).withAspectRatio(1f, horizontalAlignment = value)) {
        shadow(cornerRadius = 50.percent, colour = shadowColour)
        ellipse(colour = highlightColour ?: backgroundColour)
    }
}

/** Draw a progress bar where [progress] describes how far along the bar should
 *  be (0-1 inclusive, where 1 is right and "complete" or "full"). If
 *  [textColour] is not null, the integer-rounded percentage is written within
 *  the progress bar.
 *
 *  **Note:** The standard size for progress bars is (192px, 24px). */
fun DrawContext.progress(
    progress: Float,
    backgroundColour: Colour,
    highlightColour: Colour,
    textColour: Colour? = null,
    shadowColour: Colour = Colours.black,
) {
    val leftRegion = region.resizeTo(width = Pixels(0f, relative = progress), horizontalAlignment = 0f)
    val rightRegion = region.resizeTo(width = Pixels(0f, relative = 1 - progress), horizontalAlignment = 1f)

    shadow(cornerRadius = 50.percent, colour = shadowColour)
    roundedRectangle(cornerRadius = 50.percent, colour = backgroundColour)

    withRegion(leftRegion, clip = true) {
        withRegion(region.resizeTo(width = region.width.px + region.height.px, horizontalAlignment = 0f), clip = true) {
            roundedRectangle(cornerRadius = 50.percent, colour = highlightColour)
        }
    }

    if (textColour != null) {
        if (progress > 0.5f)
            withRegion(leftRegion) { write("${(progress * 100 + 0.5).toInt()}%", textColour) }
        else
            // TODO: the text colour here can fuck up :(
            withRegion(rightRegion) { write("${((1 - progress) * 100 + 0.5).toInt()}%", textColour) }
    }
}
