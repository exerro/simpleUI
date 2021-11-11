package com.exerro.simpleui

import com.exerro.simpleui.colour.Colours

private const val lorem = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec non magna orci. Nullam varius lectus eros, nec porta justo pellentesque non. Mauris suscipit erat ut finibus bibendum. Sed maximus sollicitudin vulputate. Nam dictum luctus orci ac varius. In in varius erat, sed dictum justo. Quisque efficitur quis metus ac tincidunt. Nulla eu lacinia velit, nec elementum libero. Donec pulvinar mauris et nunc suscipit, congue fringilla nunc auctor. Donec eu velit dapibus, bibendum velit at, malesuada mi. Suspendisse potenti."

fun main() {
    val window = GLFWWindowCreator.createWindow("Text")
    val palette = Palette.Default
    val white = palette[PaletteColour.White()]
    val silver = palette[PaletteColour.Silver()]
    val charcoal = palette[PaletteColour.Charcoal()]
    val red = palette[PaletteColour.Red()]
    val orange = palette[PaletteColour.Orange()]
    val yellow = palette[PaletteColour.Yellow()]
    val green = palette[PaletteColour.Green()]
    val teal = palette[PaletteColour.Teal()]
    val blue = palette[PaletteColour.Blue()]
    val purple = palette[PaletteColour.Purple()]
    val pink = palette[PaletteColour.Pink()]

    window.draw {
        fill(charcoal)

        val (top, _, bottom) = region.withPadding(32.px).splitVertically(at1 = 50.percent - 16.px, at2 = 50.percent + 16.px)
        val textBoxes = listOf(top.listHorizontally(384.px, spacing = 32.px), bottom.listHorizontally(384.px, spacing = 32.px))
            .flatten()
            .map { it.rounded() }
        val textBoxContentAreas = textBoxes.map { it.withPadding(16.px) }

        val textBuffer0 = TextBufferBuilder {
            emitText("Hello world!", white)
            emitLineBreak()
            emitText("Line two", blue)
            emitLineBreak()
            beginDecoration(TextBuffer.Decoration.Highlight, green)
            emitText("Line three", yellow)
            stopDecoration(TextBuffer.Decoration.Highlight)
            emitLineBreak()
            beginDecoration(TextBuffer.Decoration.Underline, purple)
            emitText("Line four", orange)
            stopDecoration(TextBuffer.Decoration.Underline)
            emitLineBreak()
            emitText("Line five", red)
            emitLineBreak()
            beginDecoration(TextBuffer.Decoration.Highlight, silver.withAlpha(0.4f))
            beginDecoration(TextBuffer.Decoration.Underline, silver)
            emitText("I am underlined and highlighted", white)
            stopDecoration(TextBuffer.Decoration.Highlight)
            stopDecoration(TextBuffer.Decoration.Underline)
            emitLineBreak()
            beginDecoration(TextBuffer.Decoration.Strikethrough, white)
            emitText("I am struck through", white)
            stopDecoration(TextBuffer.Decoration.Strikethrough)
        }

        val textBuffer1 = TextBufferBuilder {
            emitText("effect", purple)
            emitWhitespace()
            emitText("Yield", teal)
            emitText("<", white)
            emitText("'a", blue)
            emitText(">", white)
            emitWhitespace()
            emitText("{", white)
            emitLineBreak(1)
            beginDecoration(TextBuffer.Decoration.Highlight, white.withAlpha(0.1f))
            emitText("yield", teal)
            emitText("(", white)
            beginDecoration(TextBuffer.Decoration.Highlight, teal.withAlpha(0.4f))
            emitText("value", pink)
            stopDecoration(TextBuffer.Decoration.Highlight)
            emitText(":", white)
            emitWhitespace()
            emitText("'a", blue)
            emitText(")", white)
            emitText(":", white)
            emitWhitespace()
            beginDecoration(TextBuffer.Decoration.Underline, red)
            emitText("Unit", blue)
            stopDecoration(TextBuffer.Decoration.Underline)
            stopDecoration(TextBuffer.Decoration.Highlight)
            emitLineBreak(-1)
            emitText("}", white)
        }

        val textBuffer2 = TextBufferBuilder(lorem, white, splitSegments = true)

        val textBuffer4 = TextBufferBuilder {
            beginDecoration(TextBuffer.Decoration.Highlight, Colours.blue.withAlpha(0.4f))
            emitTextSegments(lorem, white)
        }

        val textBuffer5 = TextBufferBuilder {
            beginDecoration(TextBuffer.Decoration.Underline, Colours.blue)
            emitTextSegments(lorem, white)
        }

        val textBuffer6 = TextBufferBuilder {
            beginDecoration(TextBuffer.Decoration.Highlight, Colours.red)
            emitText("Short")
            stopDecoration(TextBuffer.Decoration.Highlight)
            emitWhitespace()
            beginDecoration(TextBuffer.Decoration.Highlight, Colours.red)
            emitText("text")
            stopDecoration(TextBuffer.Decoration.Highlight)
        }

        textBoxes.take(10).draw {
            shadow()
            fill(palette[PaletteColour.Charcoal(PaletteVariant.Darker)])
        }

        textBoxContentAreas[0].draw { write(
            buffer = textBuffer0,
            horizontalAlignment = 0f,
            verticalAlignment = 0f
        ) }

        textBoxContentAreas[1].draw { write(
            buffer = textBuffer1,
            font = Font.monospace,
            horizontalAlignment = 0f,
            verticalAlignment = 0f
        ) }

        textBoxContentAreas[2].draw { write(
            buffer = wordWrap(textBuffer2),
            horizontalAlignment = 0f,
            verticalAlignment = 0f,
        )}

        textBoxContentAreas[3].draw { write(
            buffer = wordWrap(textBuffer2),
        )}

        textBoxContentAreas[4].draw { write(
            buffer = wordWrap(textBuffer4),
        )}

        textBoxContentAreas[5].draw { write(
            buffer = wordWrap(textBuffer5),
        )}

        textBoxContentAreas[6].draw {
            val r = textBufferBounds(textBuffer6)

            write(textBuffer6)

            region.above(r).draw {
                write("I am above", silver, verticalAlignment = 1f)
            }

            region.below(r).draw {
                write("I am below", silver, verticalAlignment = 0f)
            }

            region.toLeftOf(r).draw {
                write("I am to the left", silver, horizontalAlignment = 1f)
            }

            region.toRightOf(r).draw {
                write("I am to the right", silver, horizontalAlignment = 0f)
            }
        }
    }

    while (!window.isClosed) GLFWWindowCreator.update()
}
