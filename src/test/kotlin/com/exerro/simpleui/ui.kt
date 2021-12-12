package com.exerro.simpleui

import com.exerro.simpleui.colour.Colours
import com.exerro.simpleui.extensions.progress
import com.exerro.simpleui.extensions.slider
import com.exerro.simpleui.extensions.textInput
import com.exerro.simpleui.ui.*
import com.exerro.simpleui.ui.components.*
import com.exerro.simpleui.ui.hooks.useState
import com.exerro.simpleui.ui.modifiers.withHeight
import com.exerro.simpleui.ui.modifiers.withHorizontalAlignment
import com.exerro.simpleui.ui.modifiers.withPadding
import com.exerro.simpleui.ui.modifiers.withVerticalAlignment

data class MyModel(
    val window: Window,
    val theme: Int = 0,
): UIModel {
    override val keybinds = ActionKeybinds.Default
    override val style = when (theme % 3) {
        0 -> Style.Dark
        1 -> Style.Accessible
        else -> Style.Light
    }
}

fun <Model: UIModel> ComponentChildrenContext<Model, ParentDefinesMe, ChildDefinesMe>.labelledSection(
    label: String,
    init: ComponentChildrenContext<Model, ParentDefinesMe, ChildDefinesMe>.() -> Unit,
) = withPadding(bottom = 16.px).vflow(spacing = 8.px) {
    component {
        onDraw {
            write(label, model.style[Style.HeaderForegroundColour], horizontalAlignment = 0f, font = Font.heading)
        }

        noChildrenDefineDefaultHeight(height = Font.heading.lineHeight)
    }
    init()
}

fun main() {
    val window = GLFWWindowCreator.createWindow("UI")

    val component = UIController(MyModel(window)) {
        onDraw {
            fill(model.style[Style.BackgroundColour])
        }

        bind(ActionKeybind("t")) {
            updateModel { it.copy(theme = it.theme + 1) }
            true
        }

        bind(ActionKeybind("w", setOf(KeyModifier.Control))) {
            window.close()
            true
        }

        vdiv(128.px) {
            component {
                onDraw {
                    fill(model.style[Style.AlternateBackgroundColour])
                    write(
                        text = "WASD to move focus\nP to change primary colour\nT to toggle theme",
                        colour = model.style[Style.AlternateForegroundColour],
                        wordWrap = true
                    )
                }
                noChildren()
            }

            verticalSelector(true, maximumValue = 4) { selectedRow ->
                withPadding(32.px).withVerticalAlignment(0f).vflow(
                    spacing = 32.px,
                    showSeparators = true,
                ) {
                    labelledSection("Buttons") {
                        horizontalSelector(selectedRow == 0, maximumValue = 8) { selectedElement ->
                            withHorizontalAlignment(0f).hflow(spacing = 32.px) {
                                button("CONFIRM", type = ButtonType.Primary, focused = selectedElement == 0)
                                button("CANCEL", type = ButtonType.Error, focused = selectedElement == 1)
                                button("DISABLED", type = ButtonType.Disabled, focused = selectedElement == 2)
                                component {
                                    val (count, setCount) = useState(0)
                                    button("ACTION $count", type = ButtonType.Default, focused = selectedElement == 3) {
                                        setCount(count + 1)
                                    }
                                }
                                button("ACTION", type = ButtonType.Default, icon = Image("images/search.png"), focused = selectedElement == 4)
                                iconButton(Image("images/search.png"), type = ButtonType.Primary, focused = selectedElement == 5)
                                iconButton(Image("images/search.png"), type = ButtonType.Error, focused = selectedElement == 6)
                                iconButton(Image("images/search.png"), type = ButtonType.Disabled, focused = selectedElement == 7)
                                iconButton(Image("images/search.png"), type = ButtonType.Default, focused = selectedElement == 8)
                            }
                        }
                    }

                    labelledSection("Text inputs") {
                        horizontalSelector(selectedRow == 1, maximumValue = 3) { selectedElement ->
                            withHorizontalAlignment(0f).hflow(spacing = 32.px) {
                                component {
                                    val focused = selectedElement == 0
                                    val textBuffer = TextBufferBuilder {
                                        if (focused) emitCursor(model.style[Style.PrimaryBackgroundColour])
                                        emitText("Placeholder...", model.style[Style.AlternateForegroundColour])
                                    }

                                    onDraw {
                                        withRegion(region.resizeTo(height = 48.px)) {
                                            textInput(textBuffer, model.style[Style.ElementBackgroundColour], model.style[Style.PrimaryBackgroundColour], model.style[Style.ShadowColour], focused)
                                        }
                                    }

                                    noChildrenDefineDefaultSize(192f, 48f)
                                }

                                component {
                                    val focused = selectedElement == 1
                                    val textBuffer = TextBufferBuilder(defaultColour = model.style[Style.ForegroundColour]) {
                                        emitText("I")
                                        if (focused) beginDecoration(TextBuffer.Decoration.Highlight, Colours.red.withAlpha(0.4f))
                                        emitText("nva")
                                        if (focused) stopDecoration(TextBuffer.Decoration.Highlight)
                                        if (focused) emitCursor(Colours.red)
                                        emitText("lid")
                                    }

                                    onDraw {
                                        withRegion(region.resizeTo(height = 48.px)) {
                                            textInput(textBuffer, model.style[Style.ElementBackgroundColour], model.style[Style.ErrorBackgroundColour], model.style[Style.ShadowColour], focused)
                                        }
                                    }

                                    noChildrenDefineDefaultSize(192f, 48f)
                                }

                                component {
                                    val focused = selectedElement == 2
                                    val textBuffer = TextBufferBuilder {
                                        emitText("Disabled", model.style[Style.ForegroundColour])
                                    }

                                    onDraw {
                                        withRegion(region.resizeTo(height = 48.px)) {
                                            textInput(textBuffer, model.style[Style.ElementBackgroundColour], model.style[Style.DisabledBackgroundColour], model.style[Style.ShadowColour], focused)
                                        }
                                    }

                                    noChildrenDefineDefaultSize(192f, 48f)
                                }

                                component {
                                    val focused = selectedElement == 3
                                    val textBuffer = TextBufferBuilder {
                                        emitText("Something", model.style[Style.ForegroundColour])
                                        if (focused) emitCursor(model.style[Style.PrimaryBackgroundColour])
                                    }

                                    onDraw {
                                        withRegion(region.resizeTo(height = 48.px)) {
                                            textInput(textBuffer, model.style[Style.ElementBackgroundColour], model.style[Style.PrimaryBackgroundColour], model.style[Style.ShadowColour], focused, icon = "images/search.png", iconColour = model.style[Style.ForegroundColour])
                                        }
                                    }

                                    noChildrenDefineDefaultSize(192f, 48f)
                                }
                            }
                        }
                    }

                    labelledSection("Sliders") {
                        horizontalSelector(selectedRow == 2, maximumValue = 0) { selectedElement ->
                            withHorizontalAlignment(0f).hflow(spacing = 32.px) {
                                component {
                                    val focused = selectedElement == 0

                                    onDraw {
                                        slider(0.3f, model.style[Style.ElementBackgroundColour], model.style[Style.PrimaryBackgroundColour].takeIf { focused }, model.style[Style.ShadowColour])
                                    }

                                    noChildrenDefineDefaultSize(192f, 24f)
                                }

                                component {
                                    onDraw {
                                        progress(0.3f, model.style[Style.ElementBackgroundColour], model.style[Style.PrimaryBackgroundColour], null, model.style[Style.ShadowColour])
                                    }

                                    noChildrenDefineDefaultSize(192f, 24f)
                                }

                                component {
                                    onDraw {
                                        progress(0.6f, model.style[Style.ElementBackgroundColour], model.style[Style.PrimaryBackgroundColour], Colours.white, model.style[Style.ShadowColour])
                                    }

                                    noChildrenDefineDefaultSize(192f, 24f)
                                }

                                component {
                                    onDraw {
                                        progress(0.2f, model.style[Style.ElementBackgroundColour], model.style[Style.PrimaryBackgroundColour], Colours.white, model.style[Style.ShadowColour])
                                    }

                                    noChildrenDefineDefaultSize(192f, 24f)
                                }
                            }
                        }
                    }

                    labelledSection("Dropdowns") {
                        horizontalSelector(selectedRow == 3, maximumValue = 1) { selectedElement ->
                            withHorizontalAlignment(0f).hflow(spacing = 32.px) {
                                component {
                                    val (num, setNum) = useState(initialValue = 3)

                                    withHeight(48.px).hflow(spacing = 16.px) {
                                        dropdown(
                                            initialSelectedOption = num,
                                            options = listOf(1, 2, 3, 4),
                                            focused = selectedElement == 0,
                                            onOptionChanged = setNum,
                                            renderOption = { option ->
                                                withPadding(8.px, 16.px).label("Option $option", horizontalAlignment = 0f)
                                            },
                                            renderPrimaryOption = { option ->
                                                withPadding(8.px, 16.px).label("Option $option", horizontalAlignment = 0f)
                                            }
                                        )

                                        button("You selected $num", focused = selectedElement == 1)
                                    }
                                }
                            }
                        }
                    }

                    labelledSection("Toggles") {
                        horizontalSelector(selectedRow == 4, maximumValue = 2) { selectedElement ->
                            withHorizontalAlignment(0f).hflow(spacing = 32.px) {
                                component {
                                    onDraw {
                                        val focused = selectedElement == 0
                                        val (button, _, label) = region.splitHorizontally(at1 = region.height.px, at2 = region.height.px + 8.px)
                                        val textBuffer = TextBufferBuilder("Radio button", model.style[Style.ForegroundColour])
                                        val (tbw, tbh) = withRegion(label.withPadding(4.px)) { graphics.textBufferSize(textBuffer) }
                                        val textBufferRegion = Region(label.x, 0f, tbw, tbh).alignWithin(region, horizontalAlignment = 0f)

                                        if (focused) {
                                            withRegion(listOf(button.withPadding(left = (-4).px), textBufferRegion.withPadding(right = (-8).px)).boundingRegion()) {
                                                shadow(model.style[Style.ShadowColour], cornerRadius = 8.px)
                                                roundedRectangle(8.px, model.style[Style.ElementBackgroundColour])
                                            }
                                        }

                                        withRegion(button.withPadding(4.px)) {
                                            shadow(model.style[Style.ShadowColour], cornerRadius = 50.percent)
                                            ellipse(model.style[Style.ElementBackgroundColour])
//                                            if (index == 2) region.withPadding(4.px).draw { ellipse(model.primaryColour) }
                                        }

                                        withRegion(label.withPadding(4.px)) {
                                            write(textBuffer, horizontalAlignment = 0f)
                                        }
                                    }

                                    noChildrenDefineDefaultSize(192f, 24f)
                                }

                                component {
                                    onDraw {
                                        val focused = selectedElement == 1
                                        val (button, _, label) = region.splitHorizontally(at1 = region.height.px, at2 = region.height.px + 8.px)
                                        val textBuffer = TextBufferBuilder("Checkbox", model.style[Style.ForegroundColour])
                                        val (tbw, tbh) = withRegion(label.withPadding(4.px)) { graphics.textBufferSize(textBuffer) }
                                        val textBufferRegion = Region(label.x, 0f, tbw, tbh).alignWithin(region, horizontalAlignment = 0f)

                                        if (focused) {
                                            withRegion(listOf(button.withPadding(left = (-4).px), textBufferRegion.withPadding(right = (-8).px)).boundingRegion()) {
                                                shadow(model.style[Style.ShadowColour], cornerRadius = 8.px)
                                                roundedRectangle(8.px, model.style[Style.ElementBackgroundColour])
                                            }
                                        }

                                        withRegion(button.withPadding(4.px)) {
                                            shadow(model.style[Style.ShadowColour], cornerRadius = 2.px)
                                            roundedRectangle(2.px, model.style[Style.ElementBackgroundColour])
//                                            if (index == 2) region.withPadding(4.px).draw { ellipse(model.primaryColour) }
                                        }

                                        withRegion(label.withPadding(4.px)) {
                                            write(textBuffer, horizontalAlignment = 0f)
                                        }
                                    }

                                    noChildrenDefineDefaultSize(192f, 24f)
                                }

                                component {
                                    onDraw {
                                        val r = region.resizeTo(width = 96.px, horizontalAlignment = 0f)
                                        val (left, right) = r.withPadding(horizontal = 4.px).splitHorizontally()

                                        withRegion(r) {
                                            shadow(cornerRadius = 50.percent, colour = model.style[Style.ShadowColour])
                                            roundedRectangle(50.percent, model.style[Style.ElementBackgroundColour])
                                        }

                                        withRegion(r.withPadding(4.px).resizeTo(width = 50.percent, horizontalAlignment = 0f)) {
                                            roundedRectangle(50.percent, model.style[Style.PrimaryBackgroundColour])
                                        }

                                        withRegion(left, clip = true) {
                                            withRegion(left.resizeTo(region.width.px + region.height.px, horizontalAlignment = 0f)) {
//                    roundedRectangle(50.percent, if (isOn) primaryColour else model.lighterBackgroundColour)
                                            }

                                            withRegion(region.withPadding(4.px)) { write("ON", colour = Colours.white, font = Font.default.copy(lineHeight = Font.default.lineHeight * 0.8f)) }
                                        }

                                        withRegion(right, clip = true) {
                                            withRegion(right.resizeTo(region.width.px + region.height.px, horizontalAlignment = 1f)) {
//                    roundedRectangle(50.percent, if (!isOn) primaryColour else model.lighterBackgroundColour)
                                            }

                                            withRegion(region.withPadding(4.px)) { write("OFF", colour = model.style[Style.ForegroundColour], font = Font.default.copy(lineHeight = Font.default.lineHeight * 0.8f)) }
                                        }
                                    }

                                    noChildrenDefineDefaultSize(192f, 24f)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    component.events.connect {
        window.draw { component.draw(this) }
    }

    window.events.connect(component::pushEvent)

    component.load()

    while (!window.isClosed) GLFWWindowCreator.update()
}
