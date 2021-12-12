package com.exerro.simpleui.ui.modifiers

import com.exerro.simpleui.Alignment
import com.exerro.simpleui.UndocumentedExperimentalUI
import com.exerro.simpleui.px
import com.exerro.simpleui.ui.*

@UndocumentedExperimentalUI
fun <Model: UIModel, ParentWidth: Float?, ChildWidth: Float?> ComponentChildrenContext<Model, ParentWidth, Float, ChildWidth, Nothing?>.withVerticalAlignment(
    verticalAlignment: Alignment,
) = modifier<Model, ParentWidth, Float, ChildWidth, Nothing?, ParentWidth, Nothing?, ChildWidth, Float>(
    { w, _, aw, ah -> ModifiedSizes(w, null, aw, ah) },
    { _, _, _, _, _, (childWidth, childHeight, eventHandlers, draw) ->
        ResolvedComponent(childWidth, null, eventHandlers) {
            withRegion(region.resizeTo(
                height = childHeight.px,
                verticalAlignment = verticalAlignment,
            ), draw = draw)
        }
    }
)

@UndocumentedExperimentalUI
fun <Model: UIModel, ParentWidth: Float?, ParentHeight: Float?, ChildWidth: Float?, ChildHeight: Float?>
ComponentChildrenContext<Model, ParentWidth, ParentHeight, ChildWidth, ChildHeight>.withVerticalAlignment2(
    verticalAlignment: Alignment,
) = modifier<Model, ParentWidth, ParentHeight, ChildWidth, ChildHeight, ParentWidth, Nothing?, ChildWidth, Float>(
    { w, _, aw, ah -> ModifiedSizes(w, null, aw, ah) },
    { _, ph, _, _, _, (childWidth, childHeight, eventHandlers, draw) ->
        ResolvedComponent(childWidth, (if (ph == null) childHeight else null) as ChildHeight, eventHandlers) {
            withRegion(region.resizeTo(
                height = childHeight.px,
                verticalAlignment = verticalAlignment,
            ), draw = draw)
        }
    }
)
