package com.exerro.simpleui.ui.modifiers

import com.exerro.simpleui.Alignment
import com.exerro.simpleui.DrawContext
import com.exerro.simpleui.UndocumentedExperimental
import com.exerro.simpleui.px
import com.exerro.simpleui.ui.ParentContext
import com.exerro.simpleui.ui.ResolvedChild
import com.exerro.simpleui.ui.modifier

@UndocumentedExperimental
fun <ParentHeight: Float?, ChildHeight: Float?> ParentContext<Float, ParentHeight, Nothing?, ChildHeight>.withHorizontalAlignment(
    horizontalAlignment: Alignment,
) = modifier<Float, ParentHeight, Nothing?, ChildHeight, Float, ChildHeight> { _, _, _, _, (childWidth, childHeight, draw: DrawContext.() -> Unit) ->
    ResolvedChild(null, childHeight) {
        region.resizeTo(
            width = childWidth.px,
            horizontalAlignment = horizontalAlignment,
        ).draw(draw = draw)
    }
}
