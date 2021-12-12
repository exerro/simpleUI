package com.exerro.simpleui.ui.extensions

import com.exerro.simpleui.UndocumentedExperimentalUI
import com.exerro.simpleui.ui.*
import com.exerro.simpleui.ui.internal.standardChildRendering

@UndocumentedExperimentalUI
val <Model: UIModel, Width: WhoDefinesMe, Height: WhoDefinesMe> ComponentContext<Model, Width, Height>.singleChild: ComponentChildContext<Model, Width, Height> get() =
    object: ComponentChildContext<Model, Width, Height>, ComponentContext<Model, Width, Height> by this {
        override val ids = IdProvider(thisComponentId)

        override fun component(
            elementType: String,
            id: Id,
            init: ComponentContext<Model, Width, Height>.() -> ComponentIsResolved
        ): ComponentIsResolved {
            return this@singleChild.children<Width, Height>(
                { component(elementType, id, init) },
                { width, height, availableWidth, availableHeight, drawFunctions, eventHandlers, children ->
                    val child = children[0](width, height, availableWidth, availableHeight)

                    ResolvedComponentSizePhase(child.width, child.height) { r ->
                        standardChildRendering(r, drawFunctions, eventHandlers, listOf(child.positionResolver(r)))
                    }
                }
            )
        }
    }
