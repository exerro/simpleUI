package com.exerro.simpleui.colour

import com.exerro.simpleui.internal.isRoughlyZero
import java.util.*

/** Greyscale implementation of the [Colour] interface. */
data class Greyscale(
    override val lightness: Float,
    override val alpha: Float = 1f
): Colour {
    constructor(lightness: Double, alpha: Double = 1.0): this(
        lightness.toFloat(), alpha.toFloat())

    override val brightness = lightness
    override val rgb get() = RGB(lightness, lightness, lightness)
    override val red = lightness
    override val green = lightness
    override val blue = lightness
    override val hsl get() = HSL(0f, 0f, lightness)
    override val hue = 0f
    override val saturation = 0f

    override fun equals(other: Any?) = other is Colour && (red - other.red).isRoughlyZero && (green - other.green).isRoughlyZero && (blue - other.blue).isRoughlyZero && (alpha - other.alpha).isRoughlyZero
    override fun hashCode() = Objects.hash(red, green, blue, alpha)
}
