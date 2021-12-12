package com.exerro.simpleui.ui.modifiers

import com.exerro.simpleui.DrawContext
import com.exerro.simpleui.UndocumentedExperimentalUI
import com.exerro.simpleui.ui.*

@UndocumentedExperimentalUI
fun <Model: UIModel, ParentWidth: Float?, ParentHeight: Float?, ChildWidth: Float?, ChildHeight: Float?>
ComponentChildrenContext<Model, ParentWidth, ParentHeight, ChildWidth, ChildHeight>.withDrawModifier(
    modify: DrawContext.(ComponentDrawFunction) -> Unit,
) = modifier<Model, ParentWidth, ParentHeight, ChildWidth, ChildHeight, ParentWidth, ParentHeight, ChildWidth, ChildHeight>(
    { w, h, aw, ah -> ModifiedSizes(w, h, aw, ah) },
    { _, _, _, _, _, (childWidth, childHeight, eventHandlers, draw) ->
        ResolvedComponent(childWidth, childHeight, eventHandlers) { modify(draw) }
    }
)
