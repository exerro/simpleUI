import com.exerro.simpleui.GLFWWindowCreator
import com.exerro.simpleui.colour.Colours
import com.exerro.simpleui.px

/** Demonstrates a simple application with a single window with "Hello world"
 *  written in the centre. */
fun main() {
    // Create a window, titled "Hello World".
    val window = GLFWWindowCreator.createWindow("Hello World")

    // Draw to this window. The function provided here may be called many times,
    // for example after the window resizes, or during some animation.
    window.draw {
        // Fill the screen with colour (black).
        fill(Colours.black)

        // Take the region representing the screen, and resize to 256x32px. By
        // default, resizing down like this leaves the resultant region centred.
        withRegion(region.resizeTo(256.px, 32.px)) {
            // Draw an outline shadow for this region.
            shadow()
            // Fill the region with a teal background.
            fill(Colours.teal)
            // Write "Hello world!" in white. By default, text is centred.
            write("Hello world!", Colours.white)
        }
    }

    // While the window hasn't been closed, update the windowing system we're
    // using (GLFWWindowCreator).
    while (!window.isClosed) GLFWWindowCreator.update()
}
