package com.exerro.simpleui.ui.modifiers

import com.exerro.simpleui.*
import com.exerro.simpleui.ui.ModifiedSizes
import com.exerro.simpleui.ui.ParentContext
import com.exerro.simpleui.ui.ResolvedChild
import com.exerro.simpleui.ui.modifier

@UndocumentedExperimental
fun ParentContext<Nothing?, Nothing?, Float, Float>.withSize(
    width: Pixels,
    height: Pixels,
) = modifier<Nothing?, Nothing?, Float, Float, Float, Float, Nothing?, Nothing?>(
    { _, _, availableWidth, availableHeight ->
        val newWidth = width.apply(availableWidth)
        val newHeight = height.apply(availableHeight)
        ModifiedSizes(newWidth, newHeight, newWidth, newHeight)
    },
    { _, _, _, _, m, (_, _, draw: DrawContext.() -> Unit) ->
        ResolvedChild(m.width, m.height, draw)
    }
)
