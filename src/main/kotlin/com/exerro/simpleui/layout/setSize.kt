package com.exerro.simpleui.layout

import com.exerro.simpleui.*

@Undocumented
fun DefinedLayoutContext.withSize(
    width: Pixels = 100.percent,
    height: Pixels = 100.percent,
    horizontalAlignment: Alignment = 0.5f,
    verticalAlignment: Alignment = 0.5f,
    init: DefinedLayoutContext.() -> Unit
) = object: DefinedLayoutContext {
    override fun includeChild(init: (Float, Float, Float, Float) -> LayoutContext.Child<Nothing?, Nothing?>) {
        this@withSize.includeChild { allocatedWidth, allocatedHeight, _, _ ->
            val widthValue = width.apply(allocatedWidth)
            val heightValue = height.apply(allocatedHeight)
            val child = init(widthValue, heightValue, widthValue, heightValue)
            LayoutContext.Child(null, null) {
                region.resizeTo(width = widthValue.px, height = heightValue.px,
                    horizontalAlignment = horizontalAlignment,
                    verticalAlignment = verticalAlignment).draw(draw = child.draw)
            }
        }
    }
} .init()

@Undocumented
fun UndefinedLayoutContext.withSize(
    width: Pixels = 100.percent,
    height: Pixels = 100.percent,
    init: DefinedLayoutContext.() -> Unit
) = object: DefinedLayoutContext {
    override fun includeChild(init: (Float, Float, Float, Float) -> LayoutContext.Child<Nothing?, Nothing?>) {
        this@withSize.includeChild { _, _, availableWidth, availableHeight ->
            val w = width.apply(availableWidth)
            val h = height.apply(availableHeight)
            val draw = init(w, h, w, h).draw

            LayoutContext.Child(w, h, draw)
        }
    }
} .init()

@Undocumented
fun <H: Float?, CH: Float?> LayoutContext<Nothing?, H, Float, CH>.withWidth(
    width: Pixels,
    init: LayoutContext<Float, H, Nothing?, CH>.() -> Unit
) = object: LayoutContext<Float, H, Nothing?, CH> {
    override fun includeChild(init: (Float, H, Float, Float) -> LayoutContext.Child<Nothing?, CH>) {
        this@withWidth.includeChild { _, allocatedHeight, availableWidth, availableHeight ->
            val w = width.apply(availableWidth)
            val child = init(w, allocatedHeight, w, availableHeight)

            LayoutContext.Child(w, child.height, child.draw)
        }
    }
} .init()

@Undocumented
fun <W: Float?, CW: Float?> LayoutContext<W, Nothing?, CW, Float>.withHeight(
    height: Pixels,
    init: LayoutContext<W, Float, CW, Nothing?>.() -> Unit
) = object: LayoutContext<W, Float, CW, Nothing?> {
    override fun includeChild(init: (W, Float, Float, Float) -> LayoutContext.Child<CW, Nothing?>) {
        this@withHeight.includeChild { allocatedWidth, _, availableWidth, availableHeight ->
            val h = height.apply(availableHeight)
            val child = init(allocatedWidth, h, availableWidth, h)

            LayoutContext.Child(child.width, h, child.draw)
        }
    }
} .init()
