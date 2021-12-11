package com.exerro.simpleui.ui.modifiers

import com.exerro.simpleui.*
import com.exerro.simpleui.ui.*

@UndocumentedExperimental
fun <Model: UIModel, ParentHeight: Float?, ChildHeight: Float?> ComponentChildrenContext<Model, Nothing?, ParentHeight, Float, ChildHeight>.withWidth(
    width: Pixels,
) = modifier<Model, Nothing?, ParentHeight, Float, ChildHeight, Float, ParentHeight, Nothing?, ChildHeight>(
    { _, h, availableWidth, availableHeight ->
        val newWidth = width.apply(availableWidth)
        ModifiedSizes(newWidth, h, newWidth, availableHeight)
    },
    { _, _, _, _, m, (_, childHeight, eventHandlers, draw) ->
        ResolvedComponent(m.width, childHeight, eventHandlers, draw)
    }
)
