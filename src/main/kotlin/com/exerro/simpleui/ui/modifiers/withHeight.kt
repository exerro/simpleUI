package com.exerro.simpleui.ui.modifiers

import com.exerro.simpleui.*
import com.exerro.simpleui.ui.ModifiedSizes
import com.exerro.simpleui.ui.ParentContext
import com.exerro.simpleui.ui.ResolvedChild
import com.exerro.simpleui.ui.modifier

@UndocumentedExperimental
fun <ParentWidth: Float?, ChildWidth: Float?> ParentContext<ParentWidth, Nothing?, ChildWidth, Float>.withHeight(
    height: Pixels,
) = modifier<ParentWidth, Nothing?, ChildWidth, Float, ParentWidth, Float, ChildWidth, Nothing?>(
    { w, _, availableWidth, availableHeight ->
        val newHeight = height.apply(availableHeight)
        ModifiedSizes(w, newHeight, availableWidth, newHeight)
    },
    { _, _, _, _, m, (childWidth, _, draw: DrawContext.() -> Unit) ->
        ResolvedChild(childWidth, m.height, draw)
    }
)
