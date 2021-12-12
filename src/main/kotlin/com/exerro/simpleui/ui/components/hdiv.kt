package com.exerro.simpleui.ui.components

import com.exerro.simpleui.Alignment
import com.exerro.simpleui.Pixels
import com.exerro.simpleui.px
import com.exerro.simpleui.ui.*
import com.exerro.simpleui.ui.ComponentChildrenContext
import com.exerro.simpleui.ui.SizeResolvedComponent
import com.exerro.simpleui.ui.internal.divCalculateOverflow
import com.exerro.simpleui.ui.internal.joinEventHandlers
import kotlin.math.round

/** An hdiv partitions space horizontally. [partitions] specify specific widths
 *  for the first N children. Space for remaining children (more than the number
 *  of explicit partitions) is divided evenly. [spacing] defines spacing between
 *  children, relative to the available width of the hdiv. [reversed] allows
 *  the order of children to be reversed. If children define the height of the
 *  hdiv, the maximum height of any child is reported to the parent, and
 *  children are aligned within this height according to [verticalAlignment]. */
inline fun <Model: UIModel, reified Height: WhoDefinesMe> ComponentChildrenContext<Model, ParentDefinesMe, Height>.hdiv(
    vararg partitions: Pixels,
    spacing: Pixels = 0.px,
    reversed: Boolean = false,
    verticalAlignment: Alignment = 0.5f,
    noinline init: ComponentChildrenContext<Model, ParentDefinesMe, Height>.() -> Unit
) = rawComponent("hdiv") {
    children(init) { width, height, _, availableHeight, drawFunctions, eventHandlers, children ->
        val spacingValue = spacing.apply(fixFromParent(width))
        val overflowAllocation = divCalculateOverflow(partitions, children.size, spacing).apply(fixFromParent(width))
        val appliedWidths = partitions.map { it.apply(fixFromParent(width)) } + (partitions.size until children.size).map { overflowAllocation }
        val allocatedChildren = if (reversed) children.zip(appliedWidths).reversed() else children.zip(appliedWidths)
        val appliedChildren = allocatedChildren.map { (child, allocatedWidth) ->
            child(fixForChild(allocatedWidth), height, allocatedWidth, availableHeight)
        }
        val childHeight = if (appliedChildren.isNotEmpty()) appliedChildren.maxOf { fixFromChildAny(it.height) } else 0f

        SizeResolvedComponent(nothingForParent(), fixForParentAny(childHeight), joinEventHandlers(eventHandlers, appliedChildren)) {
            var lastX = 0f

            for (f in drawFunctions) f(this)

            appliedChildren.forEachIndexed { i, c ->
                val allocatedWidth = if (i == children.lastIndex && children.size > partitions.size) fixFromParent(width) - lastX else round(appliedWidths[i])

                withRegion(region
                    .resizeTo(height = (fixFromChildAnyOptional(c.height) ?: region.height).px, verticalAlignment = verticalAlignment)
                    .copy(x = region.x + lastX, width = allocatedWidth),
                    draw = c.draw)

                lastX += round(appliedWidths[i] + spacingValue)
            }
        }
    }
}
