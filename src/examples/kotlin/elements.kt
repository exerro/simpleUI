import com.exerro.simpleui.*
import com.exerro.simpleui.colour.Colour
import com.exerro.simpleui.colour.Colours
import com.exerro.simpleui.colour.RGBA
import com.exerro.simpleui.extensions.*
import kotlin.time.TimeMark

/** Element data for a row of elements. [name] is a label for the row, [height]
 *  is the total height of the row. [elements] is a list of draw functions. */
data class Row(
    val name: String,
    val height: Pixels,
    val elements: MutableList<DrawContext.(ElementModel, Boolean) -> Unit>,
)

/** Keep track of the mutable state of the application. */
data class ElementModel(
    val focusedRow: Int,
    val focusedColumn: Int,
    val darkTheme: Boolean,
    val primaryColour: Colour,
    val movedAt: TimeMark,
) {
    val backgroundColour: Colour get() = if (darkTheme) RGBA(0x18, 0x19, 0x19) else RGBA(0xe8, 0xe9, 0xe9)
    val elementBackgroundColour: Colour get() = if (darkTheme) RGBA(0x1e, 0x1f, 0x1f) else RGBA(0xf1, 0xf2, 0xf2)
    val alternateBackgroundColour: Colour get() = if (darkTheme) backgroundColour.darken(0.05f) else backgroundColour.darken(0.05f)
    val shadowColour: Colour get() = if (darkTheme) Colours.pureBlack else Colours.lightGrey.withAlpha(0.8f)
    val textColour: Colour get() = if (darkTheme) Colours.white else Colours.black
    val alternateTextColour: Colour get() = if (darkTheme) Colours.lightGrey else Colours.grey
    val disabledColour: Colour get() = if (darkTheme) Colours.grey else Colours.lightGrey
}

/** Draw the model and rows of elements to the window. */
fun Window.drawModel(model: ElementModel, rows: List<Row>) = draw {
    val header = region.resizeTo(height = 128.px, verticalAlignment = 0f)
    val rowRegions = rows.runningFold(header.withPadding(horizontal = 32.px).below((-16).px)) { above, row ->
        above.below(32.px).below(height = row.height)
    } .drop(1)

    fill(model.backgroundColour)

    header.draw {
        fill(model.alternateBackgroundColour)
        write("WASD to move focus\nP to change primary colour\nT to toggle theme", model.textColour, wordWrap = true)
    }

    for ((y, row) in rows.withIndex()) {
        val (rowHeader, _, rowContent) = rowRegions[y].splitVertically(Font.heading.lineHeight.px, Font.heading.lineHeight.px + 8.px)
        val rowItems = rowContent.listHorizontally(192.px, spacing = 32.px)

        rowHeader.draw {
            write(rows[y].name, model.alternateTextColour, horizontalAlignment = 0f, font = Font.heading)
        }

        if (y > 0) rowHeader.above(16.px).resizeTo(height = 1.px).rounded().draw {
            fill(model.disabledColour)
        }

        for ((x, drawElement) in row.elements.withIndex()) {
            rowItems[x].draw { drawElement(model, model.focusedRow == y && model.focusedColumn == x) }
        }
    }
}

/** Demonstrates a simple application with a single window with "Hello world"
 *  written in the centre. */
fun main() {
    // Create a window, titled "HelloWorld".
    val window = GLFWWindowCreator.createWindow("Elements")

    // Keep track of mutable state
    var currentModel = ElementModel(
        focusedRow = 0,
        focusedColumn = 0,
        darkTheme = true,
        primaryColour = Colours.teal,
        movedAt = window.createdAt,
    )

    // Define the rows in the stuff we're drawing
    val rowData = listOf("Buttons" to 48.px, "Text inputs" to 48.px, "Sliders" to 24.px, "Dropdowns" to 128.px, "Toggles" to 192.px)
    val rows = rowData.map { (name, height) -> Row(name, height + 8.px + Font.heading.lineHeight.px, mutableListOf()) }

    // Add a utility function to simplify adding elements
    fun addElement(row: Int, column: Int, draw: DrawContext.(model: ElementModel, focused: Boolean) -> Unit) {
        rows[row].elements.add(column, draw)
    }

    // Add buttons
    addElement(0, 0) { model, focused ->
        region.resizeTo(height = 32.px).draw {
            button("CONFIRM", model.primaryColour, highlightColour = Colours.white, textColour = Colours.white, shadowColour = model.shadowColour, focused = focused)
        }
    }

    addElement(0, 1) { model, focused ->
        region.resizeTo(height = 32.px).draw {
            button("CANCEL", Colours.red, highlightColour = Colours.white, textColour = Colours.white, shadowColour = model.shadowColour, focused = focused)
        }
    }

    addElement(0, 2) { model, focused ->
        region.resizeTo(height = 32.px).draw {
            button("DISABLED", model.disabledColour, highlightColour = Colours.white, textColour = Colours.white, shadowColour = model.shadowColour, focused = focused)
        }
    }

    addElement(0, 3) { model, focused ->
        region.resizeTo(height = 32.px).draw {
            button("ACTION", model.elementBackgroundColour, model.primaryColour, model.textColour, model.shadowColour, focused = focused)
        }
    }

    addElement(0, 4) { model, focused ->
        region.resizeTo(height = 32.px).draw {
            button("SEARCH", model.elementBackgroundColour, model.primaryColour, model.textColour, model.shadowColour, focused = focused, icon = "images/search.png")
        }
    }

    addElement(0, 5) { model, focused ->
        region.resizeTo(width = 48.px, horizontalAlignment = 0f).draw {
            iconButton("images/search.png", model.elementBackgroundColour, highlightColour = model.primaryColour, model.textColour, model.shadowColour, focused = focused)
        }
    }

    // Add text inputs
    addElement(1, 0) { model, focused ->
        val textBuffer = TextBufferBuilder {
            if (focused) emitCursor(model.primaryColour, model.movedAt)
            emitText("Placeholder...", model.alternateTextColour)
        }

        region.resizeTo(height = 48.px).draw {
            textInput(textBuffer, model.elementBackgroundColour, model.primaryColour, model.shadowColour, focused)
        }
    }

    addElement(1, 1) { model, focused ->
        val textBuffer = TextBufferBuilder(defaultColour = model.textColour) {
            emitText("I")
            if (focused) beginDecoration(TextBuffer.Decoration.Highlight, Colours.red.withAlpha(0.4f))
            emitText("nva")
            if (focused) stopDecoration(TextBuffer.Decoration.Highlight)
            if (focused) emitCursor(Colours.red)
            emitText("lid")
        }

        region.resizeTo(height = 48.px).draw {
            textInput(textBuffer, model.elementBackgroundColour, Colours.red, model.shadowColour, focused)
        }
    }

    addElement(1, 2) { model, focused ->
        val textBuffer = TextBufferBuilder {
            emitText("Disabled", model.textColour)
        }

        region.resizeTo(height = 48.px).draw {
            textInput(textBuffer, model.elementBackgroundColour, model.disabledColour, model.shadowColour, focused)
        }
    }

    addElement(1, 3) { model, focused ->
        val textBuffer = TextBufferBuilder {
            emitText("Something", model.textColour)
            if (focused) emitCursor(model.primaryColour, model.movedAt)
        }

        region.resizeTo(height = 48.px).draw {
            textInput(textBuffer, model.elementBackgroundColour, model.primaryColour, model.shadowColour, focused = focused, icon = "images/search.png", iconColour = model.textColour)
        }
    }

    // Add sliders
    addElement(2, 0) { model, focused ->
        slider(0.3f, model.elementBackgroundColour, model.primaryColour.takeIf { focused }, model.shadowColour)
    }

    addElement(2, 1) { model, _ ->
        region.resizeTo(height = 24.px).draw {
            progress(0.3f, model.elementBackgroundColour, model.primaryColour, null, model.shadowColour)
        }
    }

    addElement(2, 2) { model, _ ->
        region.resizeTo(height = 24.px).draw {
            progress(0.6f, model.elementBackgroundColour, model.primaryColour, Colours.white, model.shadowColour)
        }
    }

    addElement(2, 3) { model, _ ->
        region.resizeTo(height = 24.px).draw {
            progress(0.2f, model.elementBackgroundColour, model.primaryColour, Colours.white, model.shadowColour)
        }
    }

    // Add dropdowns
    addElement(3, 0) { model, focused ->
        region.resizeTo(height = 32.px, verticalAlignment = 0f).draw {
            dropdown("Option 1", emptyList(), model.elementBackgroundColour, model.primaryColour, model.shadowColour, focused, iconColour = model.textColour) {
                region.withPadding(left = 16.px).draw {
                    write(it, model.textColour, horizontalAlignment = 0f)
                }
            }
        }
    }

    addElement(3, 1) { model, focused ->
        region.resizeTo(height = 32.px, verticalAlignment = 0f).draw {
            dropdown("Option 1", listOf("Option 1", "Option 2", "Option 3"), model.elementBackgroundColour, model.primaryColour, model.textColour, model.shadowColour, focused, 1, iconColour = model.textColour)
        }
    }

    addElement(3, 2) { model, focused ->
        region.resizeTo(height = 32.px, verticalAlignment = 0f).draw {
            dropdown("Option 1", listOf("Option 1", "Option 2", "Option 3"), model.elementBackgroundColour, model.primaryColour, model.textColour, model.shadowColour, focused, 0, iconColour = model.textColour)
        }
    }

    // Add toggles
    // TODO: these need standardising
    addElement(4, 0) { model, focused ->
        region.listVertically(24.px, spacing = 8.px).take(6).draw { index ->
            val (button, _, label) = region.splitHorizontally(at1 = region.height.px, at2 = region.height.px + 8.px)
            val textBuffer = TextBufferBuilder("Radio button ${"$index".repeat(index)}", model.textColour)
            val textBufferRegion = label.withPadding(4.px).draw { textBufferBounds(textBuffer, horizontalAlignment = 0f) }

            if (focused && index == 3) {
                listOf(button.withPadding(left = (-4).px), textBufferRegion.withPadding(right = (-8).px)).boundingRegion().draw {
                    shadow(model.shadowColour, cornerRadius = 8.px)
                    roundedRectangle(8.px, model.elementBackgroundColour)
                }
            }

            button.withPadding(4.px).draw {
                shadow(model.shadowColour, cornerRadius = 50.percent)
                ellipse(model.elementBackgroundColour)
                if (index == 2) region.withPadding(4.px).draw { ellipse(model.primaryColour) }
            }

            label.withPadding(4.px).draw {
                write(textBuffer, horizontalAlignment = 0f)
            }
        }
    }

    // TODO: these need standardising
    addElement(4, 1) { model, focused ->
        region.listVertically(24.px, spacing = 8.px).take(6).draw { index ->
            val (button, _, label) = region.splitHorizontally(at1 = region.height.px, at2 = region.height.px + 8.px)
            val textBuffer = TextBufferBuilder("Checkbox ${"$index".repeat(index)}", model.textColour)
            val textBufferRegion = label.withPadding(4.px).draw { textBufferBounds(textBuffer, horizontalAlignment = 0f) }

            if (focused && index == 4) {
                listOf(button.withPadding(left = (-4).px), textBufferRegion.withPadding(right = (-8).px)).boundingRegion().draw {
                    shadow(model.shadowColour, cornerRadius = 8.px)
                    roundedRectangle(8.px, model.elementBackgroundColour)
                }
            }

            button.withPadding(4.px).draw {
                shadow(model.shadowColour, cornerRadius = 2.px)
                roundedRectangle(2.px, model.elementBackgroundColour)
                if (index % 2 == 1) region.withPadding(4.px).draw { fill(model.primaryColour) }
            }

            label.withPadding(4.px).draw {
                write(textBuffer, horizontalAlignment = 0f)
            }
        }
    }

    // TODO: these need standardising and adding a focus graphic
    addElement(4, 2) { model, _ ->
        region.listVertically(24.px, spacing = 8.px).take(6).draw { index ->
            val r = region.resizeTo(width = 96.px, horizontalAlignment = 0f)
            val (left, right) = r.withPadding(horizontal = 4.px).splitHorizontally()
            val isOn = index % 3 == 1
            val drawAnything = index % 2 == 0

            if (drawAnything) r.draw {
                shadow(cornerRadius = 50.percent, colour = model.shadowColour)
                roundedRectangle(50.percent, model.elementBackgroundColour)
            }

            if (drawAnything) r.withPadding(4.px).resizeTo(width = 50.percent, horizontalAlignment = if (isOn) 0f else 1f).draw {
                roundedRectangle(50.percent, model.primaryColour)
            }

            if (drawAnything) left.draw(clip = true) {
                left.resizeTo(region.width.px + region.height.px, horizontalAlignment = 0f).draw {
//                    roundedRectangle(50.percent, if (isOn) primaryColour else model.lighterBackgroundColour)
                }

                region.withPadding(4.px).draw { write("ON", colour = if (isOn) Colours.white else model.textColour, font = Font.default.copy(lineHeight = Font.default.lineHeight * 0.8f)) }
            }

            if (drawAnything) right.draw(clip = true) {
                right.resizeTo(region.width.px + region.height.px, horizontalAlignment = 1f).draw {
//                    roundedRectangle(50.percent, if (!isOn) primaryColour else model.lighterBackgroundColour)
                }

                region.withPadding(4.px).draw { write("OFF", colour = if (!isOn) Colours.white else model.textColour, font = Font.default.copy(lineHeight = Font.default.lineHeight * 0.8f)) }
            }
        }
    }

    // Draw the initial window.
    window.drawModel(currentModel, rows)

    // Move the focus to the left when A is pressed.
    window.events.filterKeyPressed("a").connect {
        if (currentModel.focusedColumn > 0) {
            currentModel = currentModel.copy(
                focusedColumn = currentModel.focusedColumn - 1,
                movedAt = window.timeSource.markNow()
            )
            window.drawModel(currentModel, rows)
        }
    }

    // Move the focus to the right when D is pressed.
    window.events.filterKeyPressed("d").connect {
        if (currentModel.focusedColumn < rows[currentModel.focusedRow].elements.lastIndex) {
            currentModel = currentModel.copy(
                focusedColumn = currentModel.focusedColumn + 1,
                movedAt = window.timeSource.markNow()
            )
            window.drawModel(currentModel, rows)
        }
    }

    // Move the focus upwards when W is pressed.
    window.events.filterKeyPressed("w").connect {
        if (currentModel.focusedRow > 0) {
            currentModel = currentModel.copy(
                focusedRow = currentModel.focusedRow - 1,
                movedAt = window.timeSource.markNow()
            )
            // TODO: check column too!
            window.drawModel(currentModel, rows)
        }
    }

    // Move the focus downwards when S is pressed.
    window.events.filterKeyPressed("s").connect {
        if (currentModel.focusedRow < rows.lastIndex) {
            currentModel = currentModel.copy(
                focusedRow = currentModel.focusedRow + 1,
                movedAt = window.timeSource.markNow()
            )
            // TODO: check column too!
            window.drawModel(currentModel, rows)
        }
    }

    // Toggle the theme when D is pressed.
    window.events.filterKeyPressed("t").connect {
        currentModel = currentModel.copy(darkTheme = !currentModel.darkTheme)
        window.drawModel(currentModel, rows)
    }

    // Cycle the primary colour when P is pressed.
    window.events.filterKeyPressed("p").connect {
        val index = Colours.colours.indexOf(currentModel.primaryColour)
        val newIndex = (index + 1) % Colours.colours.size
        currentModel = currentModel.copy(primaryColour = Colours.colours[newIndex])
        window.drawModel(currentModel, rows)
    }

    // Close the window when ctrl+W is pressed.
    window.events.filterKeyPressed("w", KeyModifier.Control)
        .connect { window.close() }

    // While the window hasn't been closed, update the windowing system we're
    // using (GLFWWindowCreator).
    while (!window.isClosed) GLFWWindowCreator.update()
}
