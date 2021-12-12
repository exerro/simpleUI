package com.exerro.simpleui.ui.modifiers

import com.exerro.simpleui.UndocumentedExperimentalUI
import com.exerro.simpleui.ui.*

@UndocumentedExperimentalUI
fun <Model: UIModel, Width: WhoDefinesMe, Height: WhoDefinesMe> ComponentChildrenContext<Model, Width, Height>.tracked(overrideId: Id): ComponentChildrenContext<Model, Width, Height> {
    val parentContext = this
    return object : ComponentChildrenContext<Model, Width, Height> by this {
        override fun rawComponent(elementType: String, id: Id, init: ComponentContext<Model, Width, Height>.() -> ComponentIsResolved) =
            parentContext.rawComponent(elementType, overrideId, init)
    }
}
