package com.exerro.simpleui.ui.components

import com.exerro.simpleui.Alignment
import com.exerro.simpleui.Pixels
import com.exerro.simpleui.px
import com.exerro.simpleui.ui.*
import com.exerro.simpleui.ui.ComponentChildrenContext
import com.exerro.simpleui.ui.ResolvedComponentSizePhase
import com.exerro.simpleui.ui.internal.calculateDivOverflow
import com.exerro.simpleui.ui.internal.standardChildRendering
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
) = component("hdiv") {
    children(init) { width, height, _, availableHeight, drawFunctions, eventHandlers, children ->
        val spacingValue = spacing.apply(fixFromParent(width))
        val overflowAllocation = calculateDivOverflow(partitions, children.size, spacingValue.px).apply(fixFromParent(width))
        val appliedWidths = partitions.map { it.apply(fixFromParent(width)) } + (partitions.size until children.size).map { overflowAllocation }
        val allocatedChildren = if (reversed) children.zip(appliedWidths).asReversed() else children.zip(appliedWidths)
        val resolvedChildrenSizePhase = allocatedChildren.map { (child, allocatedWidth) ->
            child(fixForChild(allocatedWidth), height, allocatedWidth, availableHeight)
        }
        val childHeight = resolvedChildrenSizePhase.maxOfOrNull { fixFromChildAny(it.height) } ?: 0f

        ResolvedComponentSizePhase(nothingForParent(), fixForParentAny(childHeight)) { r ->
            var lastX = 0f
            val resolvedChildrenPositionPhase = resolvedChildrenSizePhase.mapIndexed { i, c ->
                val allocatedWidth = if (i == children.lastIndex && children.size > partitions.size) fixFromParent(width) - lastX else round(appliedWidths[i])
                val thisX = lastX

                lastX += round(appliedWidths[i] + spacingValue)

                c.positionResolver(r
                    .resizeTo(height = (fixFromChildAnyOptional(c.height) ?: r.height).px, verticalAlignment = verticalAlignment)
                    .copy(x = r.x + thisX, width = allocatedWidth))
            }

            standardChildRendering(r, drawFunctions, eventHandlers, resolvedChildrenPositionPhase)
        }
    }
}
