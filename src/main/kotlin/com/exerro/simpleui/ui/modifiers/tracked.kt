package com.exerro.simpleui.ui.modifiers

import com.exerro.simpleui.UndocumentedExperimentalUI
import com.exerro.simpleui.ui.*

@UndocumentedExperimentalUI
fun <Model: UIModel, W: Float?, H: Float?, CW: Float?, CH: Float?> ComponentChildrenContext<Model, W, H, CW, CH>.tracked(overrideId: Id): ComponentChildrenContext<Model, W, H, CW, CH> {
    val parentContext = this
    return object : ComponentChildrenContext<Model, W, H, CW, CH> by this {
        override fun rawComponent(elementType: String, id: Id, init: ComponentContext<Model, W, H, CW, CH>.() -> ComponentIsResolved) =
            parentContext.rawComponent(elementType, overrideId, init)
    }
}
