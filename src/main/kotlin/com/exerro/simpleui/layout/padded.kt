package com.exerro.simpleui.layout

import com.exerro.simpleui.Pixels
import com.exerro.simpleui.UndocumentedExperimental
import com.exerro.simpleui.px

@UndocumentedExperimental
fun <W: Float?, H: Float?, CW: Float?, CH: Float?> LayoutContext<W, H, CW, CH>.withPadding(
    top: Pixels = 0.px,
    right: Pixels = 0.px,
    bottom: Pixels = 0.px,
    left: Pixels = 0.px,
    init: LayoutContext<W, H, CW, CH>.() -> Unit
) = object: LayoutContext<W, H, CW, CH> {
    override fun includeChild(init: (allocatedWidth: W, allocatedHeight: H, availableWidth: Float, availableHeight: Float) -> LayoutContext.Child<CW, CH>) {
        this@withPadding.includeChild { allocatedWidth, allocatedHeight, availableWidth, availableHeight ->
            val topValue = top.apply(availableHeight)
            val leftValue = left.apply(availableWidth)
            val widthDelta = leftValue + right.apply(availableWidth)
            val heightDelta = topValue + bottom.apply(availableHeight)
            val w = allocatedWidth?.let { (it as Float - widthDelta) } as W
            val h = allocatedHeight?.let { (it as Float - heightDelta) } as H
            val child = init(w, h, if (allocatedWidth == null) availableWidth else availableWidth - widthDelta, if (allocatedHeight == null) availableHeight else availableHeight - heightDelta)
            val cw = child.width?.let { it as Float + widthDelta } as CW
            val ch = child.height?.let { it as Float + heightDelta } as CH

            LayoutContext.Child(cw, ch) {
                region.withPadding(top = top, right = right, bottom = bottom, left = left).draw(draw = child.draw)
            }
        }
    }
} .init()

@UndocumentedExperimental
fun <W: Float?, H: Float?, CW: Float?, CH: Float?> LayoutContext<W, H, CW, CH>.withPadding(
    horizontal: Pixels = 0.px,
    vertical: Pixels = 0.px,
    init: LayoutContext<W, H, CW, CH>.() -> Unit
) = withPadding(top = vertical, right = horizontal, bottom = vertical, left = horizontal, init = init)

@UndocumentedExperimental
fun <W: Float?, H: Float?, CW: Float?, CH: Float?> LayoutContext<W, H, CW, CH>.withPadding(
    all: Pixels,
    init: LayoutContext<W, H, CW, CH>.() -> Unit
) = withPadding(top = all, right = all, bottom = all, left = all, init = init)
