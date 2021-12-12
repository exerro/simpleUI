package com.exerro.simpleui.ui.modifiers

import com.exerro.simpleui.Layer
import com.exerro.simpleui.Pixels
import com.exerro.simpleui.UndocumentedExperimental
import com.exerro.simpleui.px
import com.exerro.simpleui.ui.*

@UndocumentedExperimental
fun <Model: UIModel, ParentWidth: Float?, ParentHeight: Float?, ChildWidth: Float?, ChildHeight: Float?> ComponentChildrenContext<Model, ParentWidth, ParentHeight, ChildWidth, ChildHeight>.withLayer(
    layer: Layer,
) = modifier<Model, ParentWidth, ParentHeight, ChildWidth, ChildHeight, ParentWidth, ParentHeight, ChildWidth, ChildHeight>(
    { w, h, aw, ah -> ModifiedSizes(w, h, aw, ah) },
    { _, _, _, _, _, (childWidth, childHeight, eventHandlers, draw) ->
        ResolvedComponent(childWidth, childHeight, eventHandlers) {
            withLayer(layer, draw)
        }
    }
)
