package com.exerro.simpleui.ui.components

import com.exerro.simpleui.Region
import com.exerro.simpleui.UndocumentedExperimentalUI
import com.exerro.simpleui.ui.ComponentChildrenContext
import com.exerro.simpleui.ui.ComponentIsResolved
import com.exerro.simpleui.ui.UIModel
import com.exerro.simpleui.ui.WhoDefinesMe
import com.exerro.simpleui.ui.hooks.useMemory
import com.exerro.simpleui.ui.modifiers.withDrawModifier
import kotlin.math.min
import kotlin.time.Duration

@UndocumentedExperimentalUI
fun <Model: UIModel, Width: WhoDefinesMe, Height: WhoDefinesMe>
ComponentChildrenContext<Model, Width, Height>.animatedRegion(
    duration: Duration = Duration.seconds(0.4),
    easing: (t: Float, a: Float, b: Float) -> Float = { t, a, b -> a + 3 * (b - a) * t * t - 2 * (b - a) * t * t * t },
    content: ComponentChildrenContext<Model, Width, Height>.() -> ComponentIsResolved,
) = component("animated-region") {
    val (getLastRegion, setLastRegion) = useMemory<Region>()
    val (getAnimatingFrom, setAnimatingFrom) = useMemory<Region>()
    val (getAnimatingFromTime, setAnimatingFromTime) = useMemory<Long>()

    withDrawModifier { draw ->
        val time = System.nanoTime()
        val lastRegion = getLastRegion()
        val animatingFrom = getAnimatingFrom()

        // first draw
        if (lastRegion == null) draw()
        // region differs to last region
        else if (region != lastRegion) {
            val currentlyAt = when (animatingFrom) {
                null -> lastRegion
                else -> {
                    val t = min(1f, (Duration.nanoseconds(time - getAnimatingFromTime()!!) / duration).toFloat())

                    Region(
                        x = easing(t, animatingFrom.x, lastRegion.x),
                        y = easing(t, animatingFrom.y, lastRegion.y),
                        width = easing(t, animatingFrom.width, lastRegion.width),
                        height = easing(t, animatingFrom.height, lastRegion.height),
                    )
                }
            }

            setAnimatingFrom(currentlyAt)
            setAnimatingFromTime(time)
            withRegion(currentlyAt, draw = draw)
            dynamicContent()
        }
        // we're not animating
        else if (animatingFrom == null) draw()
        // we're animating
        else {
            val t = min(1f, (Duration.nanoseconds(time - getAnimatingFromTime()!!) / duration).toFloat())
            val r = Region(
                x = easing(t, animatingFrom.x, region.x),
                y = easing(t, animatingFrom.y, region.y),
                width = easing(t, animatingFrom.width, region.width),
                height = easing(t, animatingFrom.height, region.height),
            )

            withRegion(r, draw = draw)
            if (t < 1f) dynamicContent()
        }

        setLastRegion(region)
    } .content()
}
