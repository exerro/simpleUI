package com.exerro.simpleui.extensions

import com.exerro.simpleui.AnimatedValue
import com.exerro.simpleui.Animation
import com.exerro.simpleui.Region
import com.exerro.simpleui.colour.Colour
import com.exerro.simpleui.colour.Colours
import kotlin.time.Duration

infix fun Float.Companion.animationTo(other: Float) =
    Animation(other) { t, a, b -> a * (1 - t) + b * t }

infix fun Region.Companion.animationTo(target: Region) =
    Animation(target) { t, a, b -> Region(
        x = a.x * (1 - t) + b.x * t,
        y = a.y * (1 - t) + b.y * t,
        width = a.width * (1 - t) + b.width * t,
        height = a.height * (1 - t) + b.height * t,
    ) }

infix fun Colour.Companion.animationTo(other: Colour) =
    Animation(other) { t, a, b -> Colours.mixRGBNoGamma(t, a, b) }

infix fun <T> Animation<T>.withDuration(duration: Duration) =
    copy(duration = duration)

infix fun <T> Animation<T>.withSeconds(duration: Int) =
    copy(duration = Duration.seconds(duration))

infix fun <T> Animation<T>.withSeconds(duration: Double) =
    copy(duration = Duration.seconds(duration))

infix fun <T> Animation<T>.withMilliseconds(duration: Int) =
    copy(duration = Duration.milliseconds(duration))

infix fun <T> Animation<T>.withMilliseconds(duration: Double) =
    copy(duration = Duration.milliseconds(duration))

infix fun <T> Animation<T>.withEasing(easing: Animation.Easing) =
    copy(easing = easing)

operator fun <T> Animation<T>.plus(other: Animation<T>) {
    val tk = (duration / (duration + other.duration)).toFloat()
    val tk1 = 1 - tk
    Animation(
        to = other.to,
        duration = duration + other.duration,
        easing = { t -> if (t <= tk) easing.fix(t / tk) * tk else tk + other.easing.fix((t - tk) / tk1) * tk1 },
        interpolator = { t, a, b -> (if (t <= tk) interpolator else other.interpolator)(t, a, b) }
    )
}

infix fun <T> Animation<T>.animatedNowFrom(initialValue: T) =
    AnimatedValue(initialValue, this)

fun <T> Animation<T>.animatedFrom(initialValue: T, clock: Long = 0L) =
    AnimatedValue(initialValue, this, clock)
