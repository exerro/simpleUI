package com.exerro.simpleui.experimental

import com.exerro.simpleui.colour.Colour
import com.exerro.simpleui.colour.Colours
import com.exerro.simpleui.colour.RGBA

/** A mapping from [PaletteColour]s to [Colour] values. */
fun interface Palette {
    /** Return the [Colour] value of a [PaletteColour]. */
    operator fun get(colour: PaletteColour): Colour

    /** A default palette with some nice colours. */
    object Default: Palette {
        override fun get(colour: PaletteColour) = when (colour) {
            is PaletteColour.Black -> when (colour.variant) {
                PaletteVariant.Dimmer -> RGBA(10, 11, 11)
                PaletteVariant.Brighter -> RGBA(0, 0, 0)
                PaletteVariant.Darker -> RGBA(2, 2, 2)
                PaletteVariant.Default -> RGBA(8, 8, 8)
                PaletteVariant.Lighter -> RGBA(14, 14, 14)
            }
            is PaletteColour.Charcoal -> when (colour.variant) {
                PaletteVariant.Dimmer -> RGBA(20, 20, 20)
                PaletteVariant.Brighter -> RGBA(28, 28, 32)
                PaletteVariant.Darker -> RGBA(18, 19, 19)
                PaletteVariant.Default -> RGBA(24, 25, 25)
                PaletteVariant.Lighter -> RGBA(30, 31, 31)
            }
            is PaletteColour.Silver -> when (colour.variant) {
                PaletteVariant.Dimmer -> RGBA(162, 162, 162)
                PaletteVariant.Brighter -> RGBA(200, 200, 200)
                PaletteVariant.Darker -> RGBA(150, 150, 150)
                PaletteVariant.Default -> RGBA(168, 168, 168)
                PaletteVariant.Lighter -> RGBA(186, 186, 186)
            }
            is PaletteColour.White -> when (colour.variant) {
                PaletteVariant.Dimmer -> RGBA(210, 210, 210)
                PaletteVariant.Brighter -> RGBA(255, 255, 255)
                PaletteVariant.Darker -> RGBA(225, 225, 225)
                PaletteVariant.Default -> RGBA(235, 235, 235)
                PaletteVariant.Lighter -> RGBA(242, 242, 242)
            }
            is PaletteColour.Blue -> Colours.blue
            is PaletteColour.Green -> Colours.green
            is PaletteColour.Orange -> Colours.orange
            is PaletteColour.Pink -> Colours.pink
            is PaletteColour.Purple -> Colours.purple
            is PaletteColour.Red -> Colours.red
            is PaletteColour.Teal -> Colours.teal
            is PaletteColour.Yellow -> Colours.yellow
        }
    }
}
