package com.exerro.simpleui.ui.modifiers

import com.exerro.simpleui.Region
import com.exerro.simpleui.UndocumentedExperimentalUI
import com.exerro.simpleui.ui.*
import com.exerro.simpleui.ui.ComponentSizeResolver
import com.exerro.simpleui.ui.hooks.useMemory
import kotlin.math.min
import kotlin.time.Duration

@UndocumentedExperimentalUI
fun <Model: UIModel, Width: WhoDefinesMe, Height: WhoDefinesMe>
ComponentChildrenContext<Model, Width, Height>.withAnimatedRegion(
    duration: Duration = Duration.seconds(0.3),
    easing: (t: Float, a: Float, b: Float) -> Float = { t, a, b -> a + 3 * (b - a) * t * t - 2 * (b - a) * t * t * t },
) = object: ComponentChildrenContext<Model, Width, Height> by this {
    override fun component(elementType: String, id: Id, init: ComponentContext<Model, Width, Height>.() -> ComponentIsResolved) = this@withAnimatedRegion.component(elementType, id) {
        val outerComponentContext = this
        val (getLastPositionedRegion, setLastPositionedRegion) = useMemory<Region>()
        val (getAnimatingFromRegion, setAnimatingFromRegion) = useMemory<Region>()
        val (getAnimatingFromTime, setAnimatingFromTime) = useMemory<Long>()

        init(object: ComponentContext<Model, Width, Height> by outerComponentContext {
            override fun <SubWidth : WhoDefinesMe, SubHeight : WhoDefinesMe> children(
                getChildren: ComponentChildrenContext<Model, SubWidth, SubHeight>.() -> Unit,
                resolveComponentSize: (width: SizeForChild<Width>, height: SizeForChild<Height>, availableWidth: Float, availableHeight: Float, drawFunctions: List<ComponentDrawFunction>, eventHandlers: List<ComponentEventHandler>, children: List<ComponentSizeResolver<SubWidth, SubHeight>>) -> ResolvedComponentSizePhase<Width, Height>
            ) = outerComponentContext.children(getChildren) { width, height, availableWidth, availableHeight, drawFunctions, eventHandlers, children ->
                val sizeResolved = resolveComponentSize(width, height, availableWidth, availableHeight, drawFunctions, eventHandlers, children)

                sizeResolved.copy { r ->
                    val resolved = sizeResolved.positionResolver(r)
                    val currentPositionedRegion = resolved.region
                    val lastPositionedRegion = getLastPositionedRegion()

                    if (lastPositionedRegion != null && lastPositionedRegion != currentPositionedRegion) {
                        val time = System.nanoTime()
                        val currentlyAt = when (val animatingFromRegion = getAnimatingFromRegion()) {
                            null -> lastPositionedRegion
                            else -> {
                                val t = min(1f, (Duration.nanoseconds(time - getAnimatingFromTime()!!) / duration).toFloat())

                                Region(
                                    x = easing(t, animatingFromRegion.x, lastPositionedRegion.x),
                                    y = easing(t, animatingFromRegion.y, lastPositionedRegion.y),
                                    width = easing(t, animatingFromRegion.width, lastPositionedRegion.width),
                                    height = easing(t, animatingFromRegion.height, lastPositionedRegion.height),
                                )
                            }
                        }

                        setAnimatingFromRegion(currentlyAt)
                        setAnimatingFromTime(time)
                    }

                    val a = resolved.copy(draw = {
                        val time = System.nanoTime()
                        val animatingFromRegion = getAnimatingFromRegion()
                        val animatingFromTime = getAnimatingFromTime()
                        // we're not animating
                        if (animatingFromRegion == null || time > animatingFromTime!! + duration.inWholeNanoseconds) {
                            resolved.draw(this)
                        }
                        // we're animating
                        else {
                            val t = (Duration.nanoseconds(time - getAnimatingFromTime()!!) / duration).toFloat()
                            val drawRegion = Region(
                                x = easing(t, animatingFromRegion.x, region.x),
                                y = easing(t, animatingFromRegion.y, region.y),
                                width = easing(t, animatingFromRegion.width, region.width),
                                height = easing(t, animatingFromRegion.height, region.height),
                            )

                            withRegion(drawRegion, draw = resolved.draw)
                            if (t < 1f) dynamicContent()
                        }
                    })

                    setLastPositionedRegion(currentPositionedRegion)

                    a
                }
            }
        })
    }
}
