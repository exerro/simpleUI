package com.exerro.simpleui.extensions

import com.exerro.simpleui.*
import com.exerro.simpleui.colour.Colours
import com.exerro.simpleui.ui.Style

@UndocumentedExperimental
enum class ButtonType {
    Default,
    Primary,
    Disabled,
    Error,
}

@UndocumentedExperimental
object ButtonCornerRadius: Style.Key<Pixels>(
    6.px,
    defaultAccessible = 0.px,
)

/** Draw a standard button, with [text] being drawn in the centre. [focused]
 *  describes whether this button has focus (e.g. keyboard focus). An icon can
 *  optionally be passed and will be drawn at the left of the button.
 *
 *  **Note:** The standard size for buttons is (192px, 32px). */
fun DrawContext.button(
    text: String,
    style: Style,
    type: ButtonType = ButtonType.Default,
    focused: Boolean = false,
    icon: String? = null,
    iconIsResource: Boolean = true,
) {
    val buttonHeight = region.height
    val cornerRadius = style[ButtonCornerRadius]
    val focusUnderlineThickness = style[Style.FocusUnderlineThickness].px
    val shadowRadius = style[Style.ShadowRadius].px
    val shadowOffset = style[Style.ShadowOffset].px
    val backgroundColourKey = when (type) {
        ButtonType.Default -> Style.ElementBackgroundColour
        ButtonType.Primary -> Style.PrimaryBackgroundColour
        ButtonType.Disabled -> Style.DisabledBackgroundColour
        ButtonType.Error -> Style.ErrorBackgroundColour
    }
    val textColourKey = when (type) {
        ButtonType.Default -> Style.ForegroundColour
        ButtonType.Primary -> Style.PrimaryForegroundColour
        ButtonType.Disabled -> Style.DisabledForegroundColour
        ButtonType.Error -> Style.ErrorForegroundColour
    }
    val focusColourKey = when (type) {
        ButtonType.Default -> Style.PrimaryBackgroundColour
        else -> textColourKey
    }

    shadow(cornerRadius = cornerRadius, colour = style[Style.ShadowColour], radius = shadowRadius, offset = shadowOffset)
    roundedRectangle(cornerRadius = cornerRadius, colour = style[backgroundColourKey])
    write(text, style[textColourKey])

    if (focused) {
        region.resizeTo(height = focusUnderlineThickness, width = 80.percent, verticalAlignment = 1f).draw(clip = true) {
            region.resizeTo(height = buttonHeight.px, verticalAlignment = 0f).draw {
                roundedRectangle(cornerRadius = cornerRadius, colour = style[focusColourKey])
            }
        }
    }

    if (icon != null) region
        .resizeTo(width = region.height.px, horizontalAlignment = 0f)
        .withPadding(6.px)
        .draw { image(icon, style[textColourKey], iconIsResource) }
}

/** Draw a standard icon button, with [icon] being drawn in the centre.
 *  [focused] describes whether this button has focus (e.g. keyboard focus).
 *
 *  **Note:** The standard size for icon buttons is (48px, 48px). */
fun DrawContext.iconButton(
    icon: String,
    style: Style,
    type: ButtonType,
    focused: Boolean = false,
    iconIsResource: Boolean = true,
) {
    val focusUnderlineThickness = style[Style.FocusUnderlineThickness].px
    val shadowRadius = style[Style.ShadowRadius].px
    val shadowOffset = style[Style.ShadowOffset].px
    val backgroundColourKey = when (type) {
        ButtonType.Default -> Style.ElementBackgroundColour
        ButtonType.Primary -> Style.PrimaryBackgroundColour
        ButtonType.Disabled -> Style.DisabledBackgroundColour
        ButtonType.Error -> Style.ErrorBackgroundColour
    }
    val textColourKey = when (type) {
        ButtonType.Default -> Style.ForegroundColour
        ButtonType.Primary -> Style.PrimaryForegroundColour
        ButtonType.Disabled -> Style.DisabledForegroundColour
        ButtonType.Error -> Style.ErrorForegroundColour
    }
    val focusColourKey = when (type) {
        ButtonType.Default -> Style.PrimaryBackgroundColour
        else -> textColourKey
    }

    shadow(cornerRadius = 50.percent, colour = style[Style.ShadowColour], radius = shadowRadius, offset = shadowOffset)
    roundedRectangle(cornerRadius = 50.percent, colour = style[backgroundColourKey])

    if (focused) {
        val thisRegion = region
        region.resizeTo(height = 20.percent, width = 100.percent, verticalAlignment = 1f).rounded().draw(clip = true) {
            thisRegion.withPadding(1.px).draw {
                roundedRectangle(cornerRadius = 50.percent, colour = Colours.transparent, borderColour = style[focusColourKey], borderWidth = focusUnderlineThickness)
            }
        }
    }

    region.withPadding(12.px).draw { image(icon, style[textColourKey], iconIsResource) }
}
