package com.exerro.simpleui.ui.modifiers

import com.exerro.simpleui.Alignment
import com.exerro.simpleui.UndocumentedExperimentalUI
import com.exerro.simpleui.px
import com.exerro.simpleui.ui.*
import com.exerro.simpleui.ui.ComponentChildrenContext
import com.exerro.simpleui.ui.ModifiedSizes
import com.exerro.simpleui.ui.SizeResolvedComponent

@UndocumentedExperimentalUI
fun <Model: UIModel> ComponentChildrenContext<Model, ParentDefinesMe, ParentDefinesMe>.withAlignment(
    horizontalAlignment: Alignment,
    verticalAlignment: Alignment,
) = modifier(
    { _, _, aw, ah -> ModifiedSizes(nothingForChild(), nothingForChild(), aw, ah) },
    { _, _, _, _, _, (childWidth, childHeight, eventHandlers, draw) ->
        SizeResolvedComponent(nothingForParent(), nothingForParent(), eventHandlers) {
            withRegion(
                region.resizeTo(
                    width = fixFromChild(childWidth).px,
                    height = fixFromChild(childHeight).px,
                    horizontalAlignment = horizontalAlignment,
                    verticalAlignment = verticalAlignment
                ), draw = draw
            )
        }
    }
)

@UndocumentedExperimentalUI
inline fun <Model: UIModel, Width: WhoDefinesMe, reified Height: WhoDefinesMe>
ComponentChildrenContext<Model, Width, Height>.withVerticalAlignment2(
    verticalAlignment: Alignment,
) = modifier(
    { w, _, aw, ah -> ModifiedSizes(w, nothingForChild(), aw, ah) },
    { _, ph, _, _, _, (childWidth, childHeight, eventHandlers, draw) ->
        SizeResolvedComponent(childWidth, invert(ph) { fixFromChild(childHeight) }, eventHandlers) {
            withRegion(region.resizeTo(
                height = fixFromChild(childHeight).px,
                verticalAlignment = verticalAlignment,
            ), draw = draw)
        }
    }
)

@UndocumentedExperimentalUI
fun <Model: UIModel, Width: WhoDefinesMe> ComponentChildrenContext<Model, Width, ParentDefinesMe>.withVerticalAlignment(
    verticalAlignment: Alignment,
) = modifier(
    { w, _, aw, ah -> ModifiedSizes(w, nothingForChild(), aw, ah) },
    { _, _, _, _, _, (childWidth, childHeight, eventHandlers, draw) ->
        SizeResolvedComponent(childWidth, nothingForParent(), eventHandlers) {
            withRegion(
                region.resizeTo(
                    height = fixFromChild(childHeight).px,
                    verticalAlignment = verticalAlignment,
                ), draw = draw
            )
        }
    }
)

@UndocumentedExperimentalUI
fun <Model: UIModel, Height: WhoDefinesMe> ComponentChildrenContext<Model, ParentDefinesMe, Height>.withHorizontalAlignment(
    horizontalAlignment: Alignment,
) = modifier(
    { _, h, aw, ah -> ModifiedSizes(nothingForChild(), h, aw, ah) },
    { _, _, _, _, _, (childWidth, childHeight, eventHandlers, draw) ->
        SizeResolvedComponent(nothingForParent(), childHeight, eventHandlers) {
            withRegion(
                region.resizeTo(
                    width = fixFromChild(childWidth).px,
                    horizontalAlignment = horizontalAlignment,
                ), draw = draw
            )
        }
    }
)
