package com.exerro.simpleui.colour

/** HSL object containing [hue], [saturation], and [lightness] components. */
data class HSL(
    /** Hue component, in the range 0-6. */
    val hue: Float,
    /** Saturation component, in the range 0-1. */
    val saturation: Float,
    /** Lightness component, in the range 0-1. */
    val lightness: Float
)
