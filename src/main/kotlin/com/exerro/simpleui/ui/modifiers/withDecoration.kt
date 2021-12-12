package com.exerro.simpleui.ui.modifiers

import com.exerro.simpleui.*
import com.exerro.simpleui.ui.*

@UndocumentedExperimental
fun <Model: UIModel, ParentWidth: Float?, ParentHeight: Float?, ChildWidth: Float?, ChildHeight: Float?>
ComponentChildrenContext<Model, ParentWidth, ParentHeight, ChildWidth, ChildHeight>.withDecoration(
    after: Boolean = false,
    decoration: DrawContext.() -> Unit,
) = modifier<Model, ParentWidth, ParentHeight, ChildWidth, ChildHeight, ParentWidth, ParentHeight, ChildWidth, ChildHeight>(
    { w, h, aw, ah -> ModifiedSizes(w, h, aw, ah) },
    { _, _, _, _, _, (childWidth, childHeight, eventHandlers, draw) ->
        ResolvedComponent(childWidth, childHeight, eventHandlers) {
            if (!after) decoration()
            draw()
            if (after) decoration()
        }
    }
)
