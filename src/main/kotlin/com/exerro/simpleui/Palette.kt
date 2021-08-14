package com.exerro.simpleui

@Undocumented
fun interface Palette {
    @Undocumented
    operator fun get(colour: PaletteColour): RGB

    @Undocumented
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
                PaletteVariant.Darker -> RGB(16, 17, 17)
                PaletteVariant.Default -> RGB(24, 25, 25)
                PaletteVariant.Lighter -> RGB(36, 37, 37)
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
                PaletteVariant.Darker -> RGB(210, 210, 210).adjust(colour.variant)
                PaletteVariant.Default -> RGB(235, 235, 235)
                PaletteVariant.Lighter -> RGB(250, 250, 250).adjust(colour.variant)
            }
            is PaletteColour.Blue -> when (colour.variant) {
                PaletteVariant.Dimmer -> RGB(50, 140, 210).adjust(colour.variant)
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
                PaletteVariant.Dimmer -> RGB(210, 140, 175).adjust(colour.variant)
                PaletteVariant.Brighter -> RGB(210, 140, 175).adjust(colour.variant)
                PaletteVariant.Darker -> RGB(210, 140, 175).adjust(colour.variant)
                PaletteVariant.Default -> RGB(210, 140, 175)
                PaletteVariant.Lighter -> RGB(210, 140, 175).adjust(colour.variant)
            }
            is PaletteColour.Purple -> when (colour.variant) {
                PaletteVariant.Dimmer -> RGB(70, 70, 210).adjust(colour.variant)
                PaletteVariant.Brighter -> RGB(70, 70, 210).adjust(colour.variant)
                PaletteVariant.Darker -> RGB(70, 70, 210).adjust(colour.variant)
                PaletteVariant.Default -> RGB(70, 70, 210)
                PaletteVariant.Lighter -> RGB(70, 70, 210).adjust(colour.variant)
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
                PaletteVariant.Dimmer -> RGB(210, 200, 70).adjust(colour.variant)
                PaletteVariant.Brighter -> RGB(210, 200, 70).adjust(colour.variant)
                PaletteVariant.Darker -> RGB(210, 200, 70).adjust(colour.variant)
                PaletteVariant.Default -> RGB(210, 200, 70)
                PaletteVariant.Lighter -> RGB(210, 200, 70).adjust(colour.variant)
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
