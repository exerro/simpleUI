package com.exerro.simpleui.internal

internal infix fun Float.cycle(d: Float): Float {
    var n = this % d
    while (n < 0f) n += d
    while (n >= d) n -= d
    return n
}

internal val Float.isRoughlyZero get() = this > -0.000_000_1 && this < 0.000_000_1
