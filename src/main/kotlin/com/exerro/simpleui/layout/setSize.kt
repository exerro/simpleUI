package com.exerro.simpleui.layout

import com.exerro.simpleui.*

@UndocumentedExperimental
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

@UndocumentedExperimental
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
