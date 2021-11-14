package com.exerro.simpleui.ui.modifiers

import com.exerro.simpleui.Alignment
import com.exerro.simpleui.DrawContext
import com.exerro.simpleui.UndocumentedExperimental
import com.exerro.simpleui.px
import com.exerro.simpleui.ui.ParentContext
import com.exerro.simpleui.ui.ResolvedChild
import com.exerro.simpleui.ui.modifier

@UndocumentedExperimental
fun <ParentWidth: Float?, ChildWidth: Float?> ParentContext<ParentWidth, Float, ChildWidth, Nothing?>.withVerticalAlignment(
    verticalAlignment: Alignment,
) = modifier<ParentWidth, Float, ChildWidth, Nothing?, ChildWidth, Float> { _, _, _, _, (childWidth, childHeight, draw: DrawContext.() -> Unit) ->
    ResolvedChild(childWidth, null) {
        region.resizeTo(
            height = childHeight.px,
            verticalAlignment = verticalAlignment,
        ).draw(draw = draw)
    }
}
