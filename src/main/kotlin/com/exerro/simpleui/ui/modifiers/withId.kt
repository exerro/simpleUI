package com.exerro.simpleui.ui.modifiers

import com.exerro.simpleui.UndocumentedExperimentalUI
import com.exerro.simpleui.ui.*

@UndocumentedExperimentalUI
fun <Model: UIModel, Width: WhoDefinesMe, Height: WhoDefinesMe> ComponentChildrenContext<Model, Width, Height>.withId(overrideId: Id) =
    object: ComponentChildrenContext<Model, Width, Height> by this {
        override fun component(elementType: String, id: Id, init: ComponentContext<Model, Width, Height>.() -> ComponentIsResolved) =
            this@withId.component(elementType, overrideId, init)
    }
