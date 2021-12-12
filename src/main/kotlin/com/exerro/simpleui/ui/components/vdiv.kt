package com.exerro.simpleui.ui.components

import com.exerro.simpleui.Alignment
import com.exerro.simpleui.Pixels
import com.exerro.simpleui.px
import com.exerro.simpleui.ui.*
import com.exerro.simpleui.ui.internal.calculateDivOverflow
import com.exerro.simpleui.ui.internal.standardChildRendering
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
) = component("vdiv") {
    children(init) { width, height, availableWidth, _, drawFunctions, eventHandlers, children ->
        val spacingValue = spacing.apply(fixFromParent(height))
        val overflowAllocation = calculateDivOverflow(partitions, children.size, spacingValue.px).apply(fixFromParent(height))
        val appliedHeights = partitions.map { it.apply(fixFromParent(height)) } + (partitions.size until children.size).map { overflowAllocation }
        val allocatedChildren = if (reversed) children.zip(appliedHeights).asReversed() else children.zip(appliedHeights)
        val resolvedChildrenSizePhase = allocatedChildren.map { (child, allocatedHeight) ->
            child(width, fixForChild(allocatedHeight), availableWidth, allocatedHeight)
        }
        val childWidth = resolvedChildrenSizePhase.maxOfOrNull { fixFromChildAny(it.width) } ?: 0f

        ResolvedComponentSizePhase(fixForParentAny(childWidth), nothingForParent()) { r ->
            var lastY = 0f
            val resolvedChildrenPositionPhase = resolvedChildrenSizePhase.mapIndexed { i, c ->
                val allocatedHeight = if (i == children.lastIndex && children.size > partitions.size) fixFromParent(height) - lastY else round(appliedHeights[i])
                val thisY = lastY

                lastY += round(appliedHeights[i] + spacingValue)

                c.positionResolver(r
                    .resizeTo(width = (fixFromChildAnyOptional(c.width) ?: r.width).px, horizontalAlignment = horizontalAlignment)
                    .copy(y = r.y + thisY, height = allocatedHeight))
            }

            standardChildRendering(r, drawFunctions, eventHandlers, resolvedChildrenPositionPhase)
        }
    }
}
