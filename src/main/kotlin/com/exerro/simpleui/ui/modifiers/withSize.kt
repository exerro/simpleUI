package com.exerro.simpleui.ui.modifiers

import com.exerro.simpleui.Pixels
import com.exerro.simpleui.UndocumentedExperimentalUI
import com.exerro.simpleui.ui.*

@UndocumentedExperimentalUI
fun <Model: UIModel> ComponentChildrenContext<Model, ChildDefinesMe, ChildDefinesMe>.withSize(
    width: Pixels,
    height: Pixels,
) = modifier(
    { _, _, availableWidth, availableHeight ->
        val newWidth = width.apply(availableWidth)
        val newHeight = height.apply(availableHeight)
        ModifiedSizes(fixForChild(newWidth), fixForChild(newHeight), newWidth, newHeight)
    },
    { _, _, _, _, m, (_, _, p) ->
        SizeResolvedComponent(fixForParent(fixFromParent(m.width)), fixForParent(fixFromParent(m.height)), p)
    }
)

@UndocumentedExperimentalUI
fun <Model: UIModel, Height: WhoDefinesMe> ComponentChildrenContext<Model, ChildDefinesMe, Height>.withWidth(
    width: Pixels,
) = modifier(
    { _, h, availableWidth, availableHeight ->
        val newWidth = width.apply(availableWidth)
        ModifiedSizes(fixForChild(newWidth), h, newWidth, availableHeight)
    },
    { _, _, _, _, m, (_, childHeight, p) ->
        SizeResolvedComponent(fixForParent(fixFromParent(m.width)), childHeight, p)
    }
)

@UndocumentedExperimentalUI
fun <Model: UIModel, Width: WhoDefinesMe> ComponentChildrenContext<Model, Width, ChildDefinesMe>.withHeight(
    height: Pixels,
) = modifier(
    { w, _, availableWidth, availableHeight ->
        val newHeight = height.apply(availableHeight)
        ModifiedSizes(w, fixForChild(newHeight), availableWidth, newHeight)
    },
    { _, _, _, _, m, (childWidth, _, p) ->
        SizeResolvedComponent(childWidth, fixForParent(fixFromParent(m.height)), p)
    }
)
