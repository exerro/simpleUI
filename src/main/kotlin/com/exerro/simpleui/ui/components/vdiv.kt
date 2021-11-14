package com.exerro.simpleui.ui.components

import com.exerro.simpleui.Pixels
import com.exerro.simpleui.UndocumentedExperimental
import com.exerro.simpleui.layout.LayoutContext
import com.exerro.simpleui.percent
import com.exerro.simpleui.px
import com.exerro.simpleui.ui.ResolvedChild
import com.exerro.simpleui.ui.ParentContext
import kotlin.math.round

@UndocumentedExperimental
fun ParentContext<Float, Float, Nothing?, Nothing?>.vdiv(
    vararg partitions: Pixels,
    spacing: Pixels = 0.px,
    reversed: Boolean = false,
    init: ParentContext<Float, Float, Nothing?, Nothing?>.() -> Unit
) = rawComponent("hdiv") {
    children(init) { width, height, availableWidth, _, drawFunctions, children ->
        val overflowAllocation = if (children.size <= partitions.size) 0.px
        else run {
            val partitionTotalSize = partitions.fold(0.px) { a, b -> a + b }
            val spacingSize = spacing * (children.size - 1).toFloat()
            val overflowedChildren = (children.size - partitions.size).toFloat()
            (100.percent - partitionTotalSize - spacingSize) / overflowedChildren
        }
        val spacingValue = spacing.apply(height)
        val sizes = partitions.toList() + (partitions.size until children.size).map { overflowAllocation }
        val allocatedChildren = (if (reversed) children.reversed() else children).zip(sizes)

        ResolvedChild(null, null) {
            var lastY = 0f

            for (f in drawFunctions) f(this)

            allocatedChildren.forEachIndexed { i, (child, size) ->
                val fixedSize = if (i == allocatedChildren.lastIndex) 100.percent - lastY.px else size
                val sizeValue = fixedSize.apply(height)
                val sizeRounded = round(sizeValue)
                val c = child(width, sizeRounded, availableWidth, sizeRounded)

                region.copy(y = region.y + lastY, height = sizeRounded).draw { c.draw(this) }

                lastY += round(sizeValue + spacingValue)
            }
        }
    }
}
