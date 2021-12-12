package com.exerro.simpleui.ui.internal

import com.exerro.simpleui.Pixels
import com.exerro.simpleui.UndocumentedExperimentalUI
import com.exerro.simpleui.percent
import com.exerro.simpleui.px
import com.exerro.simpleui.ui.ComponentEventHandler
import com.exerro.simpleui.ui.ResolvedComponent
import com.exerro.simpleui.ui.components.hdiv
import com.exerro.simpleui.ui.components.vdiv

/** Calculate the size in [Pixels] of children that overflow the explicit
 *  partition sizes of a dividing element (see: [hdiv], [vdiv]). */
internal fun divCalculateOverflow(
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
internal fun joinEventHandlers(
    thisEventHandlers: List<ComponentEventHandler>,
    children: List<ResolvedComponent<*, *>>
) = thisEventHandlers + children.flatMap { it.eventHandlers }

@UndocumentedExperimentalUI
@Suppress("UNCHECKED_CAST")
internal inline fun <ChildValue: Float?> calculateInverse(
    parentValue: Float?,
    calculateChildValue: () -> Float,
): ChildValue = (if (parentValue == null) calculateChildValue() else null) as ChildValue
