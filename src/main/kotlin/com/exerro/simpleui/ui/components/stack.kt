package com.exerro.simpleui.ui.components

import com.exerro.simpleui.UndocumentedExperimental
import com.exerro.simpleui.ui.ResolvedChild
import com.exerro.simpleui.ui.ParentContext

@UndocumentedExperimental
fun ParentContext<Float, Float, Nothing?, Nothing?>.stack(
    init: ParentContext<Float, Float, Nothing?, Nothing?>.() -> Unit
) = rawComponent("stack") {
    children(init) { width, height, availableWidth, availableHeight, drawFunctions, children ->
        val resolvedChildren = children.map { f -> f(width, height, availableWidth, availableHeight) }
        ResolvedChild(null, null) {
            for (f in drawFunctions) f(this)
            for (child in resolvedChildren) child.draw(this)
        }
    }
}
