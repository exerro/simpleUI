package com.exerro.simpleui.ui.internal

import com.exerro.simpleui.*
import com.exerro.simpleui.ui.*
import com.exerro.simpleui.ui.components.hdiv
import com.exerro.simpleui.ui.components.vdiv

@UndocumentedExperimentalUI
fun <Width: WhoDefinesMe, Height: WhoDefinesMe> resolveFlowChildSizes(
    reversed: Boolean,
    width: SizeForChild<Width>,
    height: SizeForChild<Height>,
    availableWidth: Float,
    availableHeight: Float,
    children: List<ComponentSizeResolver<Width, Height>>
) = (if (reversed) children.asReversed() else children).map { child ->
    child(width, height, availableWidth, availableHeight)
}

/** Calculate the size in [Pixels] of children that overflow the explicit
 *  partition sizes of a dividing element (see: [hdiv], [vdiv]). */
fun calculateDivOverflow(
    partitions: Array<out Pixels>,
    childCount: Int,
    spacing: Pixels
): Pixels =
    // when there are enough partitions for the childCount, we can skip any
    // calculations and return 0
    if (childCount <= partitions.size) 0.px
    // otherwise, we calculate the size of each child after the explicit
    // partitions
    else run {
        // find the total size of partitioned children
        val partitionTotalSize = partitions.fold(0.px) { a, b -> a + b }
        // find the total size given to spacing
        val spacingSize = spacing * (childCount - 1).toFloat()
        // count the number of children that overflow the explicit partitions
        val overflowedChildren = (childCount - partitions.size).toFloat()
        // calculate the space available to overflowed children
        val availableSize = 100.percent - partitionTotalSize - spacingSize
        // return that size divided evenly amongst the overflowed children
        availableSize / overflowedChildren
    }

@UndocumentedExperimentalUI
fun joinEventHandlers(
    thisEventHandlers: List<ComponentEventHandler>,
    children: List<ResolvedComponentPositionPhase>
) = thisEventHandlers + children.flatMap { it.eventHandlers }

@UndocumentedExperimentalUI
fun standardChildRendering(
    region: Region,
    drawFunctions: List<ComponentDrawFunction>,
    eventHandlers: List<ComponentEventHandler>,
    children: List<ResolvedComponentPositionPhase>
) = ResolvedComponentPositionPhase(region, joinEventHandlers(eventHandlers, children)) {
    for (f in drawFunctions) f(this)

    for (child in children) {
        withRegion(child.region, draw = child.draw)
    }
}

@UndocumentedExperimentalUI
@Suppress("UNCHECKED_CAST")
internal inline fun <ChildValue: Float?> calculateInverse(
    parentValue: Float?,
    calculateChildValue: () -> Float,
): ChildValue = (if (parentValue == null) calculateChildValue() else null) as ChildValue
