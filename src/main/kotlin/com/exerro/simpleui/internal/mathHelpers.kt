package com.exerro.simpleui.internal

import com.exerro.simpleui.UndocumentedInternal
import kotlin.math.sqrt

internal infix fun Float.cycle(d: Float): Float {
    var n = this % d
    while (n < 0f) n += d
    while (n >= d) n -= d
    return n
}

internal val Float.isRoughlyZero get() = this > -0.000_000_1 && this < 0.000_000_1

@UndocumentedInternal
private fun lerpIn(t: Float, a: Float, b: Float) =
    a + sqrt(t) * (b - a)

@UndocumentedInternal
private fun lerpOut(t: Float, a: Float, b: Float) =
    a + (t * t) * (b - a)

@UndocumentedInternal
private fun lerpBetween(t: Float, a: Float, b: Float) =
    a + (3 * t * t - 2 * t * t * t) * (b - a)
