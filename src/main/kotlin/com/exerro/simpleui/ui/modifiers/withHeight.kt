package com.exerro.simpleui.ui.modifiers

import com.exerro.simpleui.Pixels
import com.exerro.simpleui.UndocumentedExperimentalUI
import com.exerro.simpleui.ui.*

@UndocumentedExperimentalUI
fun <Model: UIModel, ParentWidth: Float?, ChildWidth: Float?> ComponentChildrenContext<Model, ParentWidth, Nothing?, ChildWidth, Float>.withHeight(
    height: Pixels,
) = modifier<Model, ParentWidth, Nothing?, ChildWidth, Float, ParentWidth, Float, ChildWidth, Nothing?>(
    { w, _, availableWidth, availableHeight ->
        val newHeight = height.apply(availableHeight)
        ModifiedSizes(w, newHeight, availableWidth, newHeight)
    },
    { _, _, _, _, m, (childWidth, _, eventHandlers, draw) ->
        ResolvedComponent(childWidth, m.height, eventHandlers, draw)
    }
)
