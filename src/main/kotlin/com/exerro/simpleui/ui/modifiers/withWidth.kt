package com.exerro.simpleui.ui.modifiers

import com.exerro.simpleui.*
import com.exerro.simpleui.ui.ModifiedSizes
import com.exerro.simpleui.ui.ParentContext
import com.exerro.simpleui.ui.ResolvedChild
import com.exerro.simpleui.ui.modifier

@UndocumentedExperimental
fun <ParentHeight: Float?, ChildHeight: Float?> ParentContext<Nothing?, ParentHeight, Float, ChildHeight>.withWidth(
    width: Pixels,
) = modifier<Nothing?, ParentHeight, Float, ChildHeight, Float, ParentHeight, Nothing?, ChildHeight>(
    { _, h, availableWidth, availableHeight ->
        val newWidth = width.apply(availableWidth)
        ModifiedSizes(newWidth, h, newWidth, availableHeight)
    },
    { _, _, _, _, m, (_, childHeight, draw: DrawContext.() -> Unit) ->
        ResolvedChild(m.width, childHeight, draw)
    }
)
