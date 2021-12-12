package com.exerro.simpleui.ui.modifiers

import com.exerro.simpleui.UndocumentedExperimentalUI
import com.exerro.simpleui.ui.ComponentChildrenContext
import com.exerro.simpleui.ui.ComponentContext
import com.exerro.simpleui.ui.ComponentReturn
import com.exerro.simpleui.ui.UIModel

@UndocumentedExperimentalUI
fun <Model: UIModel, W: Float?, H: Float?, CW: Float?, CH: Float?> ComponentChildrenContext<Model, W, H, CW, CH>.tracked(id: Any): ComponentChildrenContext<Model, W, H, CW, CH> {
    val parentContext = this
    return object : ComponentChildrenContext<Model, W, H, CW, CH> by parentContext {
        override fun rawComponent(elementType: String, trackingId: Any?, init: ComponentContext<Model, W, H, CW, CH>.() -> ComponentReturn) =
            parentContext.rawComponent(elementType, id, init)
    }
}
