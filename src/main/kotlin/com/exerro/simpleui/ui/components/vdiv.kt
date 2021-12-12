package com.exerro.simpleui.ui.components

import com.exerro.simpleui.Alignment
import com.exerro.simpleui.Pixels
import com.exerro.simpleui.px
import com.exerro.simpleui.ui.*
import com.exerro.simpleui.ui.internal.divCalculateOverflow
import com.exerro.simpleui.ui.internal.joinEventHandlers
import kotlin.math.round

/** A vdiv partitions space vertically. [partitions] specify specific widths for
 *  the first N children. Space for remaining children (more than the number of
 *  explicit partitions) is divided evenly. [spacing] defines spacing between
 *  children, relative to the available height of the vdiv. [reversed] allows
 *  the order of children to be reversed. If children define the width of the
 *  vdiv, the maximum width of any child is reported to the parent, and children
 *  are aligned within this width according to [horizontalAlignment]. */
inline fun <Model: UIModel, reified Width: WhoDefinesMe> ComponentChildrenContext<Model, Width, ParentDefinesMe>.vdiv(
    vararg partitions: Pixels,
    spacing: Pixels = 0.px,
    reversed: Boolean = false,
    horizontalAlignment: Alignment = 0.5f,
    noinline init: ComponentChildrenContext<Model, Width, ParentDefinesMe>.() -> Unit
) = rawComponent("vdiv") {
    children(init) { width, height, availableWidth, _, drawFunctions, eventHandlers, children ->
        val spacingValue = spacing.apply(fixFromParent(height))
        val overflowAllocation = divCalculateOverflow(partitions, children.size, spacing).apply(fixFromParent(height))
        val appliedHeights = partitions.map { it.apply(fixFromParent(height)) } + (partitions.size until children.size).map { overflowAllocation }
        val allocatedChildren = if (reversed) children.zip(appliedHeights).reversed() else children.zip(appliedHeights)
        val appliedChildren = allocatedChildren.map { (child, allocatedHeight) ->
            child(width, fixForChild(allocatedHeight), availableWidth, allocatedHeight)
        }
        val childWidth = if (appliedChildren.isNotEmpty()) appliedChildren.maxOf { fixFromChildAny(it.width) } else 0f

        SizeResolvedComponent(fixForParentAny(childWidth), nothingForParent(), joinEventHandlers(eventHandlers, appliedChildren)) {
            var lastY = 0f

            for (f in drawFunctions) f(this)

            appliedChildren.forEachIndexed { i, c ->
                val allocatedHeight = if (i == children.lastIndex && children.size > partitions.size) fixFromParent(height) - lastY else round(appliedHeights[i])

                withRegion(region
                    .resizeTo(width = (fixFromChildAnyOptional(c.width) ?: region.width).px, horizontalAlignment = horizontalAlignment)
                    .copy(y = region.y + lastY, height = allocatedHeight),
                    draw = c.draw)

                lastY += round(appliedHeights[i] + spacingValue)
            }
        }
    }
}
