package com.exerro.simpleui

private const val lorem = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec non magna orci. Nullam varius lectus eros, nec porta justo pellentesque non. Mauris suscipit erat ut finibus bibendum. Sed maximus sollicitudin vulputate. Nam dictum luctus orci ac varius. In in varius erat, sed dictum justo. Quisque efficitur quis metus ac tincidunt. Nulla eu lacinia velit, nec elementum libero. Donec pulvinar mauris et nunc suscipit, congue fringilla nunc auctor. Donec eu velit dapibus, bibendum velit at, malesuada mi. Suspendisse potenti."

fun main() {
    val window = GLFWWindowCreator.createWindow("Shadows")
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
        val textBoxes = listOf(top.listHorizontally(384.px, spacing = 32.px), bottom.listHorizontally(384.px, spacing = 32.px)).flatten()
        val textBoxContent = textBoxes.map { it.withPadding(16.px) }

        textBoxes.take(10).draw {
            shadow()
            fill(palette[PaletteColour.Charcoal(PaletteVariant.Darker)])
        }

        textBoxContent[0].draw { write(
            horizontalAlignment = 0f, verticalAlignment = 0f,
            wrap = false
        ) {
            text("Hello world!", white)
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
            beginHighlighting(silver.withAlpha(0.4f))
            beginUnderlining(silver)
            text("I am underlined and highlighted", white)
            stopHighlighting()
            stopUnderlining()
        } }

        textBoxContent[1].draw { write(
            font = Font.monospace,
            horizontalAlignment = 0f, verticalAlignment = 0f
        ) {

            text("effect", purple)
            whitespace()
            text("Yield", teal)
            text("<", white)
            text("'a", blue)
            text(">", white)
            whitespace()
            text("{", white)
            lineBreak(1)
            beginHighlighting(white.withAlpha(0.1f))
            text("yield", teal)
            text("(", white)
            beginHighlighting(teal.withAlpha(0.4f))
            text("value", pink)
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
            text(lorem, white)
        } }

        textBoxContent[3].draw { write {
            text(lorem, white)
        } }

        textBoxContent[4].draw { write {
            beginHighlighting(blue)
            text(lorem, white)
        } }

        textBoxContent[5].draw { write {
            beginUnderlining(blue)
            text(lorem, white)
        } }
    }

    while (!window.isClosed) GLFWWindowCreator.update()
}
