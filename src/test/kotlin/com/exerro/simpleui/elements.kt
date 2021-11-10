 package com.exerro.simpleui

import com.exerro.simpleui.colour.Colour
import com.exerro.simpleui.colour.Colours
import com.exerro.simpleui.extensions.text
import com.exerro.simpleui.extensions.whitespace
import kotlin.math.max

private const val lorem = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec non magna orci. Nullam varius lectus eros, nec porta justo pellentesque non. Mauris suscipit erat ut finibus bibendum. Sed maximus sollicitudin vulputate. Nam dictum luctus orci ac varius. In in varius erat, sed dictum justo. Quisque efficitur quis metus ac tincidunt. Nulla eu lacinia velit, nec elementum libero. Donec pulvinar mauris et nunc suscipit, congue fringilla nunc auctor. Donec eu velit dapibus, bibendum velit at, malesuada mi. Suspendisse potenti."

data class Theme(
    val backgroundColour: Colour,
    val lighterBackgroundColour: Colour,
    val darkerBackgroundColour: Colour,
    val textColour: Colour,
    val shadowColour: Colour,
    val disabledColour: Colour,
) {
    companion object {
        val light = Theme(
            backgroundColour = Palette.Default[PaletteColour.White()],
            lighterBackgroundColour = Palette.Default[PaletteColour.White(PaletteVariant.Lighter)],
            darkerBackgroundColour = Palette.Default[PaletteColour.White(PaletteVariant.Darker)],
            textColour = Palette.Default[PaletteColour.Black()],
            shadowColour = Palette.Default[PaletteColour.Silver()],
            disabledColour = Palette.Default[PaletteColour.Silver()],
        )

        val dark = Theme(
            backgroundColour = Palette.Default[PaletteColour.Charcoal()],
            lighterBackgroundColour = Palette.Default[PaletteColour.Charcoal(PaletteVariant.Lighter)],
            darkerBackgroundColour = Palette.Default[PaletteColour.Charcoal(PaletteVariant.Darker)],
            textColour = Palette.Default[PaletteColour.White()],
            shadowColour = Palette.Default[PaletteColour.Black(PaletteVariant.Lighter)],
            disabledColour = Palette.Default[PaletteColour.Silver(PaletteVariant.Darker)],
        )
    }
}

fun main() {
    val window = GLFWWindowCreator.createWindow("Elements")
    var theme = Theme.dark
    val palette = Palette.Default
    val colours = Colours.all - Colours.greyscale
    var primaryColour: Colour = Colours.teal
    var isSideVisible = false
    var sideIndexSelected = 0

    fun draw() = window.draw {
        val rows = region.withPadding(16.px).listVertically(64.px, spacing = 16.px)
        val right = region.resizeTo(width = 640.px, horizontalAlignment = 1f).withPadding(horizontal = 64.px, vertical = 92.px)
        val radioArea = rows[4, 7].boundingRegion()
        val textArea = rows[7, 11].boundingRegion()

        val (b0, b1, b2, b3, b4, b5, b6, b7) = rows[0].listHorizontally(192.px, spacing = 32.px)
        val (i0, i1, i2, i3, i4) = rows[1].resizeTo(height = 48.px).listHorizontally(192.px, spacing = 32.px)
        val (slider0, slider1, progress0, progress1, dropdown0, dropdown1) = rows[2].resizeTo(height = 32.px).listHorizontally(192.px, spacing = 32.px)
        val (ib0, ib1, ib2, ib3, ib4) = rows[3].listHorizontally(192.px, spacing = 32.px)
        val (t0, t1, t2, t3) = textArea.listHorizontally(256.px, spacing = 32.px)
        val (radioButtons, checkBoxes, toggles, image) = radioArea.listHorizontally(256.px, spacing = 16.px)

        fill(theme.backgroundColour)

        b0.resizeTo(height = 32.px).draw {
            shadow(cornerRadius = 6.px, colour = theme.shadowColour)
            roundedRectangle(cornerRadius = 6.px, colour = primaryColour)
            write("CONFIRM", palette[PaletteColour.White()])
        }

        b1.resizeTo(height = 32.px).draw {
            shadow(cornerRadius = 6.px, colour = theme.shadowColour)
            roundedRectangle(cornerRadius = 6.px, colour = palette[PaletteColour.Red()])
            write("CANCEL", palette[PaletteColour.White()])
        }

        b2.resizeTo(height = 32.px).draw {
            shadow(cornerRadius = 6.px, colour = theme.shadowColour)
            roundedRectangle(cornerRadius = 6.px, colour = theme.disabledColour)
            write("DISABLED", palette[PaletteColour.White()])
        }

        b3.resizeTo(height = 32.px).draw {
            shadow(cornerRadius = 6.px, colour = theme.shadowColour)
            roundedRectangle(cornerRadius = 6.px, colour = theme.lighterBackgroundColour)
            write("ACTION", colour = theme.textColour)
        }

        b4.resizeTo(height = 32.px).draw {
            shadow(cornerRadius = 6.px, colour = theme.shadowColour)
            roundedRectangle(cornerRadius = 6.px, colour = theme.lighterBackgroundColour)
            write("ACTION", colour = theme.textColour)
            region.resizeTo(height = 2.px, width = 80.percent, verticalAlignment = 1f).draw(clip = true) {
                region.resizeTo(height = 32.px, verticalAlignment = 0f).draw {
                    roundedRectangle(cornerRadius = 6.px, colour = primaryColour)
                }
            }
        }

        b5.resizeTo(height = 32.px).draw {
            shadow(cornerRadius = 6.px, colour = theme.shadowColour)
            roundedRectangle(cornerRadius = 6.px, colour = primaryColour)
            write("ACTION", colour = palette[PaletteColour.White()])
            region.resizeTo(height = 2.px, width = 80.percent, verticalAlignment = 1f).draw(clip = true) {
                region.resizeTo(height = 32.px, verticalAlignment = 0f).draw {
                    roundedRectangle(cornerRadius = 6.px, colour = palette[PaletteColour.White()])
                }
            }
        }

        ib0.resizeTo(height = 32.px).draw {
            val icon = region.resizeTo(width = region.height.px, horizontalAlignment = 0f)
            shadow(cornerRadius = 6.px, colour = theme.shadowColour)
            roundedRectangle(cornerRadius = 6.px, colour = theme.lighterBackgroundColour)
            icon.withPadding(6.px).draw { image("images/search.png") }
            write("SEARCH", colour = theme.textColour)
        }

        ib1.resizeTo(height = 32.px).draw {
            val icon = region.resizeTo(width = region.height.px, horizontalAlignment = 0f)
            shadow(cornerRadius = 6.px, colour = theme.shadowColour)
            roundedRectangle(cornerRadius = 6.px, colour = theme.lighterBackgroundColour)
            icon.withPadding(6.px).draw { image("images/search.png") }
            write("SEARCH", colour = theme.textColour)
            region.resizeTo(height = 2.px, width = 80.percent, verticalAlignment = 1f).draw(clip = true) {
                region.resizeTo(height = 32.px, verticalAlignment = 0f).draw {
                    roundedRectangle(cornerRadius = 6.px, colour = primaryColour)
                }
            }
        }

        ib2.resizeTo(height = 48.px).draw {
            region.resizeTo(width = 48.px, horizontalAlignment = 0.2f).draw {
                shadow(cornerRadius = 50.percent, colour = theme.shadowColour)
                roundedRectangle(cornerRadius = 50.percent, colour = theme.lighterBackgroundColour)
                region.withPadding(12.px).draw { image("images/search.png") }
            }

            region.resizeTo(width = 48.px, horizontalAlignment = 0.8f).draw {
                val thisRegion = region
                shadow(cornerRadius = 50.percent, colour = theme.shadowColour)
                roundedRectangle(cornerRadius = 50.percent, colour = theme.lighterBackgroundColour)
                region.resizeTo(height = 5.px, width = 100.percent, verticalAlignment = 1f).draw(clip = true) {
                    thisRegion.withPadding(1.px).draw {
                        roundedRectangle(cornerRadius = 50.percent, colour = theme.lighterBackgroundColour, borderColour = primaryColour, borderWidth = 2.px)
                    }
                }
                region.withPadding(12.px).draw { image("images/search.png", tint = theme.textColour) }
            }
        }

        t0.draw {
            shadow(colour = theme.shadowColour)
            fill(theme.lighterBackgroundColour)
            region.withPadding(8.px).draw(clip = true) {
                write(lorem, wrap = true, colour = theme.textColour, horizontalAlignment = 0f, verticalAlignment = 0f)
            }
        }

        listOf(t1, t2).boundingRegion().draw {
            shadow(colour = theme.shadowColour)
            fill(theme.lighterBackgroundColour)
            region.withPadding(16.px).draw {
                write(font = Font.monospace, horizontalAlignment = 0f, verticalAlignment = 0f) {
                    val red = palette[PaletteColour.Red()]
                    val orange = palette[PaletteColour.Orange()]
                    val teal = palette[PaletteColour.Teal()]
                    val blue = palette[PaletteColour.Blue()]
                    val purple = palette[PaletteColour.Purple()]

                    // Lua stuff
                    text("while", purple)
                    whitespace()
                    text("true", orange)
                    whitespace()
                    text("do", purple)
                    lineBreak(1)
                    text("print", teal)
                    text("(", theme.textColour)
                    text("\"Hello world!\"", orange)
                    text(")", theme.textColour)
                    lineBreak(-1)
                    text("end", purple)

                    lineBreak()
                    lineBreak()

                    // SL stuff
                    text("effect", purple)
                    whitespace()
                    text("Yield", teal)
                    text("<", theme.textColour)
                    text("'a", blue)
                    text(">", theme.textColour)
                    whitespace()
                    text("{", theme.textColour)
                    lineBreak(1)
                    text("yield", teal)
                    text("(", theme.textColour)
                    text("value", red)
                    text(":", theme.textColour)
                    whitespace()
                    text("'a", blue)
                    text(")", theme.textColour)
                    text(":", theme.textColour)
                    whitespace()
                    text("Unit", blue)
                    lineBreak(-1)
                    text("}", theme.textColour)
                }
            }
        }

        t3.draw {
            shadow(colour = theme.shadowColour)
            fill(theme.lighterBackgroundColour)

            region.listVertically(48.px).map { it.withPadding(vertical = 8.px, horizontal = 24.px) }.take(5).draw { index ->
                write("Setting $index", colour = theme.textColour, horizontalAlignment = 0f)
                write("'val #${index * 4}'", colour = palette[PaletteColour.Silver()], horizontalAlignment = 1f)
            }
        }

        radioButtons.listVertically(24.px, spacing = 8.px)[0].withPadding(left = 4.px).draw {
            write("Radio buttons", horizontalAlignment = 0f, colour = palette[PaletteColour.Silver()])
        }

        radioButtons.listVertically(24.px, spacing = 8.px).drop(1).take(6).draw { index ->
            val (button, _, label) = region.splitHorizontally(at1 = region.height.px, at2 = region.height.px + 8.px)

            button.withPadding(4.px).draw {
                shadow(theme.shadowColour, cornerRadius = 50.percent)
                ellipse(theme.lighterBackgroundColour)
                if (index == 2) region.withPadding(4.px).draw { ellipse(primaryColour) }
            }

            label.withPadding(4.px).draw {
                write("Radio button $index", horizontalAlignment = 0f, colour = theme.textColour)
            }
        }

        checkBoxes.listVertically(24.px, spacing = 8.px)[0].withPadding(left = 4.px).draw {
            write("Checkboxes", horizontalAlignment = 0f, colour = palette[PaletteColour.Silver()])
        }

        checkBoxes.listVertically(24.px, spacing = 8.px).drop(1).take(6).draw { index ->
            val (button, _, label) = region.splitHorizontally(at1 = region.height.px, at2 = region.height.px + 8.px)

            button.withPadding(4.px).draw {
                shadow(theme.shadowColour, cornerRadius = 2.px)
                roundedRectangle(2.px, theme.lighterBackgroundColour)
                if (index % 2 == 1) region.withPadding(4.px).draw { fill(primaryColour) }
            }

            label.withPadding(4.px).draw {
                write("Checkbox $index", horizontalAlignment = 0f, colour = theme.textColour)
            }
        }

        toggles.listVertically(24.px, spacing = 8.px)[0].withPadding(left = 4.px).draw {
            write("Toggles", horizontalAlignment = 0f, colour = palette[PaletteColour.Silver()])
        }

        toggles.listVertically(24.px, spacing = 8.px).drop(1).take(6).draw { index ->
            val r = region.resizeTo(width = 96.px, horizontalAlignment = 0f)
            val (left, right) = r.withPadding(horizontal = 4.px).splitHorizontally()
            val isOn = index % 3 == 1
            val drawAnything = index % 2 == 1

            if (drawAnything) r.draw {
                shadow(cornerRadius = 50.percent, colour = theme.shadowColour)
                roundedRectangle(50.percent, theme.lighterBackgroundColour)
            }

            if (drawAnything) r.withPadding(4.px).resizeTo(width = 50.percent, horizontalAlignment = if (isOn) 0f else 1f).draw {
                roundedRectangle(50.percent, primaryColour)
            }

            if (drawAnything) left.draw(clip = true) {
                left.resizeTo(region.width.px + region.height.px, horizontalAlignment = 0f).draw {
//                    roundedRectangle(50.percent, if (isOn) primaryColour else theme.lighterBackgroundColour)
                }

                region.withPadding(4.px).draw { write("ON", colour = if (isOn) palette[PaletteColour.White()] else theme.textColour, font = Font.default.copy(lineHeight = Font.default.lineHeight * 0.8f)) }
            }

            if (drawAnything) right.draw(clip = true) {
                right.resizeTo(region.width.px + region.height.px, horizontalAlignment = 1f).draw {
//                    roundedRectangle(50.percent, if (!isOn) primaryColour else theme.lighterBackgroundColour)
                }

                region.withPadding(4.px).draw { write("OFF", colour = if (!isOn) palette[PaletteColour.White()] else theme.textColour, font = Font.default.copy(lineHeight = Font.default.lineHeight * 0.8f)) }
            }
        }

        i0.draw {
            shadow(colour = theme.shadowColour, cornerRadius = 4.px)
            roundedRectangle(4.px, theme.backgroundColour)

            region.withPadding(horizontal = 16.px).draw {
                write("Placeholder...", colour = palette[PaletteColour.Silver()], horizontalAlignment = 0f)
            }
        }

        i1.draw {
            shadow(colour = theme.shadowColour, cornerRadius = 4.px)
            roundedRectangle(4.px, theme.backgroundColour)

            region.withPadding(horizontal = 16.px).draw {
                write("Something", colour = theme.textColour, horizontalAlignment = 0f)
            }
        }

        i2.draw {
            shadow(colour = theme.shadowColour, cornerRadius = 4.px)
            roundedRectangle(4.px, theme.lighterBackgroundColour)

            region.withPadding(horizontal = 16.px).draw {
                write("|Placeholder...", colour = palette[PaletteColour.Silver()], horizontalAlignment = 0f)
            }

            region.resizeTo(height = 2.px, width = 100.percent - 16.px, verticalAlignment = 1f).draw(clip = true) {
                region.resizeTo(height = 32.px, verticalAlignment = 0f).draw {
                    roundedRectangle(cornerRadius = 6.px, colour = primaryColour)
                }
            }
        }

        i3.draw {
            shadow(colour = theme.shadowColour, cornerRadius = 4.px)
            roundedRectangle(4.px, theme.lighterBackgroundColour)

            region.withPadding(horizontal = 16.px).draw {
                write("Something|", colour = theme.textColour, horizontalAlignment = 0f)
            }

            region.resizeTo(height = 2.px, width = 100.percent - 16.px, verticalAlignment = 1f).draw(clip = true) {
                region.resizeTo(height = 32.px, verticalAlignment = 0f).draw {
                    roundedRectangle(cornerRadius = 6.px, colour = primaryColour)
                }
            }
        }

        i4.draw {
            shadow(colour = theme.shadowColour, cornerRadius = 4.px)
            roundedRectangle(4.px, theme.lighterBackgroundColour)

            region.withPadding(horizontal = 16.px).draw {
                write("Invalid|", colour = theme.textColour, horizontalAlignment = 0f)
            }

            region.resizeTo(height = 2.px, width = 100.percent - 16.px, verticalAlignment = 1f).draw(clip = true) {
                region.resizeTo(height = 32.px, verticalAlignment = 0f).draw {
                    roundedRectangle(cornerRadius = 6.px, colour = palette[PaletteColour.Red()])
                }
            }
        }

        slider0.draw {
            region.resizeTo(height = 6.px).draw {
                shadow(cornerRadius = 50.percent, colour = theme.shadowColour)
                roundedRectangle(cornerRadius = 50.percent, colour = theme.lighterBackgroundColour)
            }

            region.resizeTo(height = 16.px).withAspectRatio(1f, horizontalAlignment = 0.3f).draw {
                shadow(cornerRadius = 50.percent, colour = theme.shadowColour)
                ellipse(colour = theme.lighterBackgroundColour)
            }
        }

        slider1.draw {
            region.resizeTo(height = 6.px).draw {
                shadow(cornerRadius = 50.percent, colour = theme.shadowColour)
                roundedRectangle(cornerRadius = 50.percent, colour = theme.lighterBackgroundColour)
            }

            region.resizeTo(height = 16.px).withAspectRatio(1f, horizontalAlignment = 0.3f).draw {
                shadow(cornerRadius = 50.percent, colour = theme.shadowColour)
                ellipse(colour = primaryColour)
            }
        }

        progress0.resizeTo(height = 24.px).draw {
            shadow(cornerRadius = 50.percent, colour = theme.shadowColour)
            roundedRectangle(cornerRadius = 50.percent, colour = theme.lighterBackgroundColour)

            region.resizeTo(width = 40.percent, horizontalAlignment = 0f).draw(clip = true) {
                region.resizeTo(width = region.width.px + region.height.px, horizontalAlignment = 0f).draw {
                    roundedRectangle(cornerRadius = 50.percent, colour = primaryColour)
                }
            }
        }

        progress1.resizeTo(height = 24.px).draw {
            shadow(cornerRadius = 50.percent, colour = theme.shadowColour)
            roundedRectangle(cornerRadius = 50.percent, colour = theme.lighterBackgroundColour)

            region.resizeTo(width = 60.percent, horizontalAlignment = 0f).draw(clip = true) {
                region.resizeTo(width = region.width.px + region.height.px, horizontalAlignment = 0f).draw {
                    roundedRectangle(cornerRadius = 50.percent, colour = primaryColour)
                }

                write("60%", palette[PaletteColour.White()])
            }
        }

        dropdown0.resizeTo(height = 32.px).draw {
            shadow(cornerRadius = 50.percent, colour = theme.shadowColour)
            roundedRectangle(cornerRadius = 50.percent, colour = theme.backgroundColour)

            region.withPadding(left = 16.px).draw {
                write("Option 1", colour = theme.textColour, horizontalAlignment = 0f)
            }

            region.resizeTo(width = region.height.px * 1.5f, horizontalAlignment = 1f).draw(clip = true) {
                region.resizeTo(width = region.width.px + region.height.px, horizontalAlignment = 1f).draw {
                    roundedRectangle(cornerRadius = 50.percent, colour = theme.lighterBackgroundColour)
                }

                region.resizeTo(height = 50.percent).withAspectRatio(1f).draw {
                    image("images/expand.png")
                }
            }
        }

        dropdown1.resizeTo(height = 32.px).draw {
            shadow(cornerRadius = 50.percent, colour = theme.shadowColour)
            roundedRectangle(cornerRadius = 50.percent, colour = theme.lighterBackgroundColour)

            region.withPadding(left = 16.px).draw {
                write("Option 1", colour = theme.textColour, horizontalAlignment = 0f)
            }

            region.resizeTo(width = region.height.px * 1.5f, horizontalAlignment = 1f).draw(clip = true) {
                region.resizeTo(width = region.width.px + region.height.px, horizontalAlignment = 1f).draw {
                    roundedRectangle(cornerRadius = 50.percent, colour = primaryColour)
                }

                region.resizeTo(height = 50.percent).withAspectRatio(1f).draw {
                    image("images/expand.png")
                }
            }
        }


        if (isSideVisible) right.draw {
            shadow(colour = theme.shadowColour)
            fill(theme.backgroundColour)

            val (rightHeader, rightContent) = right.splitVertically(at = 64.px)

            region.draw(clip = true) {
                rightHeader.draw {
                    fill(theme.darkerBackgroundColour)
                    region.withPadding(vertical = 16.px, horizontal = 32.px).draw {
                        write("Header text", horizontalAlignment = 0f, colour = theme.textColour, font = Font.heading)
                    }
                }

                rightContent.listVertically(48.px).take(10).draw { index ->
                    val isSelected = index == sideIndexSelected
                    val bgColour =
                        if (isSelected) theme.lighterBackgroundColour else theme.backgroundColour

                    fill(bgColour)

                    if (isSelected) region.resizeTo(width = 48.px, horizontalAlignment = 0f).draw {
                        write(">", colour = theme.textColour)
                    }

                    region.withPadding(left = 48.px).draw {
                        write("An item ${index + 1} in a list", horizontalAlignment = 0f, colour = theme.textColour)
                    }
                }
            }
        }
    }

    val c = window.events
        .filterIsInstance<EKeyPressed>()
        .filter { !it.isRepeat }
        .filter { it.name == "t" }
        .connect {
            theme = if (theme === Theme.light) Theme.dark else Theme.light
            draw()
        }

    window.events
        .filterIsInstance<EKeyPressed>()
        .filter { !it.isRepeat }
        .filter { it.name == "p" }
        .connect {
            primaryColour = colours[(colours.indexOf(primaryColour) + 1) % colours.size]
            draw()
        }

    window.events
        .filterIsInstance<EKeyPressed>()
        .filter { !it.isRepeat }
        .filter { it.name == "s" }
        .connect {
            isSideVisible = !isSideVisible
            draw()
        }

    window.events
        .filterIsInstance<EKeyPressed>()
        .filter { it.name == "up" || it.name == "down" }
        .connect {
            sideIndexSelected = max(0, sideIndexSelected + if (it.name == "up") -1 else 1)
            draw()
        }

    window.events
        .filterIsInstance<EKeyPressed>()
        .filter { it.name == "w" && KeyModifier.Control in it.modifiers }
        .connect { window.close() }

    draw()

    while (!window.isClosed) GLFWWindowCreator.update()
}
