import com.exerro.simpleui.*
import com.exerro.simpleui.colour.Colours
import com.exerro.simpleui.event.KeyModifier
import com.exerro.simpleui.extensions.filterKeyPressed

/** A model to keep track of the information we want to show. */
data class EventModel(
    val text: String,
    val colourToggle: Boolean,
)

/** Draw the model to a window. */
fun Window.drawModel(model: EventModel) = draw {
    // Fill the screen with colour (black).
    fill(Colours.black)

    // Take the region representing the screen, and resize to 256x32px. By
    // default, resizing down like this leaves the resultant region centred.
    withRegion(region.resizeTo(256.px, 32.px)) {
        // Draw an outline shadow for this region.
        shadow()
        // Fill the region with a teal or red background, depending on the
        // model's colour toggle.
        fill(if (model.colourToggle) Colours.teal else Colours.red)
        // Write "Hello world!" in white. By default, text is centred.
        write("Hello world!", Colours.white)
    }

    // Draw a region at the bottom of the screen containing the model text
    withRegion(region.resizeTo(width = 100.percent, height = 64.px, verticalAlignment = 1f)) {
        fill(Colours.charcoal)

        withRegion(region.withPadding(16.px)) {
            write(model.text, Colours.white, horizontalAlignment = 0f)
        }
    }
}

/** Demonstrates an application which graphically responds to events. */
fun main() {
    // Create a window, titled "Events".
    val window = GLFWWindowCreator.createWindow("Events")

    // Keep a mutable model around. There are nicer ways of doing this, but for
    // the sake of simplicity...
    var model = EventModel("No events yet", true)

    // Draw the window initially.
    window.drawModel(model)

    // Update the model with the string representation of events received.
    window.events.connect { event ->
        model = model.copy(text = event.toString())
        window.drawModel(model)
    }

    // When space is pressed, toggle the colour in the model
    window.events.filterKeyPressed("space").connect {
        model = model.copy(colourToggle = !model.colourToggle)
        window.drawModel(model)
    }

    // When ctrl+W is pressed, close the window
    window.events.filterKeyPressed("w", KeyModifier.Control).connect {
        window.close()
    }

    // While the window hasn't been closed, update the windowing system we're
    // using (GLFWWindowCreator).
    while (!window.isClosed) GLFWWindowCreator.update()
}
