package com.exerro.simpleui.layout

import com.exerro.simpleui.Pixels
import com.exerro.simpleui.Undocumented
import com.exerro.simpleui.px

@Undocumented
fun <W: Float?, H: Float?, CW: Float?, CH: Float?> LayoutContext<W, H, CW, CH>.withTranslation(
    dx: Pixels = 0.px,
    dy: Pixels = 0.px,
    init: LayoutContext<W, H, CW, CH>.() -> Unit,
) = object: LayoutContext<W, H, CW, CH> {
    override fun includeChild(init: (W, H, Float, Float) -> LayoutContext.Child<CW, CH>) {
        this@withTranslation.includeChild { allocatedWidth, allocatedHeight, availableWidth, availableHeight ->
            val child = init(allocatedWidth, allocatedHeight, availableWidth, availableHeight)
            LayoutContext.Child(child.width, child.height) {
                region.translateBy(dx = dx, dy = dy).draw(draw = child.draw)
            }
        }
    }
} .init()
