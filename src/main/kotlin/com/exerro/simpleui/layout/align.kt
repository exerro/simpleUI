package com.exerro.simpleui.layout

import com.exerro.simpleui.Alignment
import com.exerro.simpleui.Undocumented
import com.exerro.simpleui.px

@Undocumented
fun DefinedLayoutContext.align(
    horizontalAlignment: Alignment,
    verticalAlignment: Alignment,
    init: UndefinedLayoutContext.() -> Unit,
) = object: UndefinedLayoutContext {
    override fun includeChild(init: (Nothing?, Nothing?, Float, Float) -> LayoutContext.Child<Float, Float>) {
        this@align.includeChild { allocatedWidth, allocatedHeight, _, _ ->
            val child = init(null, null, allocatedWidth, allocatedHeight)

            LayoutContext.Child(null, null) {
                region.resizeTo(
                    width = child.width.px,
                    height = child.height.px,
                    horizontalAlignment = horizontalAlignment,
                    verticalAlignment = verticalAlignment,
                ).draw(draw = child.draw)
            }
        }
    }
} .init ()

@Undocumented
fun <H: Float?, CH: Float?> LayoutContext<Float, H, Nothing?, CH>.halign(
    horizontalAlignment: Alignment,
    init: LayoutContext<Nothing?, H, Float, CH>.() -> Unit,
) = object: LayoutContext<Nothing?, H, Float, CH> {
    override fun includeChild(init: (Nothing?, H, Float, Float) -> LayoutContext.Child<Float, CH>) {
        this@halign.includeChild { allocatedWidth, allocatedHeight, _, availableHeight ->
            val child = init(null, allocatedHeight, allocatedWidth, availableHeight)

            LayoutContext.Child(null, child.height) {
                region.resizeTo(
                    width = child.width.px,
                    height = region.height.px,
                    horizontalAlignment = horizontalAlignment,
                    verticalAlignment = 0f,
                ).draw(draw = child.draw)
            }
        }
    }
} .init ()

@Undocumented
fun <W: Float?, CW: Float?> LayoutContext<W, Float, CW, Nothing?>.valign(
    verticalAlignment: Alignment,
    init: LayoutContext<W, Nothing?, CW, Float>.() -> Unit,
) = object: LayoutContext<W, Nothing?, CW, Float> {
    override fun includeChild(init: (W, Nothing?, Float, Float) -> LayoutContext.Child<CW, Float>) {
        this@valign.includeChild { allocatedWidth, allocatedHeight, availableWidth, _ ->
            val child = init(allocatedWidth, null, availableWidth, allocatedHeight)

            LayoutContext.Child(child.width, null) {
                region.resizeTo(
                    width = region.width.px,
                    height = child.height.px,
                    horizontalAlignment = 0f,
                    verticalAlignment = verticalAlignment,
                ).draw(draw = child.draw)
            }
        }
    }
} .init ()
