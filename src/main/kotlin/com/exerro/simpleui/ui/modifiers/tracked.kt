package com.exerro.simpleui.ui.modifiers

import com.exerro.simpleui.UndocumentedExperimental
import com.exerro.simpleui.ui.ChildReturn
import com.exerro.simpleui.ui.ComponentContext
import com.exerro.simpleui.ui.ParentContext

@UndocumentedExperimental
fun <W: Float?, H: Float?, CW: Float?, CH: Float?> ParentContext<W, H, CW, CH>.tracked(id: Any): ParentContext<W, H, CW, CH> {
    val parentContext = this
    return object : ParentContext<W, H, CW, CH> by parentContext {
        override fun rawComponent(elementType: String, trackingId: Any?, init: ComponentContext<W, H, CW, CH>.() -> ChildReturn) =
            parentContext.rawComponent(elementType, id, init)
    }
}
