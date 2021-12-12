package com.exerro.simpleui.ui.modifiers

import com.exerro.simpleui.Pixels
import com.exerro.simpleui.UndocumentedExperimentalUI
import com.exerro.simpleui.ui.*

@UndocumentedExperimentalUI
fun <Model: UIModel> ComponentChildrenContext<Model, Nothing?, Nothing?, Float, Float>.withSize(
    width: Pixels,
    height: Pixels,
) = modifier<Model, Nothing?, Nothing?, Float, Float, Float, Float, Nothing?, Nothing?>(
    { _, _, availableWidth, availableHeight ->
        val newWidth = width.apply(availableWidth)
        val newHeight = height.apply(availableHeight)
        ModifiedSizes(newWidth, newHeight, newWidth, newHeight)
    },
    { _, _, _, _, m, (_, _, eventHandlers, draw) ->
        ResolvedComponent(m.width, m.height, eventHandlers, draw)
    }
)
