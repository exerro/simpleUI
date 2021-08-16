package com.exerro.simpleui.colour

import kotlin.math.max
import kotlin.math.min

/** Abstract Colour interface with an alpha component and both RGB/HSL colour
 *  components. See [Colours] for colour constants and utility methods, and
 *  ([RGBA], [HSLA], and [Greyscale]) for default implementations. */
interface Colour {
    /** Alpha (transparency) component in the range 0-1. */
    val alpha: Float

    /** Perceived brightness of the colour.
     *
     *  See: https://stackoverflow.com/questions/596216/formula-to-determine-brightness-of-rgb-color
     *
     *  See: https://www.w3.org/TR/AERT/#color-contrast */
    val brightness: Float get() = 0.299f * red + 0.587f * green + 0.114f * blue

    /** RGB components of the colour. All components are in the range 0-1.
     *  See [red], [green], [blue]. */
    val rgb: RGB

    /** Red component, see [rgb]. */
    val red: Float get() = rgb.red

    /** Green component, see [rgb]. */
    val green: Float get() = rgb.green

    /** Blue component, see [rgb]. */
    val blue: Float get() = rgb.blue

    /** HSL components of the colour. See [hue], [saturation], [lightness]. */
    val hsl: HSL

    /** Hue component, in the range 0-6. See [hsl]. */
    val hue: Float get() = hsl.hue

    /** Saturation component, in the range 0-1. See [hsl]. */
    val saturation: Float get() = hsl.saturation

    /** Lightness component, in the range 0-1. See [hsl]. */
    val lightness: Float get() = hsl.lightness

    /** Return a copy of the colour with modified values. Note, this does not
     *  synchronise the values in any way and should therefore be used
     *  carefully. */
    fun copy(
        alpha: Float = this.alpha, brightness: Float = this.brightness,
        rgb: RGB = this.rgb, red: Float = this.red, green: Float = this.green, blue: Float = this.blue,
        hsl: HSL = this.hsl, hue: Float = this.hue, saturation: Float = this.saturation, lightness: Float = this.lightness,
    ) = object: Colour {
        override val alpha = alpha
        override val brightness = brightness
        override val rgb = rgb
        override val red = red
        override val green = green
        override val blue = blue
        override val hsl = hsl
        override val hue = hue
        override val saturation = saturation
        override val lightness = lightness
    }

    /** Return the same colour with a different alpha value. */
    fun withAlpha(alpha: Float) = copy(alpha = alpha)

    /** Return a lighter variant of the colour. */
    fun lighten(amount: Float = 0.1f) =
        HSLA(hue, saturation, min(lightness + amount, 1f), alpha)

    /** Return a darker variant of the colour. */
    fun darken(amount: Float = 0.1f) =
        HSLA(hue, saturation, max(lightness - amount, 0f), alpha)

    /** Return the colour with no alterations. */
    fun identity() = this
}
