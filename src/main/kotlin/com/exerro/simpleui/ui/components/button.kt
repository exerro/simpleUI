package com.exerro.simpleui.ui.components

import com.exerro.simpleui.*
import com.exerro.simpleui.colour.Colour
import com.exerro.simpleui.ui.*
import com.exerro.simpleui.ui.standardActions.SelectEntity

@UndocumentedExperimentalUI
fun AnyComponentChildrenContext.button(
    text: TextBuffer<Colour>,
    type: ButtonType = ButtonType.Default,
    focused: Boolean = false,
    icon: Image? = null,
    action: Action = SelectEntity,
    behaviour: () -> Unit = {},
) = rawComponent("button") {
    val backgroundColourKey = when (type) {
        ButtonType.Default -> Style.ElementBackgroundColour
        ButtonType.Primary -> Style.PrimaryBackgroundColour
        ButtonType.Disabled -> Style.DisabledBackgroundColour
        ButtonType.Error -> Style.ErrorBackgroundColour
    }
    val foregroundColourKey = when (type) {
        ButtonType.Default -> Style.ForegroundColour
        ButtonType.Primary -> Style.PrimaryForegroundColour
        ButtonType.Disabled -> Style.DisabledForegroundColour
        ButtonType.Error -> Style.ErrorForegroundColour
    }
    val focusColourKey = when (type) {
        ButtonType.Default -> Style.PrimaryBackgroundColour
        else -> foregroundColourKey
    }
    val shadowColourKey = when (type) {
        ButtonType.Default -> Style.ShadowColour
        else -> Style.AlternateShadowColour
    }
    val cornerRadius = model.style[Style.ButtonCornerRadius]
    val focusUnderlineThickness = model.style[Style.FocusUnderlineThickness].px
    val shadowRadius = model.style[Style.ShadowRadius].px
    val shadowOffset = model.style[Style.ShadowOffset].px

    if (focused) bind(action) {
        behaviour()
        true
    }

    onDraw {
        val buttonHeight = region.height

        shadow(cornerRadius = cornerRadius, colour = model.style[shadowColourKey], radius = shadowRadius, offset = shadowOffset)
        roundedRectangle(cornerRadius = cornerRadius, colour = model.style[backgroundColourKey])
        write(text)

        if (focused) {
            withRegion(region.resizeTo(height = focusUnderlineThickness, width = 80.percent, verticalAlignment = 1f), clip = true) {
                withRegion(region.resizeTo(height = buttonHeight.px, verticalAlignment = 0f)) {
                    roundedRectangle(cornerRadius = cornerRadius, colour = model.style[focusColourKey])
                }
            }
        }

        if (icon != null) {
            val iconRegion = region
                .resizeTo(width = region.height.px, horizontalAlignment = 0f)
                .withPadding(6.px)

            withRegion(iconRegion) { image(icon.image, model.style[foregroundColourKey], icon.imageIsResource) }
        }
    }

    noChildrenDefineDefaultSize(width = 192f, height = 32f)
}

@UndocumentedExperimentalUI
fun ComponentChildrenContext<*, *, *, *, *>.button(
    text: String,
    colour: Colour? = null,
    type: ButtonType = ButtonType.Default,
    focused: Boolean = false,
    icon: Image? = null,
    action: Action = SelectEntity,
    behaviour: () -> Unit = {},
) = button(
    text = TextBufferBuilder(text, colour ?: model.style[when (type) {
        ButtonType.Default -> Style.ForegroundColour
        ButtonType.Primary -> Style.PrimaryForegroundColour
        ButtonType.Disabled -> Style.DisabledForegroundColour
        ButtonType.Error -> Style.ErrorForegroundColour
    }]),
    type = type,
    focused = focused,
    icon = icon,
    action = action,
    behaviour = behaviour,
)
