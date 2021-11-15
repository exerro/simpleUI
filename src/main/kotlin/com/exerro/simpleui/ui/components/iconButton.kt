package com.exerro.simpleui.ui.components

import com.exerro.simpleui.UndocumentedExperimental
import com.exerro.simpleui.colour.Colour
import com.exerro.simpleui.colour.Colours
import com.exerro.simpleui.percent
import com.exerro.simpleui.px
import com.exerro.simpleui.ui.*
import com.exerro.simpleui.ui.standardActions.SelectEntity

@UndocumentedExperimental
fun ParentContext<*, *, *, *, *>.iconButton(
    icon: Image,
    type: ButtonType = ButtonType.Default,
    focused: Boolean = false,
    action: Action = SelectEntity,
    behaviour: () -> Unit = {},
) = rawComponent("iconButton") {
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
    val focusUnderlineThickness = model.style[Style.FocusUnderlineThickness].px
    val shadowRadius = model.style[Style.ShadowRadius].px
    val shadowOffset = model.style[Style.ShadowOffset].px

    if (focused) bind(action) {
        behaviour()
        true
    }

    onDraw {
        shadow(cornerRadius = 50.percent, colour = model.style[shadowColourKey], radius = shadowRadius, offset = shadowOffset)
        roundedRectangle(cornerRadius = 50.percent, colour = model.style[backgroundColourKey])

        if (focused) {
            val thisRegion = region
            region.resizeTo(height = 20.percent, width = 100.percent, verticalAlignment = 1f).rounded().draw(clip = true) {
                thisRegion.withPadding(1.px).draw {
                    roundedRectangle(cornerRadius = 50.percent, colour = Colours.transparent, borderColour = model.style[focusColourKey], borderWidth = focusUnderlineThickness)
                }
            }
        }

        region.withPadding(12.px).draw { image(icon.image, model.style[foregroundColourKey], icon.imageIsResource) }
    }

    noChildrenDeclareDefaultSize(width = 48f, height = 48f)
}
