package com.exerro.simpleui.ui.modifiers

import com.exerro.simpleui.Alignment
import com.exerro.simpleui.UndocumentedExperimental
import com.exerro.simpleui.px
import com.exerro.simpleui.ui.*

@UndocumentedExperimental
fun <Model: UIModel, ParentHeight: Float?, ChildHeight: Float?> ComponentChildrenContext<Model, Float, ParentHeight, Nothing?, ChildHeight>.withHorizontalAlignment(
    horizontalAlignment: Alignment,
) = modifier<Model, Float, ParentHeight, Nothing?, ChildHeight, Nothing?, ParentHeight, Float, ChildHeight>(
    { _, h, aw, ah -> ModifiedSizes(null, h, aw, ah) },
    { _, _, _, _, _, (childWidth, childHeight, eventHandlers, draw) ->
        ResolvedComponent(null, childHeight, eventHandlers) {
            region.resizeTo(
                width = childWidth.px,
                horizontalAlignment = horizontalAlignment,
            ).draw(draw = draw)
        }
    }
)
