package com.exerro.simpleui.colour

import com.exerro.simpleui.internal.isRoughlyZero
import java.util.*

/** RGBA implementation of the [Colour] interface. */
data class RGBA(
    override val red: Float,
    override val green: Float,
    override val blue: Float,
    override val alpha: Float = 1f,
): Colour {
    constructor(red: Double, green: Double, blue: Double, alpha: Double = 1.0): this(
        red.toFloat(), green.toFloat(), blue.toFloat(), alpha.toFloat())

    constructor(red: Int, green: Int, blue: Int, alpha: Int = 255): this(
        red / 255f, green / 255f, blue / 255f, alpha / 255f)

    override val rgb get() = RGB(red, green, blue)
    override val hsl by lazy { Colours.rgbToHSL(red, green, blue) }
    override val hue get() = hsl.hue
    override val saturation get() = hsl.saturation
    override val lightness get() = hsl.lightness

    override fun equals(other: Any?) = other is Colour && (red - other.red).isRoughlyZero && (green - other.green).isRoughlyZero && (blue - other.blue).isRoughlyZero && (alpha - other.alpha).isRoughlyZero
    override fun hashCode() = Objects.hash(red, green, blue, alpha)
    override fun toString() = "RGBA($red, $green, $blue, $alpha)"
}
