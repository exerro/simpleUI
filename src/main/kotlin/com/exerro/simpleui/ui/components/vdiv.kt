package com.exerro.simpleui.ui.components

import com.exerro.simpleui.*
import com.exerro.simpleui.ui.ResolvedComponent
import com.exerro.simpleui.ui.ParentContext
import com.exerro.simpleui.ui.UIModel
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
fun <Model: UIModel, ParentWidth: Float?, ChildWidth: Float?> ParentContext<Model, ParentWidth, Float, ChildWidth, Nothing?>.vdiv(
    vararg partitions: Pixels,
    spacing: Pixels = 0.px,
    reversed: Boolean = false,
    horizontalAlignment: Alignment = 0.5f,
    init: ParentContext<Model, ParentWidth, Float, ChildWidth, Nothing?>.() -> Unit
) = rawComponent("vdiv") {
    children(init) { width, height, availableWidth, _, drawFunctions, eventHandlers, children ->
        val spacingValue = spacing.apply(height)
        val overflowAllocation = divCalculateOverflow(partitions, children.size, spacing).apply(height)
        val appliedHeights = partitions.map { it.apply(height) } + (partitions.size until children.size).map { overflowAllocation }
        val allocatedChildren = if (reversed) children.zip(appliedHeights).reversed() else children.zip(appliedHeights)
        val appliedChildren = allocatedChildren.map { (child, allocatedHeight) ->
            child(width, allocatedHeight, availableWidth, allocatedHeight)
        }
        val childWidth = (if (width == null) if (appliedChildren.isNotEmpty()) appliedChildren.maxOf { it.width as Float } else 0f else null) as ChildWidth

        ResolvedComponent(childWidth, null, joinEventHandlers(eventHandlers, appliedChildren)) {
            var lastY = 0f

            for (f in drawFunctions) f(this)

            appliedChildren.forEachIndexed { i, c ->
                val allocatedHeight = if (i == children.lastIndex && children.size > partitions.size) height - lastY else round(appliedHeights[i])

                region
                    .resizeTo(width = (c.width ?: region.width).px, horizontalAlignment = horizontalAlignment)
                    .copy(y = region.y + lastY, height = allocatedHeight)
                    .draw(draw = c.draw)

                lastY += round(appliedHeights[i] + spacingValue)
            }
        }
    }
}
