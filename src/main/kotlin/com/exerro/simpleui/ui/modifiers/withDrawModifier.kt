package com.exerro.simpleui.ui.modifiers

import com.exerro.simpleui.DrawContext
import com.exerro.simpleui.UndocumentedExperimentalUI
import com.exerro.simpleui.ui.*
import com.exerro.simpleui.ui.ComponentSizeResolver

@UndocumentedExperimentalUI
fun <Model: UIModel, Width: WhoDefinesMe, Height: WhoDefinesMe>
ComponentChildrenContext<Model, Width, Height>.withDrawModifier(
    modify: DrawContext.(ComponentDrawFunction) -> Unit,
) = object: ComponentChildrenContext<Model, Width, Height> by this {
    override fun component(elementType: String, id: Id, init: ComponentContext<Model, Width, Height>.() -> ComponentIsResolved) = this@withDrawModifier.component(elementType, id) {
        val outerComponentContext = this

        init(object: ComponentContext<Model, Width, Height> by outerComponentContext {
            override fun <SubWidth : WhoDefinesMe, SubHeight : WhoDefinesMe> children(
                getChildren: ComponentChildrenContext<Model, SubWidth, SubHeight>.() -> Unit,
                resolveComponentSize: (width: SizeForChild<Width>, height: SizeForChild<Height>, availableWidth: Float, availableHeight: Float, drawFunctions: List<ComponentDrawFunction>, eventHandlers: List<ComponentEventHandler>, children: List<ComponentSizeResolver<SubWidth, SubHeight>>) -> ResolvedComponentSizePhase<Width, Height>
            ) = outerComponentContext.children(getChildren) { width, height, availableWidth, availableHeight, drawFunctions, eventHandlers, children ->
                val sizeResolved = resolveComponentSize(width, height, availableWidth, availableHeight, drawFunctions, eventHandlers, children)

                sizeResolved.copy { r ->
                    val resolved = sizeResolved.positionResolver(r)
                    resolved.copy(draw = { modify(resolved.draw) })
                }
            }
        })
    }
}
