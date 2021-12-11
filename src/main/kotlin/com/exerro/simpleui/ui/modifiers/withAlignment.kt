package com.exerro.simpleui.ui.modifiers

import com.exerro.simpleui.Alignment
import com.exerro.simpleui.UndocumentedExperimental
import com.exerro.simpleui.px
import com.exerro.simpleui.ui.*

@UndocumentedExperimental
fun <Model: UIModel> ComponentChildrenContext<Model, Float, Float, Nothing?, Nothing?>.withAlignment(
    horizontalAlignment: Alignment,
    verticalAlignment: Alignment,
) = modifier<Model, Float, Float, Nothing?, Nothing?, Nothing?, Nothing?, Float, Float>(
    { _, _, aw, ah -> ModifiedSizes(null, null, aw, ah) },
    { _, _, _, _, _, (childWidth, childHeight, eventHandlers, draw) ->
        ResolvedComponent(null, null, eventHandlers) {
            region.resizeTo(
                width = childWidth.px,
                height = childHeight.px,
                horizontalAlignment = horizontalAlignment,
                verticalAlignment = verticalAlignment
            ).draw(draw = draw)
        }
    }
)
