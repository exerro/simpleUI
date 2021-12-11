package com.exerro.simpleui.ui.modifiers

import com.exerro.simpleui.*
import com.exerro.simpleui.ui.*

@UndocumentedExperimental
fun <Model: UIModel, ParentWidth: Float?, ParentHeight: Float?, ChildWidth: Float?, ChildHeight: Float?> ComponentChildrenContext<Model, ParentWidth, ParentHeight, ChildWidth, ChildHeight>.withPadding(
    top: Pixels = 0.percent,
    right: Pixels = 0.percent,
    bottom: Pixels = 0.percent,
    left: Pixels = 0.percent,
) = modifier<Model, ParentWidth, ParentHeight, ChildWidth, ChildHeight, ParentWidth, ParentHeight, ChildWidth, ChildHeight>(
    { w, h, availableWidth, availableHeight ->
        val widthDelta = right.apply(availableWidth) + left.apply(availableWidth)
        val heightDelta = top.apply(availableHeight) + bottom.apply(availableHeight)
        val newWidth = w?.let { it - widthDelta } as ParentWidth
        val newHeight = h?.let { it - heightDelta } as ParentHeight
        ModifiedSizes(newWidth, newHeight, availableWidth - widthDelta, availableHeight - heightDelta)
    },
    { _, _, availableWidth, availableHeight, _, (childWidth, childHeight, eventHandlers, draw) ->
        val leftValue = left.apply(availableWidth)
        val topValue = top.apply(availableHeight)
        val widthDelta = leftValue + right.apply(availableWidth)
        val heightDelta = topValue + bottom.apply(availableHeight)
        val cw = childWidth?.let { it + widthDelta } as ChildWidth
        val ch = childHeight?.let { it + heightDelta } as ChildHeight
        ResolvedComponent(cw, ch, eventHandlers) {
            region.withPadding(top = top, right = right, bottom = bottom, left = left).draw(draw = draw)
        }
    }
)

@UndocumentedExperimental
fun <Model: UIModel, ParentWidth: Float?, ParentHeight: Float?, ChildWidth: Float?, ChildHeight: Float?> ComponentChildrenContext<Model, ParentWidth, ParentHeight, ChildWidth, ChildHeight>.withPadding(
    vertical: Pixels,
    horizontal: Pixels,
) = withPadding(top = vertical, right = horizontal, bottom = vertical, left = horizontal)

@UndocumentedExperimental
fun <Model: UIModel, ParentWidth: Float?, ParentHeight: Float?, ChildWidth: Float?, ChildHeight: Float?> ComponentChildrenContext<Model, ParentWidth, ParentHeight, ChildWidth, ChildHeight>.withPadding(
    all: Pixels,
) = withPadding(top = all, right = all, bottom = all, left = all)
