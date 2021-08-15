package com.exerro.simpleui

fun main() {
    val window = GLFWWindowCreator.createWindow("Hello World")

    window.draw {
        fill(PaletteColour.Black())

        val colours = listOf(
            "Red" to PaletteColour.Red(),
            "Orange" to PaletteColour.Orange(),
            "Yellow" to PaletteColour.Yellow(),
            "Green" to PaletteColour.Green(),
            "Teal" to PaletteColour.Teal(),
            "Blue" to PaletteColour.Blue(),
            "Purple" to PaletteColour.Purple(),
            "Pink" to PaletteColour.Pink(),
            "Black" to PaletteColour.Black(),
            "Charcoal" to PaletteColour.Charcoal(),
            "Silver" to PaletteColour.Silver(),
            "White" to PaletteColour.White(),
        )

        val topRegions = region
            .resizeTo(height = 48.px, verticalAlignment = 0f)
            .withPadding(8.px)
            .partitionHorizontally(partitions = colours.size, spacing = 8.px)

        val belowRegions = region
            .withPadding(top = 48.px)
            .listVertically(208.px)
            .map { it.withPadding(8.px) }
            .flatMap { it.partitionHorizontally(partitions = colours.size / 4, spacing = 8.px) }

        topRegions.zip(colours).forEach { (r, c) ->
            r.draw {
                roundedRectangle(4.px, c.second, c.second.withVariant(PaletteVariant.Darker), 2.px)
                write(FormattedText.text(c.first.uppercase(), PaletteColour.Black()), Font.monospace)
            }
        }

        belowRegions.zip(colours).forEach { (r, c) ->
            r.draw {
                val (r0, r1, r2, r3, r4) = r.partitionVertically(5, 8.px)

                r0.draw { fill(c.second.withVariant(PaletteVariant.Dimmer)); write("dimmer") }
                r1.draw { fill(c.second.withVariant(PaletteVariant.Brighter)); write("brighter") }
                r2.draw { fill(c.second.withVariant(PaletteVariant.Darker)); write("darker") }
                r3.draw { fill(c.second); write(c.first.lowercase()) }
                r4.draw { fill(c.second.withVariant(PaletteVariant.Lighter)); write("lighter") }
            }
        }
    }

    window.events.connect(::println)

    while (!window.isClosed) {
        GLFWWindowCreator.update()
    }
}
