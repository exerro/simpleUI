package com.exerro.simpleui.ui.modifiers

import com.exerro.simpleui.UndocumentedExperimentalUI
import com.exerro.simpleui.ui.*


@UndocumentedExperimentalUI
fun <Model: UIModel, Width: WhoDefinesMe, Height: WhoDefinesMe> ComponentChildrenContext<Model, Width, Height>.invisible() =
    modifier(
        { w, h, availableWidth, availableHeight -> ModifiedSizes(w, h, availableWidth, availableHeight) },
        { _, _, _, _, _, (childWidth, childHeight) ->
            SizeResolvedComponent(childWidth, childHeight, emptyList()) {}
        }
    )
