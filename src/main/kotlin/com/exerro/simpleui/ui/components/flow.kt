package com.exerro.simpleui.ui.components

import com.exerro.simpleui.Alignment
import com.exerro.simpleui.Pixels
import com.exerro.simpleui.UndocumentedExperimentalUI
import com.exerro.simpleui.px
import com.exerro.simpleui.ui.*
import com.exerro.simpleui.ui.internal.standardChildRendering

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
) = component("flow") {
    children(init) { width, _, availableWidth, availableHeight, drawFunctions, eventHandlers, children ->
        val verticalSpacingValue = verticalSpacing.apply(availableHeight)
        val horizontalSpacingValue = horizontalSpacing.apply(availableWidth)
        val rows = mutableListOf<List<SizeResolvedComponent<ChildDefinesMe, ChildDefinesMe>>>()
        val thisRow = mutableListOf<SizeResolvedComponent<ChildDefinesMe, ChildDefinesMe>>()
        var thisRowAccumulatedWidth = 0f

        for (child in if (reversed) children.reversed() else children) {
            val c = child(nothingForChild(), nothingForChild(), availableWidth, availableHeight)
            val extraWidth = if (thisRow.isEmpty()) 0f else horizontalSpacingValue

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

        SizeResolvedComponent(nothingForParent(), fixForParent(totalHeight)) { r ->
            var lastY = 0f
            val positionResolvedChildren = (if (reverseRows) rows.withIndex().reversed() else rows.withIndex()).flatMap { (rowIndex, row) ->
                val rowWidth = rowWidths[rowIndex]
                val rowHeight = rowHeights[rowIndex]
                var lastX = (fixFromParent(width) - rowWidth) * horizontalRowAlignment
                val thisY = lastY

                lastY += rowHeight + verticalSpacingValue

                (if (reverseColumns) row.reversed() else row).map { child ->
                    val valignOffset = (rowHeight - fixFromChild(child.height)) * verticalRowAlignment
                    val thisX = lastX

                    lastX += fixFromChild(child.width) + horizontalSpacingValue

                    child.positionResolver(r.copy(
                        x = r.x + thisX,
                        y = r.y + thisY + valignOffset,
                        width = fixFromChild(child.width),
                        height = fixFromChild(child.height),
                    ))
                }
            }

            standardChildRendering(r, drawFunctions, eventHandlers, positionResolvedChildren)
        }
    }
}
