package com.exerro.simpleui

val lorem2 = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec non magna orci. Nullam varius lectus eros, nec porta justo pellentesque non. Mauris suscipit erat ut finibus bibendum. Sed maximus sollicitudin vulputate. Nam dictum luctus orci ac varius. In in varius erat, sed dictum justo. Quisque efficitur quis metus ac tincidunt. Nulla eu lacinia velit, nec elementum libero. Donec pulvinar mauris et nunc suscipit, congue fringilla nunc auctor. Donec eu velit dapibus, bibendum velit at, malesuada mi. Suspendisse potenti."

fun main() {
    val window = GLFWWindowCreator.createWindow("Shadows")

    window.draw {
        fill(PaletteColour.Charcoal())

        val (top, _, bottom) = region.withPadding(32.px).splitVertically(at1 = 50.percent - 16.px, at2 = 50.percent + 16.px)
        val textBoxes = listOf(top.listHorizontally(384.px, spacing = 32.px), bottom.listHorizontally(384.px, spacing = 32.px)).flatten()
        val textBoxContent = textBoxes.map { it.withPadding(16.px) }

        textBoxes.take(10).draw {
            shadow()
            fill(PaletteColour.Charcoal(PaletteVariant.Darker))
        }

        textBoxContent[0].draw { write(
            horizontalAlignment = 0f, verticalAlignment = 0f,
            highlightAlpha = 0.4f, underlineAlpha = 1f,
            wrap = false
        ) {
            text("Hello world!", PaletteColour.White())
            lineBreak()
            text("Line two", blue)
            lineBreak()
            beginHighlighting(green)
            text("Line three", yellow)
            stopHighlighting()
            lineBreak()
            beginUnderlining(purple)
            text("Line four", orange)
            stopUnderlining()
            lineBreak()
            text("Line five", red)
            lineBreak()
            beginHighlighting(PaletteColour.Silver())
            beginUnderlining(PaletteColour.Silver())
            text("I am underlined and highlighted", PaletteColour.White())
            stopHighlighting()
            stopUnderlining()
        } }

        textBoxContent[1].draw { write(
            font = Font.monospace,
            highlightAlpha = 1f,
            horizontalAlignment = 0f, verticalAlignment = 0f
        ) {
            val white = PaletteColour.White()
            val silver = PaletteColour.Silver()
            val charcoal = PaletteColour.Charcoal()

            text("effect", purple)
            whitespace()
            text("Yield", teal)
            text("<", white)
            text("'a", blue)
            text(">", white)
            whitespace()
            text("{", white)
            lineBreak(1)
            beginHighlighting(charcoal)
            text("yield", teal)
            text("(", white)
            beginHighlighting(charcoal.withVariant(PaletteVariant.Lighter))
            text("value", red)
            stopHighlighting()
            text(":", white)
            whitespace()
            text("'a", blue)
            text(")", white)
            text(":", white)
            whitespace()
            beginUnderlining(red)
            text("Unit", blue)
            stopUnderlining()
            stopHighlighting()
            lineBreak(-1)
            text("}", white)
        } }

        textBoxContent[2].draw { write(
            horizontalAlignment = 0f, verticalAlignment = 0f,
        ) {
            text(lorem2, PaletteColour.White())
        } }

        textBoxContent[3].draw { write {
            text(lorem2, PaletteColour.White())
        } }

        textBoxContent[4].draw { write {
            beginHighlighting(PaletteColour.Blue())
            text(lorem2, PaletteColour.White())
        } }

        textBoxContent[5].draw { write {
            beginUnderlining(PaletteColour.Blue())
            text(lorem2, PaletteColour.White())
        } }
    }

    while (!window.isClosed) GLFWWindowCreator.update()
}
