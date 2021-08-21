package com.exerro.simpleui

/** An animated value. */
interface Animated<T> {
    /** Current value. */
    val currentValue: T

    /** Return true if the animated value has reached its target and no longer
     *  needs updating. */
    val isFinished: Boolean

    /** Update the animation clock with a [dt] in nanoseconds. */
    fun update(dt: Long)
}
