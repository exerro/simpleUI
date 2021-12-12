package com.exerro.simpleui.ui.modifiers

import com.exerro.simpleui.Pixels
import com.exerro.simpleui.UndocumentedExperimentalUI
import com.exerro.simpleui.percent
import com.exerro.simpleui.ui.*
import com.exerro.simpleui.ui.extensions.ModifiedSizes
import com.exerro.simpleui.ui.extensions.modifier

@UndocumentedExperimentalUI
inline fun <Model: UIModel, reified Width: WhoDefinesMe, reified Height: WhoDefinesMe> ComponentChildrenContext<Model, Width, Height>.withPadding(
    top: Pixels = 0.percent,
    right: Pixels = 0.percent,
    bottom: Pixels = 0.percent,
    left: Pixels = 0.percent,
) = modifier(
    { w, h, availableWidth, availableHeight ->
        val widthDelta = right.apply(availableWidth) + left.apply(availableWidth)
        val heightDelta = top.apply(availableHeight) + bottom.apply(availableHeight)
        val newWidth = map(w) { it - widthDelta }
        val newHeight = map(h) { it - heightDelta }
        ModifiedSizes(newWidth, newHeight, availableWidth - widthDelta, availableHeight - heightDelta)
    },
    { _, _, availableWidth, availableHeight, _, (childWidth, childHeight, positionResolver) ->
        val leftValue = left.apply(availableWidth)
        val topValue = top.apply(availableHeight)
        val widthDelta = leftValue + right.apply(availableWidth)
        val heightDelta = topValue + bottom.apply(availableHeight)
        val cw = map(childWidth) { it + widthDelta }
        val ch = map(childHeight) { it + heightDelta }
        ResolvedComponentSizePhase(cw, ch) { r ->
            positionResolver(r.withPadding(top = top, right = right, bottom = bottom, left = left))
        }
    }
)

@UndocumentedExperimentalUI
inline fun <Model: UIModel, reified Width: WhoDefinesMe, reified Height: WhoDefinesMe> ComponentChildrenContext<Model, Width, Height>.withPadding(
    vertical: Pixels,
    horizontal: Pixels,
) = withPadding(top = vertical, right = horizontal, bottom = vertical, left = horizontal)

@UndocumentedExperimentalUI
inline fun <Model: UIModel, reified Width: WhoDefinesMe, reified Height: WhoDefinesMe> ComponentChildrenContext<Model, Width, Height>.withPadding(
    all: Pixels,
) = withPadding(top = all, right = all, bottom = all, left = all)
