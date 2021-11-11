package com.exerro.simpleui.layout

import com.exerro.simpleui.Pixels
import com.exerro.simpleui.Undocumented
import com.exerro.simpleui.percent
import com.exerro.simpleui.px
import kotlin.math.round

@Undocumented
fun DefinedLayoutContext.hdiv(
    vararg partitions: Pixels,
    spacing: Pixels = 0.px,
    reversed: Boolean = false,
    init: DefinedLayoutContext.() -> Unit,
) = includeChild { _, _, availableWidth, availableHeight ->
    val childBuilders = mutableListOf<(Float, Float, Float, Float) -> LayoutContext.Child<Nothing?, Nothing?>>()
    val context = object: DefinedLayoutContext {
        override fun includeChild(init: (Float, Float, Float, Float) -> LayoutContext.Child<Nothing?, Nothing?>) {
            childBuilders.add(init)
        }
    }

    context.init()

    val overflowAllocation = if (childBuilders.size <= partitions.size) 0.px
    else run {
        val partitionTotalSize = partitions.fold(0.px) { a, b -> a + b }
        val spacingSize = spacing * (childBuilders.size - 1).toFloat()
        val overflowedChildren = (childBuilders.size - partitions.size).toFloat()
        (100.percent - partitionTotalSize - spacingSize) / overflowedChildren
    }
    val spacingValue = spacing.apply(availableWidth)
    val sizes = partitions.toList() + (partitions.size until childBuilders.size).map { overflowAllocation }
    val allocatedChildren = (if (reversed) childBuilders.reversed() else childBuilders)
        .zip(sizes)

    LayoutContext.Child(null, null) {
        var lastX = 0f

        allocatedChildren.forEachIndexed { i, (child, size) ->
            val fixedSize = if (i == allocatedChildren.lastIndex) 100.percent - lastX.px else size
            val sizeValue = fixedSize.apply(availableWidth)
            val sizeRounded = round(sizeValue)
            val c = child(sizeRounded, availableHeight, sizeRounded, availableHeight)

            region.copy(x = region.x + lastX, width = sizeRounded).draw { c.draw(this) }

            lastX += round(sizeValue + spacingValue)
        }
    }
}

@Undocumented
fun DefinedLayoutContext.vdiv(
    vararg partitions: Pixels,
    spacing: Pixels = 0.px,
    reversed: Boolean = false,
    init: DefinedLayoutContext.() -> Unit,
) = includeChild { _, _, availableWidth, availableHeight ->
    val childBuilders = mutableListOf<(Float, Float, Float, Float) -> LayoutContext.Child<Nothing?, Nothing?>>()
    val context = object: DefinedLayoutContext {
        override fun includeChild(init: (Float, Float, Float, Float) -> LayoutContext.Child<Nothing?, Nothing?>) {
            childBuilders.add(init)
        }
    }

    context.init()

    val overflowAllocation = if (childBuilders.size <= partitions.size) 0.px
    else run {
        val partitionTotalSize = partitions.fold(0.px) { a, b -> a + b }
        val spacingSize = spacing * (childBuilders.size - 1).toFloat()
        val overflowedChildren = (childBuilders.size - partitions.size).toFloat()
        (100.percent - partitionTotalSize - spacingSize) / overflowedChildren
    }
    val spacingValue = spacing.apply(availableHeight)
    val sizes = partitions.toList() + (partitions.size until childBuilders.size).map { overflowAllocation }
    val allocatedChildren = (if (reversed) childBuilders.reversed() else childBuilders)
        .zip(sizes)

    LayoutContext.Child(null, null) {
        var lastY = 0f
        allocatedChildren.forEachIndexed { i, (child, size) ->
            val fixedSize = if (i == allocatedChildren.lastIndex) 100.percent - lastY.px else size
            val sizeValue = fixedSize.apply(availableHeight)
            val sizeRounded = round(sizeValue)
            val c = child(availableWidth, sizeRounded, availableWidth, sizeRounded)

            region.copy(y = region.y + lastY, height = sizeRounded).draw { c.draw(this) }

            lastY += round(sizeValue + spacingValue)
        }
    }
}
