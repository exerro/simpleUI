package com.exerro.simpleui.ui.modifiers

import com.exerro.simpleui.UndocumentedExperimental
import com.exerro.simpleui.ui.ComponentReturn
import com.exerro.simpleui.ui.ComponentContext
import com.exerro.simpleui.ui.ParentContext
import com.exerro.simpleui.ui.UIModel

@UndocumentedExperimental
fun <Model: UIModel, W: Float?, H: Float?, CW: Float?, CH: Float?> ParentContext<Model, W, H, CW, CH>.tracked(id: Any): ParentContext<Model, W, H, CW, CH> {
    val parentContext = this
    return object : ParentContext<Model, W, H, CW, CH> by parentContext {
        override fun rawComponent(elementType: String, trackingId: Any?, init: ComponentContext<Model, W, H, CW, CH>.() -> ComponentReturn) =
            parentContext.rawComponent(elementType, id, init)
    }
}
