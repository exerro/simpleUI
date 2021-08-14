package com.exerro.simpleui

/** A unit representing a constant and relative dimension in
 *  pixels. */
data class Pixels(
    /** Constant dimension component. */
    val pixels: Float,
    /** Relative dimension component. */
    val relative: Float,
) {
    @Undocumented
    fun apply(context: Float) = relative * context + pixels

    ////////////////////////////////////////////////////////////

    operator fun plus(other: Pixels) =
        Pixels(pixels + other.pixels, relative + other.relative)

    operator fun minus(other: Pixels) =
        Pixels(pixels - other.pixels, relative - other.relative)

    operator fun times(scale: Float) =
        Pixels(pixels * scale, relative * scale)

    operator fun div(divisor: Float) =
        Pixels(pixels / divisor, relative / divisor)
}

val Double.px get() = Pixels(this.toFloat(), 0f)
val Float.px get() = Pixels(this, 0f)
val Int.px get() = Pixels(this.toFloat(), 0f)

val Double.percent get() = Pixels(0f, this.toFloat() / 100f)
val Float.percent get() = Pixels(0f, this / 100f)
val Int.percent get() = Pixels(0f, this / 100f)
