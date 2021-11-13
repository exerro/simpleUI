package com.exerro.simpleui.experimental

/** Variations to the [Default] colour of a [PaletteColour]. [Lighter] and
 *  [Darker] effectively control HSL lightness, where [Brighter] and [Dimmer]
 *  effectively control saturation. However, it is up to [Palette] instances
 *  to implement these appropriately. */
enum class PaletteVariant {
    Darker,
    Lighter,
    Dimmer,
    Brighter,
    Default,
}
