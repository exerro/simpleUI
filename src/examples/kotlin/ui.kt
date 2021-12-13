import com.exerro.simpleui.px
import com.exerro.simpleui.ui.*
import com.exerro.simpleui.ui.components.ButtonType
import com.exerro.simpleui.ui.components.button
import com.exerro.simpleui.ui.components.flow
import com.exerro.simpleui.ui.components.vdiv
import com.exerro.simpleui.ui.extensions.bind
import com.exerro.simpleui.ui.extensions.noChildren
import com.exerro.simpleui.ui.extensions.useState
import com.exerro.simpleui.ui.modifiers.withAlignment
import com.exerro.simpleui.ui.modifiers.withPadding
import com.exerro.simpleui.ui.modifiers.withWidth

/** Main function which runs the application. We're using
 *  [UIController.runDefaultApp] which wraps up the window creation, event
 *  binding, and window drawing, allowing us to just focus on the UI.
 *  See [UIController.runDefaultApp] to see exactly what it does. */
fun main() = UIController.runDefaultApp("UI Example") {
    // Keep track of whether we're showing the dark theme or light theme.
    val (darkTheme, setDarkTheme) = useState(true)

    // When we draw, clear the screen to the model style's background
    // colour.
    onDraw {
        fill(model.style[Style.BackgroundColour])
    }

    // When we press the T key, update the model, incrementing the theme
    // value.
    bind(ActionKeybind("t")) {
        setDarkTheme(!darkTheme)
        setModel(UIModel(style = when (darkTheme) {
            true -> Style.Light
            else -> Style.Dark
        }))
        true
    }

    // Divide the screen vertically, with the first section getting 128
    // pixels of height.
    vdiv(128.px) {
        // Declare a header component. This is needed to define its onDraw.
        component {
            onDraw {
                // When we draw, fill this component with the model style's
                // alternate background colour and write "Header text".
                fill(model.style[Style.AlternateBackgroundColour])
                write("Header text", model.style[Style.AlternateForegroundColour])
            }

            // We must declare that the component has no children, since it
            // doesn't wrap any component or have any children.
            noChildren()
        }

        // Add a "flow" element with modifiers applied:
        // * withAlignment allows following modifiers/components to specify
        //   their height, rather than inheriting it from the parent
        // * withWidth sets the height of following modifiers/components
        // * withPadding adds padding to the following modifiers/components
        // The flow element here lays children out top-to-bottom,
        // left-to-right, wrapping when the width of a row exceeds 768px.
        // Spacing between elements is provided in each axis.
        this
            .withAlignment(verticalAlignment = 0f, horizontalAlignment = 0.5f)
            .withWidth(768.px)
            .withPadding(32.px)
            .flow(
                horizontalSpacing = 16.px,
                verticalSpacing = 32.px,
            ) {
                // For each button type, add a button of that type to the
                // flow.
                for (type in ButtonType.values()) {
                    button(type.name, type = type)
                }
            }
    }
}
