package com.exerro.simpleui.extensions

import com.exerro.simpleui.DrawContext
import com.exerro.simpleui.colour.Colour
import com.exerro.simpleui.colour.Colours
import com.exerro.simpleui.percent
import com.exerro.simpleui.px
import kotlin.math.min

/** Draw a dropdown box. [item] is seen as the currently selected item.
 *  [options] represents the full range of options, and is only used when
 *  [selection] is not null. [focused] describes whether this dropdown has focus
 *  (e.g. keyboard focus). [selection] marks the index of the selected item (if
 *  the dropdown should be expanded with an option selected) or is null
 *  otherwise. [iconColour] controls the colour of the expand/contract buttons.
 *  [renderOption] is called in the un-padded region of where an item should be
 *  drawn.
 *
 *  When expanded, the options are rendered below (and outside the region of)
 *  the region specified.
 *
 *  **Note:** The standard size for dropdowns is (192px, 32px). */
fun <T> DrawContext.dropdown(
    item: T,
    options: List<T>,
    backgroundColour: Colour,
    highlightColour: Colour,
    shadowColour: Colour = Colours.black,
    focused: Boolean = false,
    selection: Int? = null,
    iconColour: Colour? = null,
    renderOption: DrawContext.(T) -> Unit,
) {
    val expanded = selection != null && options.isNotEmpty()
    val totalHeight = 1 + (if (expanded) options.size else 0)
    val cornerRadius = (min(region.width, region.height) / 2).px
    val toggleButtonRegion = region.resizeTo(width = region.height.px * 1.5f, horizontalAlignment = 1f)

    region.resizeTo(height = (region.height * totalHeight).px, verticalAlignment = 0f).draw {
        shadow(cornerRadius = cornerRadius, colour = shadowColour)
        roundedRectangle(cornerRadius = cornerRadius, colour = backgroundColour)
    }

    if (!expanded) renderOption(item)

    // draw shadows for the toggle button
    if (focused && expanded && selection == 0) toggleButtonRegion.toLeft().translateBy(dx = 50.percent).draw(clip = true) {
        toggleButtonRegion.resizeTo(height = 200.percent, verticalAlignment = 0f).draw {
            shadow(cornerRadius = cornerRadius, colour = shadowColour)
        }
    }
    else if (focused && expanded) toggleButtonRegion.resizeTo(width = 200.percent, height = 200.percent, 1f, 0f).draw(clip = true) {
        toggleButtonRegion.draw {
            shadow(cornerRadius = cornerRadius, colour = shadowColour)
        }
    }
    else if (focused) toggleButtonRegion.toLeft().translateBy(dx = 50.percent).draw(clip = true) {
        toggleButtonRegion.draw {
            shadow(cornerRadius = cornerRadius, colour = shadowColour)
        }
    }

    // selection shadow
    if (focused && expanded && selection == 0) {
        region.below().draw {
            shadow(cornerRadius = cornerRadius, colour = shadowColour)
        }
    }

    // draw the toggle button content
    toggleButtonRegion.draw(clip = true) {
        if (focused && expanded && selection == 0) {
            region.resizeTo(
                height = (region.height * 2).px,
                verticalAlignment = 0f,
            ).draw {
                roundedRectangle(cornerRadius = cornerRadius, colour = highlightColour)
            }
        }
        else if (focused) {
            roundedRectangle(cornerRadius = cornerRadius, colour = highlightColour)
        }

        region.resizeTo(height = 50.percent).withAspectRatio(1f).draw {
            if (expanded) image("images/contract.png", iconColour)
            else image("images/expand.png", iconColour)
        }
    }

    if (expanded) {
        options.fold(0 to region.below()) { (index, region), option ->
            region.draw {
                if (focused && index == selection) {
                    if (index > 0) shadow(cornerRadius = cornerRadius, colour = shadowColour)
                    roundedRectangle(cornerRadius = cornerRadius, colour = highlightColour)

                    if (selection == 0) {
                        region.resizeTo(50.percent, 50.percent, 1f, 0f).draw {
                            fill(colour = highlightColour)
                        }
                    }
                }
                renderOption(option)
            }
            (index + 1) to region.below()
        }
    }
}

/** A dropdown where the options are strings. See [dropdown]. */
fun DrawContext.dropdown(
    item: String,
    options: List<String>,
    backgroundColour: Colour,
    highlightColour: Colour,
    textColour: Colour = Colours.white,
    shadowColour: Colour = Colours.black,
    focused: Boolean = false,
    selection: Int? = null,
    iconColour: Colour? = null,
) = dropdown(
    item = item,
    options = options,
    backgroundColour = backgroundColour,
    highlightColour = highlightColour,
    shadowColour = shadowColour,
    focused = focused,
    selection = selection,
    iconColour = iconColour,
) {
    region.withPadding(left = 16.px).draw {
        write(it, textColour, horizontalAlignment = 0f)
    }
}
