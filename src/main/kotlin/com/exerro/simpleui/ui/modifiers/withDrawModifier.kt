package com.exerro.simpleui.ui.modifiers

import com.exerro.simpleui.DrawContext
import com.exerro.simpleui.UndocumentedExperimentalUI
import com.exerro.simpleui.ui.*

@UndocumentedExperimentalUI
fun <Model: UIModel, Width: WhoDefinesMe, Height: WhoDefinesMe>
ComponentChildrenContext<Model, Width, Height>.withDrawModifier(
    modify: DrawContext.(ComponentDrawFunction) -> Unit,
) = modifier(
    { w, h, aw, ah -> ModifiedSizes(w, h, aw, ah) },
    { _, _, _, _, _, (childWidth, childHeight, eventHandlers, draw) ->
        SizeResolvedComponent(childWidth, childHeight, eventHandlers) { modify(draw) }
    }
)
