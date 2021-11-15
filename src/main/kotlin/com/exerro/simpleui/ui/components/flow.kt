package com.exerro.simpleui.ui.components

import com.exerro.simpleui.*
import com.exerro.simpleui.ui.ResolvedComponent
import com.exerro.simpleui.ui.ParentContext
import com.exerro.simpleui.ui.UIModel

@UndocumentedExperimental
fun <Model: UIModel> ParentContext<Model, Float, Nothing?, Nothing?, Float>.flow(
    verticalSpacing: Pixels = 0.px,
    horizontalSpacing: Pixels = verticalSpacing,
    reversed: Boolean = false,
    reverseRows: Boolean = false,
    reverseColumns: Boolean = false,
    verticalRowAlignment: Alignment = 0.5f,
    horizontalRowAlignment: Alignment = 0.5f,
    init: ParentContext<Model, Nothing?, Nothing?, Float, Float>.() -> Unit
) = rawComponent("flow") {
    children(init) { width, _, availableWidth, availableHeight, drawFunctions, eventHandlers, children ->
        val verticalSpacingValue = verticalSpacing.apply(availableHeight)
        val horizontalSpacingValue = horizontalSpacing.apply(availableWidth)
        val rows = mutableListOf<List<ResolvedComponent<Float, Float>>>()
        val thisRow = mutableListOf<ResolvedComponent<Float, Float>>()
        var thisRowAccumulatedWidth = 0f
        val allEventHandlers = eventHandlers.toMutableList()

        for (child in if (reversed) children.reversed() else children) {
            val c = child(null, null, availableWidth, availableHeight)
            val extraWidth = if (thisRow.isEmpty()) 0f else horizontalSpacingValue

            allEventHandlers.addAll(c.eventHandlers)

            if (thisRowAccumulatedWidth + extraWidth + c.width > width && thisRow.isNotEmpty()) {
                rows.add(thisRow.toList())
                thisRow.clear()
                thisRow.add(c)
                thisRowAccumulatedWidth = c.width
            }
            else {
                thisRow.add(c)
                thisRowAccumulatedWidth += extraWidth + c.width
            }
        }

        rows += thisRow

        val rowWidths = rows.map { row -> row.fold(0f) { a, b -> a + b.width } + horizontalSpacingValue * (row.size - 1) }
        val rowHeights = rows.map { row -> if (row.isEmpty()) 0f else row.maxOf { it.height } }
        val sumHeight = rowHeights.fold(0f) { a, b -> a + b }
        val totalHeight = sumHeight + verticalSpacingValue * (rows.size - 1)

        ResolvedComponent(null, totalHeight, allEventHandlers) {
            var lastY = 0f

            for (f in drawFunctions) f(this)

            for ((rowIndex, row) in if (reverseRows) rows.withIndex().reversed() else rows.withIndex()) {
                val rowWidth = rowWidths[rowIndex]
                val rowHeight = rowHeights[rowIndex]
                var lastX = (width - rowWidth) * horizontalRowAlignment

                for (child in if (reverseColumns) row.reversed() else row) {
                    val valignOffset = (rowHeight - child.height) * verticalRowAlignment

                    region.copy(
                        x = region.x + lastX,
                        y = region.y + lastY + valignOffset,
                        width = child.width,
                        height = child.height
                    ).draw(draw = child.draw)

                    lastX += child.width + horizontalSpacingValue
                }

                lastY += rowHeight + verticalSpacingValue
            }
        }
    }
}
