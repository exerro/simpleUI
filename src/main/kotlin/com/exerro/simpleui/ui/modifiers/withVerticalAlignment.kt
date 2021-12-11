package com.exerro.simpleui.ui.modifiers

import com.exerro.simpleui.Alignment
import com.exerro.simpleui.UndocumentedExperimental
import com.exerro.simpleui.px
import com.exerro.simpleui.ui.*

@UndocumentedExperimental
fun <Model: UIModel, ParentWidth: Float?, ChildWidth: Float?> ComponentChildrenContext<Model, ParentWidth, Float, ChildWidth, Nothing?>.withVerticalAlignment(
    verticalAlignment: Alignment,
) = modifier<Model, ParentWidth, Float, ChildWidth, Nothing?, ParentWidth, Nothing?, ChildWidth, Float>(
    { w, _, aw, ah -> ModifiedSizes(w, null, aw, ah) },
    { _, _, _, _, _, (childWidth, childHeight, eventHandlers, draw) ->
        ResolvedComponent(childWidth, null, eventHandlers) {
            region.resizeTo(
                height = childHeight.px,
                verticalAlignment = verticalAlignment,
            ).draw(draw = draw)
        }
    }
)
