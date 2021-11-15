import com.exerro.simpleui.*
import com.exerro.simpleui.colour.Colour
import com.exerro.simpleui.colour.Colours
import com.exerro.simpleui.extensions.*
import com.exerro.simpleui.ui.Style
import com.exerro.simpleui.ui.get
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
    val baseStyle get() = if (darkTheme) Style.Dark else Style.Light
    val style get() = Style.combine(
        baseStyle,
        Style.fromMap(mapOf(Style.PrimaryBackgroundColour to primaryColour), baseStyle.attributes)
    )
    val backgroundColour: Colour get() = style[Style.BackgroundColour]
    val elementBackgroundColour: Colour get() = style[Style.ElementBackgroundColour]
    val alternateBackgroundColour: Colour get() = style[Style.AlternateBackgroundColour]
    val shadowColour: Colour get() = style[Style.ShadowColour]
    val textColour: Colour get() = style[Style.ForegroundColour]
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
            write(rows[y].name, model.style[Style.HeaderForegroundColour], horizontalAlignment = 0f, font = Font.heading)
        }

        if (y > 0) rowHeader.above(16.px).resizeTo(height = model.style[Style.SeparatorThickness].px).rounded().draw {
            fill(model.style[Style.SeparatorColour])
        }

        for ((x, drawElement) in row.elements.withIndex()) {
            rowItems[x].draw { drawElement(model, model.focusedRow == y && model.focusedColumn == x) }
        }
    }
}

/** Demonstrates a simple application with a single window with "Hello world"
 *  written in the centre. */
fun main() {
    // Create a window, titled "Elements".
    val window = GLFWWindowCreator.createWindow("Elements")

    // Keep track of mutable state
    var currentModel = ElementModel(
        focusedRow = 0,
        focusedColumn = 0,
        darkTheme = false,
        primaryColour = Colours.teal,
        movedAt = window.createdAt,
    )

    // Define the rows in the stuff we're drawing
    val rowData = listOf("Dropdowns" to 128.px)
    val rows = rowData.map { (name, height) -> Row(name, height + 8.px + Font.heading.lineHeight.px, mutableListOf()) }

    // Add a utility function to simplify adding elements
    fun addElement(row: Int, column: Int, draw: DrawContext.(model: ElementModel, focused: Boolean) -> Unit) {
        rows[row].elements.add(column, draw)
    }

    // Add dropdowns
    addElement(0, 0) { model, focused ->
        region.resizeTo(height = 32.px, verticalAlignment = 0f).draw {
            dropdown("Option 1", emptyList(), model.elementBackgroundColour, model.primaryColour, model.shadowColour, focused, iconColour = model.textColour) {
                region.withPadding(left = 16.px).draw {
                    write(it, model.textColour, horizontalAlignment = 0f)
                }
            }
        }
    }

    addElement(0, 1) { model, focused ->
        region.resizeTo(height = 32.px, verticalAlignment = 0f).draw {
            dropdown("Option 1", listOf("Option 1", "Option 2", "Option 3"), model.elementBackgroundColour, model.primaryColour, model.textColour, model.shadowColour, focused, 1, iconColour = model.textColour)
        }
    }

    addElement(0, 2) { model, focused ->
        region.resizeTo(height = 32.px, verticalAlignment = 0f).draw {
            dropdown("Option 1", listOf("Option 1", "Option 2", "Option 3"), model.elementBackgroundColour, model.primaryColour, model.textColour, model.shadowColour, focused, 0, iconColour = model.textColour)
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
