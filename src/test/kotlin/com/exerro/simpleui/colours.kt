package com.exerro.simpleui

import com.exerro.simpleui.experimental.Palette
import com.exerro.simpleui.experimental.PaletteColour
import com.exerro.simpleui.experimental.PaletteVariant

fun main() {
    val window = GLFWWindowCreator.createWindow("Colours")
    val palette = Palette.Default

    window.draw {
        fill(palette[PaletteColour.Black()])

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
            withRegion(r) {
                roundedRectangle(4.px, palette[c.second], palette[c.second.withVariant(PaletteVariant.Darker)], 2.px)
                write(c.first.uppercase(), palette[PaletteColour.Black()], font = Font.monospace)
            }
        }

        belowRegions.zip(colours).forEach { (r, c) ->
            withRegion(r) {
                val (r0, r1, r2, r3, r4) = r.partitionVertically(5, 8.px)

                withRegion(r0) { fill(palette[c.second.withVariant(PaletteVariant.Dimmer)]); write("dimmer", palette[PaletteColour.White()]) }
                withRegion(r1) { fill(palette[c.second.withVariant(PaletteVariant.Brighter)]); write("brighter", palette[PaletteColour.White()]) }
                withRegion(r2) { fill(palette[c.second.withVariant(PaletteVariant.Darker)]); write("darker", palette[PaletteColour.White()]) }
                withRegion(r3) { fill(palette[c.second]); write(c.first.lowercase(), palette[PaletteColour.White()]) }
                withRegion(r4) { fill(palette[c.second.withVariant(PaletteVariant.Lighter)]); write("lighter", palette[PaletteColour.White()]) }
            }
        }
    }

    window.events.connect(::println)

    while (!window.isClosed) {
        GLFWWindowCreator.update()
    }
}
