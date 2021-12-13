package com.exerro.simpleui.ui.modifiers

import com.exerro.simpleui.Alignment
import com.exerro.simpleui.UndocumentedExperimentalUI
import com.exerro.simpleui.px
import com.exerro.simpleui.ui.*
import com.exerro.simpleui.ui.extensions.ModifiedSizes
import com.exerro.simpleui.ui.extensions.regionModifier

@UndocumentedExperimentalUI
fun <Model: UIModel> ComponentChildrenContext<Model, ParentDefinesMe, ParentDefinesMe>.withAlignment(
    horizontalAlignment: Alignment,
    verticalAlignment: Alignment,
) = regionModifier(
    { _, _, aw, ah -> ModifiedSizes(nothingForChild(), nothingForChild(), aw, ah) },
    { _, _, childWidth, childHeight, region -> region.resizeTo(
        width = fixFromChild(childWidth).px,
        height = fixFromChild(childHeight).px,
        horizontalAlignment = horizontalAlignment,
        verticalAlignment = verticalAlignment,
    ) }
)

@UndocumentedExperimentalUI
fun <Model: UIModel, Width: WhoDefinesMe, Height: WhoDefinesMe>
ComponentChildrenContext<Model, Width, Height>.withVerticalAlignment(
    verticalAlignment: Alignment,
) = regionModifier(
    { w, _, aw, ah -> ModifiedSizes(w, nothingForChild(), aw, ah) },
    { _, _, _, childHeight, region -> region.resizeTo(
        height = fixFromChild(childHeight).px,
        verticalAlignment = verticalAlignment,
    ) }
)

@UndocumentedExperimentalUI
fun <Model: UIModel, Width: WhoDefinesMe, Height: WhoDefinesMe>
ComponentChildrenContext<Model, Width, Height>.withHorizontalAlignment(
    horizontalAlignment: Alignment,
) = regionModifier(
    { _, h, aw, ah -> ModifiedSizes(nothingForChild(), h, aw, ah) },
    { _, _, childWidth, _, region -> region.resizeTo(
        width = fixFromChild(childWidth).px,
        horizontalAlignment = horizontalAlignment,
    ) }
)
