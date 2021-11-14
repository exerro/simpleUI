package com.exerro.simpleui.ui.modifiers

import com.exerro.simpleui.*
import com.exerro.simpleui.ui.ModifiedSizes
import com.exerro.simpleui.ui.ParentContext
import com.exerro.simpleui.ui.ResolvedChild
import com.exerro.simpleui.ui.modifier

@UndocumentedExperimental
fun <ParentWidth: Float?, ParentHeight: Float?, ChildWidth: Float?, ChildHeight: Float?> ParentContext<ParentWidth, ParentHeight, ChildWidth, ChildHeight>.withPadding(
    top: Pixels = 0.percent,
    right: Pixels = 100.percent,
    bottom: Pixels = 100.percent,
    left: Pixels = 100.percent,
) = modifier<ParentWidth, ParentHeight, ChildWidth, ChildHeight, ParentWidth, ParentHeight, ChildWidth, ChildHeight>(
    { w, h, availableWidth, availableHeight ->
        val widthDelta = right.apply(availableWidth) + left.apply(availableWidth)
        val heightDelta = top.apply(availableHeight) + bottom.apply(availableHeight)
        val newWidth = w?.let { it - widthDelta } as ParentWidth
        val newHeight = h?.let { it - heightDelta } as ParentHeight
        ModifiedSizes(newWidth, newHeight, availableWidth - widthDelta, availableHeight - heightDelta)
    },
    { _, _, availableWidth, availableHeight, _, (childWidth, childHeight, draw: DrawContext.() -> Unit) ->
        val leftValue = left.apply(availableWidth)
        val topValue = top.apply(availableHeight)
        val widthDelta = leftValue + right.apply(availableWidth)
        val heightDelta = topValue + bottom.apply(availableHeight)
        val cw = childWidth?.let { it + widthDelta } as ChildWidth
        val ch = childHeight?.let { it + heightDelta } as ChildHeight
        ResolvedChild(cw, ch) {
            region.withPadding(top = top, right = right, bottom = bottom, left = left).draw(draw = draw)
        }
    }
)

@UndocumentedExperimental
fun <ParentWidth: Float?, ParentHeight: Float?, ChildWidth: Float?, ChildHeight: Float?> ParentContext<ParentWidth, ParentHeight, ChildWidth, ChildHeight>.withPadding(
    vertical: Pixels,
    horizontal: Pixels,
) = withPadding(top = vertical, right = horizontal, bottom = vertical, left = horizontal)

@UndocumentedExperimental
fun <ParentWidth: Float?, ParentHeight: Float?, ChildWidth: Float?, ChildHeight: Float?> ParentContext<ParentWidth, ParentHeight, ChildWidth, ChildHeight>.withPadding(
    all: Pixels,
) = withPadding(top = all, right = all, bottom = all, left = all)
