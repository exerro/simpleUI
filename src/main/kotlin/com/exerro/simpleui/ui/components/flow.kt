package com.exerro.simpleui.ui.components

import com.exerro.simpleui.Alignment
import com.exerro.simpleui.Pixels
import com.exerro.simpleui.UndocumentedExperimentalUI
import com.exerro.simpleui.px
import com.exerro.simpleui.ui.*

@UndocumentedExperimentalUI
fun <Model: UIModel> ComponentChildrenContext<Model, ParentDefinesMe, ChildDefinesMe>.flow(
    verticalSpacing: Pixels = 0.px,
    horizontalSpacing: Pixels = verticalSpacing,
    reversed: Boolean = false,
    reverseRows: Boolean = false,
    reverseColumns: Boolean = false,
    verticalRowAlignment: Alignment = 0.5f,
    horizontalRowAlignment: Alignment = 0.5f,
    init: ComponentChildrenContext<Model, ChildDefinesMe, ChildDefinesMe>.() -> Unit
) = rawComponent("flow") {
    children(init) { width, _, availableWidth, availableHeight, drawFunctions, eventHandlers, children ->
        val verticalSpacingValue = verticalSpacing.apply(availableHeight)
        val horizontalSpacingValue = horizontalSpacing.apply(availableWidth)
        val rows = mutableListOf<List<SizeResolvedComponent<ChildDefinesMe, ChildDefinesMe>>>()
        val thisRow = mutableListOf<SizeResolvedComponent<ChildDefinesMe, ChildDefinesMe>>()
        var thisRowAccumulatedWidth = 0f
        val allEventHandlers = eventHandlers.toMutableList()

        for (child in if (reversed) children.reversed() else children) {
            val c = child(nothingForChild(), nothingForChild(), availableWidth, availableHeight)
            val extraWidth = if (thisRow.isEmpty()) 0f else horizontalSpacingValue

            allEventHandlers.addAll(c.eventHandlers)

            if (thisRowAccumulatedWidth + extraWidth + fixFromChild(c.width) > fixFromParent(width) && thisRow.isNotEmpty()) {
                rows.add(thisRow.toList())
                thisRow.clear()
                thisRow.add(c)
                thisRowAccumulatedWidth = fixFromChild(c.width)
            }
            else {
                thisRow.add(c)
                thisRowAccumulatedWidth += extraWidth + fixFromChild(c.width)
            }
        }

        rows += thisRow

        val rowWidths = rows.map { row -> row.fold(0f) { a, b -> a + fixFromChild(b.width) } + horizontalSpacingValue * (row.size - 1) }
        val rowHeights = rows.map { row -> if (row.isEmpty()) 0f else row.maxOf { fixFromChild(it.height) } }
        val sumHeight = rowHeights.fold(0f) { a, b -> a + b }
        val totalHeight = sumHeight + verticalSpacingValue * (rows.size - 1)

        SizeResolvedComponent(nothingForParent(), fixForParent(totalHeight), allEventHandlers) {
            var lastY = 0f

            for (f in drawFunctions) f(this)

            for ((rowIndex, row) in if (reverseRows) rows.withIndex().reversed() else rows.withIndex()) {
                val rowWidth = rowWidths[rowIndex]
                val rowHeight = rowHeights[rowIndex]
                var lastX = (fixFromParent(width) - rowWidth) * horizontalRowAlignment

                for (child in if (reverseColumns) row.reversed() else row) {
                    val valignOffset = (rowHeight - fixFromChild(child.height)) * verticalRowAlignment

                    withRegion(region.copy(
                        x = region.x + lastX,
                        y = region.y + lastY + valignOffset,
                        width = fixFromChild(child.width),
                        height = fixFromChild(child.height),
                    ), draw = child.draw)

                    lastX += fixFromChild(child.width) + horizontalSpacingValue
                }

                lastY += rowHeight + verticalSpacingValue
            }
        }
    }
}
