package com.exerro.simpleui.layout

import com.exerro.simpleui.Pixels
import com.exerro.simpleui.Undocumented
import com.exerro.simpleui.px
import kotlin.math.round

@Undocumented
fun LayoutContext<Float, Nothing?, Nothing?, Float>.vflow(
    spacing: Pixels = 0.px,
    reversed: Boolean = false,
    init: LayoutContext<Float, Nothing?, Nothing?, Float>.() -> Unit,
) = includeChild { allocatedWidth, _, availableWidth, availableHeight ->
    val children = mutableListOf<LayoutContext.Child<Nothing?, Float>>()
    val context = object: LayoutContext<Float, Nothing?, Nothing?, Float> {
        override fun includeChild(init: (Float, Nothing?, Float, Float) -> LayoutContext.Child<Nothing?, Float>) {
            children.add(init(allocatedWidth, null, availableWidth, availableHeight))
        }
    }

    context.init()

    val childrenHeight = children.fold(0f) { a, b -> a + b.height }
    val spacingValue = spacing.apply((availableHeight - childrenHeight) / (children.size - 1))
    val totalHeight = childrenHeight + spacingValue * (children.size - 1)

    LayoutContext.Child(null, totalHeight) {
        var lastY = if (reversed) region.height else 0f

        children.forEachIndexed { i, child ->
            if (reversed) lastY = round(lastY - child.height)
            val sizeRounded = round(child.height)
            region.copy(y = region.y + lastY, height = sizeRounded).draw { child.draw(this) }
            if (reversed) lastY -= spacingValue
            else lastY += round(child.height + spacingValue)
        }
    }
}

@Undocumented
fun LayoutContext<Nothing?, Float, Float, Nothing?>.hflow(
    spacing: Pixels = 0.px,
    reversed: Boolean = false,
    init: LayoutContext<Nothing?, Float, Float, Nothing?>.() -> Unit,
) = includeChild { _, allocatedHeight, availableWidth, availableHeight ->
    val children = mutableListOf<LayoutContext.Child<Float, Nothing?>>()
    val context = object: LayoutContext<Nothing?, Float, Float, Nothing?> {
        override fun includeChild(init: (Nothing?, Float, Float, Float) -> LayoutContext.Child<Float, Nothing?>) {
            children.add(init(null, allocatedHeight, availableWidth, availableHeight))
        }
    }

    context.init()

    val childrenWidth = children.fold(0f) { a, b -> a + b.width }
    val spacingValue = spacing.apply((availableWidth - childrenWidth) / (children.size - 1))
    val totalWidth = childrenWidth + spacingValue * (children.size - 1)

    LayoutContext.Child(totalWidth, null) {
        var lastX = if (reversed) region.width else 0f

        children.forEachIndexed { i, child ->
            if (reversed) lastX = round(lastX - child.width)
            val sizeRounded = round(child.width)
            region.copy(x = region.x + lastX, width = sizeRounded).draw { child.draw(this) }
            if (reversed) lastX -= spacingValue
            else lastX += round(child.width + spacingValue)
        }
    }
}
