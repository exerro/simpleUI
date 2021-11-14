package com.exerro.simpleui.ui.modifiers

import com.exerro.simpleui.Alignment
import com.exerro.simpleui.DrawContext
import com.exerro.simpleui.UndocumentedExperimental
import com.exerro.simpleui.px
import com.exerro.simpleui.ui.ParentContext
import com.exerro.simpleui.ui.ResolvedChild
import com.exerro.simpleui.ui.modifier

@UndocumentedExperimental
fun ParentContext<Float, Float, Nothing?, Nothing?>.withAlignment(
    horizontalAlignment: Alignment,
    verticalAlignment: Alignment,
) = modifier<Float, Float, Nothing?, Nothing?, Float, Float> { _, _, _, _, (childWidth, childHeight, draw: DrawContext.() -> Unit) ->
    ResolvedChild(null, null) {
        region.resizeTo(
            width = childWidth.px,
            height = childHeight.px,
            horizontalAlignment = horizontalAlignment,
            verticalAlignment = verticalAlignment
        ).draw(draw = draw)
    }
}
