package com.exerro.simpleui.colour

import com.exerro.simpleui.internal.isRoughlyZero
import java.util.*

/** HSLA implementation of the [Colour] interface. */
data class HSLA(
    override val hue: Float,
    override val saturation: Float,
    override val lightness: Float,
    override val alpha: Float = 1f,
): Colour {
    constructor(hue: Double, saturation: Double, lightness: Double, alpha: Double = 1.0): this(
        hue.toFloat(), saturation.toFloat(), lightness.toFloat(), alpha.toFloat())

    override val rgb by lazy { Colours.hslToRGB(hue, saturation, lightness) }
    override val hsl get() = HSL(hue, saturation, lightness)
    override val red get() = rgb.red
    override val green get() = rgb.green
    override val blue get() = rgb.blue

    override fun equals(other: Any?) = other is Colour && (red - other.red).isRoughlyZero && (green - other.green).isRoughlyZero && (blue - other.blue).isRoughlyZero && (alpha - other.alpha).isRoughlyZero
    override fun hashCode() = Objects.hash(red, green, blue, alpha)
    override fun toString() = "HSLA($hue, $saturation, $lightness, $alpha)"
}
