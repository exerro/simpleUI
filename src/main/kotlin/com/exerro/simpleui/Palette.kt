package com.exerro.simpleui

/** A mapping from [PaletteColour]s to [RGB] values. */
fun interface Palette {
    /** Return the [RGB] value of a [PaletteColour]. */
    operator fun get(colour: PaletteColour): RGB

    /** A default palette with some nice colours. */
    object Default: Palette {
        override fun get(colour: PaletteColour) = when (colour) {
            is PaletteColour.Black -> when (colour.variant) {
                PaletteVariant.Dimmer -> RGB(10, 11, 11)
                PaletteVariant.Brighter -> RGB(0, 0, 0)
                PaletteVariant.Darker -> RGB(2, 2, 2)
                PaletteVariant.Default -> RGB(8, 8, 8)
                PaletteVariant.Lighter -> RGB(14, 14, 14)
            }
            is PaletteColour.Charcoal -> when (colour.variant) {
                PaletteVariant.Dimmer -> RGB(20, 20, 20)
                PaletteVariant.Brighter -> RGB(28, 28, 32)
                PaletteVariant.Darker -> RGB(18, 19, 19)
                PaletteVariant.Default -> RGB(24, 25, 25)
                PaletteVariant.Lighter -> RGB(30, 31, 31)
            }
            is PaletteColour.Silver -> when (colour.variant) {
                PaletteVariant.Dimmer -> RGB(162, 162, 162)
                PaletteVariant.Brighter -> RGB(200, 200, 200)
                PaletteVariant.Darker -> RGB(150, 150, 150)
                PaletteVariant.Default -> RGB(168, 168, 168)
                PaletteVariant.Lighter -> RGB(186, 186, 186)
            }
            is PaletteColour.White -> when (colour.variant) {
                PaletteVariant.Dimmer -> RGB(210, 210, 210).adjust(colour.variant)
                PaletteVariant.Brighter -> RGB(255, 255, 255).adjust(colour.variant)
                PaletteVariant.Darker -> RGB(225, 225, 225).adjust(colour.variant)
                PaletteVariant.Default -> RGB(235, 235, 235)
                PaletteVariant.Lighter -> RGB(242, 242, 242).adjust(colour.variant)
            }
            is PaletteColour.Blue -> when (colour.variant) {
                PaletteVariant.Dimmer -> RGB(70, 120, 160)
                PaletteVariant.Brighter -> RGB(50, 140, 210).adjust(colour.variant)
                PaletteVariant.Darker -> RGB(50, 140, 210).adjust(colour.variant)
                PaletteVariant.Default -> RGB(50, 140, 210)
                PaletteVariant.Lighter -> RGB(50, 140, 210).adjust(colour.variant)
            }
            is PaletteColour.Green -> when (colour.variant) {
                PaletteVariant.Dimmer -> RGB(70, 175, 70).adjust(colour.variant)
                PaletteVariant.Brighter -> RGB(70, 175, 70).adjust(colour.variant)
                PaletteVariant.Darker -> RGB(70, 175, 70).adjust(colour.variant)
                PaletteVariant.Default -> RGB(70, 175, 70)
                PaletteVariant.Lighter -> RGB(70, 175, 70).adjust(colour.variant)
            }
            is PaletteColour.Orange -> when (colour.variant) {
                PaletteVariant.Dimmer -> RGB(210, 140, 70).adjust(colour.variant)
                PaletteVariant.Brighter -> RGB(210, 140, 70).adjust(colour.variant)
                PaletteVariant.Darker -> RGB(210, 140, 70).adjust(colour.variant)
                PaletteVariant.Default -> RGB(210, 140, 70)
                PaletteVariant.Lighter -> RGB(210, 140, 70).adjust(colour.variant)
            }
            is PaletteColour.Pink -> when (colour.variant) {
                PaletteVariant.Dimmer -> RGB(220, 140, 180).adjust(colour.variant)
                PaletteVariant.Brighter -> RGB(220, 140, 180).adjust(colour.variant)
                PaletteVariant.Darker -> RGB(220, 140, 180).adjust(colour.variant)
                PaletteVariant.Default -> RGB(220, 140, 180)
                PaletteVariant.Lighter -> RGB(220, 140, 180).adjust(colour.variant)
            }
            is PaletteColour.Purple -> when (colour.variant) {
                PaletteVariant.Dimmer -> RGB(90, 70, 210).adjust(colour.variant)
                PaletteVariant.Brighter -> RGB(90, 70, 210).adjust(colour.variant)
                PaletteVariant.Darker -> RGB(90, 70, 210).adjust(colour.variant)
                PaletteVariant.Default -> RGB(90, 70, 210)
                PaletteVariant.Lighter -> RGB(90, 70, 210).adjust(colour.variant)
            }
            is PaletteColour.Red -> when (colour.variant) {
                PaletteVariant.Dimmer -> RGB(210, 70, 70).adjust(colour.variant)
                PaletteVariant.Brighter -> RGB(210, 70, 70).adjust(colour.variant)
                PaletteVariant.Darker -> RGB(210, 70, 70).adjust(colour.variant)
                PaletteVariant.Default -> RGB(210, 70, 70)
                PaletteVariant.Lighter -> RGB(210, 70, 70).adjust(colour.variant)
            }
            is PaletteColour.Teal -> when (colour.variant) {
                PaletteVariant.Dimmer -> RGB(50, 140, 140).adjust(colour.variant)
                PaletteVariant.Brighter -> RGB(50, 140, 140).adjust(colour.variant)
                PaletteVariant.Darker -> RGB(50, 140, 140).adjust(colour.variant)
                PaletteVariant.Default -> RGB(50, 140, 140)
                PaletteVariant.Lighter -> RGB(50, 140, 140).adjust(colour.variant)
            }
            is PaletteColour.Yellow -> when (colour.variant) {
                PaletteVariant.Dimmer -> RGB(230, 200, 70).adjust(colour.variant)
                PaletteVariant.Brighter -> RGB(230, 200, 70).adjust(colour.variant)
                PaletteVariant.Darker -> RGB(230, 200, 70).adjust(colour.variant)
                PaletteVariant.Default -> RGB(230, 200, 70)
                PaletteVariant.Lighter -> RGB(230, 200, 70).adjust(colour.variant)
            }
        }

        private fun RGB.adjust(variant: PaletteVariant): RGB = when (variant) {
            PaletteVariant.Dimmer -> this
            PaletteVariant.Brighter -> this
            PaletteVariant.Darker -> this
            PaletteVariant.Default -> this
            PaletteVariant.Lighter -> this
        }
    }
}
