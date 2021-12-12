package com.exerro.simpleui.ui.modifiers

import com.exerro.simpleui.Pixels
import com.exerro.simpleui.UndocumentedExperimentalUI
import com.exerro.simpleui.px
import com.exerro.simpleui.ui.*

@UndocumentedExperimentalUI
fun <Model: UIModel, ParentWidth: Float?, ParentHeight: Float?, ChildWidth: Float?, ChildHeight: Float?> ComponentChildrenContext<Model, ParentWidth, ParentHeight, ChildWidth, ChildHeight>.withTranslation(
    dx: Pixels = 0.px,
    dy: Pixels = 0.px,
) = modifier<Model, ParentWidth, ParentHeight, ChildWidth, ChildHeight, ParentWidth, ParentHeight, ChildWidth, ChildHeight>(
    { w, h, aw, ah -> ModifiedSizes(w, h, aw, ah) },
    { _, _, _, _, _, (childWidth, childHeight, eventHandlers, draw) ->
        ResolvedComponent(childWidth, childHeight, eventHandlers) {
            withRegion(region.translateBy(dx = dx, dy = dy), draw = draw)
        }
    }
)
