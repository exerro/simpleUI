package com.exerro.simpleui.colour

import com.exerro.simpleui.Undocumented
import com.exerro.simpleui.internal.cycle
import com.exerro.simpleui.internal.isRoughlyZero
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

/** Colour constants and utility methods for mixing/generating colours. */
object Colours {
    /** Convert RGB to HSL. */
    fun rgbToHSL(red: Float, green: Float, blue: Float): HSL {
        // HSV algorithms from http://marcocorvi.altervista.org/games/imgpr/rgb-hsl.htm
        val max = max(max(red, green), blue)
        val min = min(min(red, green), blue)
        val l = (max + min) / 2
        val s = when {
            max == min -> 0f
            l < 0.5f -> (max - min) / (max + min)
            else -> (max - min) / (2 - max - min)
        }
        val h = when {
            max == min -> 0f
            red == max -> (green - blue) / (max - min)
            green == max -> 2 + (blue - red) / (max - min)
            else -> 4 + (red - green) / (max - min)
        } cycle 6f
        return HSL(h, s, l)
    }

    /** Convert RGB to HSL. */
    fun hslToRGB(hue: Float, saturation: Float, lightness: Float): RGB {
        return if (saturation.isRoughlyZero) {
            RGB(lightness, lightness, lightness)
        }
        else {
            val t2 = if (lightness < 0.5f) lightness * (1 + saturation) else lightness + saturation - lightness * saturation
            val t1 = 2 * lightness - t2
            val h = (hue / 6f) cycle 1f
            val r = ((h + 1 / 3f) cycle 1f).calculateRGBComponent(t1, t2)
            val g = h.calculateRGBComponent(t1, t2)
            val b = ((h - 1 / 3f) cycle 1f).calculateRGBComponent(t1, t2)
            RGB(r, g, b)
        }
    }

    /** Interpolate between colours [a] and [b] without gamma correction.
     *  When [ratio] is 0, [a] will be returned. When [ratio] is 1, [b] will
     *  be returned. [ratio] values between 0 and 1 will mix gradually from
     *  [a] to [b]. */
    fun mixRGBNoGamma(ratio: Float, a: Colour, b: Colour) = RGBA(
        red = a.red * (1 - ratio) + b.red * ratio,
        green = a.green * (1 - ratio) + b.green * ratio,
        blue = a.blue * (1 - ratio) + b.blue * ratio,
        alpha = a.alpha * (1 - ratio) + b.alpha * ratio
    )

    /** Interpolate between colours [a] and [b] doing gamma correction. When
     *  [ratio] is 0, [a] will be returned. When [ratio] is 1, [b] will be
     *  returned. [ratio] values between 0 and 1 will mix gradually from [a]
     *  to [b]. */
    fun mixRGB(ratio: Float, a: Colour, b: Colour, gamma: Float = 2.2f): Colour {
        val rgba = RGBA(
            red = (a.red.pow(gamma) * (1 - ratio) + b.red.pow(gamma) * ratio).pow(1 / gamma),
            green = (a.green.pow(gamma) * (1 - ratio) + b.green.pow(gamma) * ratio).pow(1 / gamma),
            blue = (a.blue.pow(gamma) * (1 - ratio) + b.blue.pow(gamma) * ratio).pow(1 / gamma),
            alpha = a.alpha * (1 - ratio) + b.alpha * ratio
        )
        return rgba
    }

    /** Mix two colours by interpolating the hue, saturation and lightness.
     *  Hue is interpolated cyclically (in whichever 'direction' the
     *  colours are closest). */
    fun mixHSL(ratio: Float, a: Colour, b: Colour): Colour = HSLA(
        hue = when {
            a.hue < b.hue && b.hue - a.hue > 3f -> (a.hue * (1 - ratio) + (b.hue - 6) * ratio) cycle 6f
            a.hue > b.hue && a.hue - b.hue > 3f -> ((a.hue - 6) * (1 - ratio) + (b.hue) * ratio) cycle 6f
            else -> a.hue * (1 - ratio) + b.hue * ratio
        },
        saturation = a.saturation * (1 - ratio) + b.saturation * ratio,
        lightness = a.lightness * (1 - ratio) + b.lightness * ratio,
        alpha = a.alpha * (1 - ratio) + b.alpha * ratio
    )

    @Undocumented
    fun random(): Colour =
        RGBA(Math.random(), Math.random(), Math.random())

    /** No colour. */
    val transparent = RGBA(0f, 0f, 0f, 0f)

    /** Standard red colour. */
//    val red = RGBA(0.8, 0.3, 0.3)
    val red = RGBA(210, 70, 70)

    /** Standard green colour. */
//    val green = RGBA(0.2, 0.8, 0.4)
    val green = RGBA(70, 175, 70)

    /** Standard blue colour. */
//    val blue = RGBA(0.1, 0.5, 0.9)
    val blue = RGBA(50, 140, 210)

    /** Standard yellow colour. */
//    val yellow = RGBA(0.9, 0.85, 0.3)
    val yellow = RGBA(230, 200, 70)

    /** Standard orange colour. */
//    val orange = RGBA(0.9, 0.5, 0.3)
    val orange = RGBA(210, 140, 70)

    /** Standard pink colour. */
//    val pink = RGBA(0.9, 0.4, 0.6)
    val pink = RGBA(220, 140, 180)

    /** Standard purple colour. */
//    val purple = RGBA(0.7, 0.1, 1.0)
    val purple = RGBA(90, 70, 210)

    /** Standard cyan colour. */
//    val teal = RGBA(0.2, 0.7, 0.7)
    val teal = RGBA(50, 140, 140)

    /** Pure white. */
    val pureWhite = Greyscale(0.95)

    /** A colour very close to white. Darker than [pureWhite], lighter than
     *  [ultraLightGrey]. */
    val white = Greyscale(0.95)

    /** Darker than [white], lighter than [lightGrey]. */
    val ultraLightGrey = Greyscale(0.85)

    /** Darker than [ultraLightGrey], lighter than [lighterGrey]. */
    val lightGrey = Greyscale(0.75)

    /** Darker than [lightGrey], lighter than [grey]. */
    val lighterGrey = RGBA(0.59, 0.58, 0.6)

    /** Darker than [lighterGrey], lighter than [darkGrey]. */
    val grey = RGBA(0.33, 0.33, 0.35)

    /** Darker than [grey], lighter than [charcoal]. */
    val darkGrey = RGBA(0.19, 0.19, 0.2)

    /** Darker than [darkGrey], lighter than [black]. */
    val charcoal = RGBA(0.13, 0.13, 0.14)

    /** A colour very close to black. Darker than [charcoal], lighter than
     *  [pureBlack]. */
    val black = Greyscale(0.1)

    /** Pure black. */
    val pureBlack = Greyscale(0.0)

    /** All the white, grey and black colours ordered lightest to darkest. */
    val greyscale = listOf(
        pureWhite,
        white,
        ultraLightGrey,
        lightGrey,
        lighterGrey,
        grey,
        darkGrey,
        charcoal,
        black,
        pureBlack,
    )

    /** All the non-greyscale pre-defined colours. */
    val colours = listOf(
        red,
        orange,
        yellow,
        green,
        teal,
        blue,
        purple,
        pink
    )

    /** All the pre-defined colours. */
    val all = greyscale + colours

    private fun Float.calculateRGBComponent(temp1: Float, temp2: Float) = when {
        this < 1/6f -> temp1 + (temp2 - temp1) * 6 * this
        this < 1/2f -> temp2
        this < 2/3f -> temp1 + (temp2 - temp1) * (2/3f - this) * 6
        else -> temp1
    }
}