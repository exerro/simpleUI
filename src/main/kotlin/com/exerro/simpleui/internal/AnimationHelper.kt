package com.exerro.simpleui.internal

import com.exerro.simpleui.*
import kotlin.math.sqrt

@Undocumented
class AnimationHelper {
    @Undocumented
    fun beginFrame() {
        thisFrameTime = System.nanoTime()
        thisFrame.clear()
    }

    @Undocumented
    fun evaluateRegion(
        region: Region,
        clipRegion: Region,
        id: StaticIdentifier,
        mount: MountPoint?,
        draw: DrawContext.() -> Unit,
    ): Region {
        val lastFrameData = lastFrame[id]

        // if we've never seen the region before
        return if (lastFrameData == null) {
            // if it's not mounted, it gets positioned at its target location
            // straight away
            // otherwise, it gets positioned at the entrance region
            val initialPosition = if (mount == null) region else
                evaluateMountRegion(region, clipRegion, mount)

            thisFrame.add(CachedRegionDraw(
                positionedAt = thisFrameTime,
                initialPosition = initialPosition,
                mode = PositionMode.Entrance,
                target = region,
                clipRegion = clipRegion,
                mount = mount, id = id, draw = draw
            ))
            initialPosition
        }
        // if we've seen the region before
        else {
            val seenFor = thisFrameTime - lastFrameData.positionedAt
            val currentPosition = if (seenFor >= animationDuration) lastFrameData.target
                else lerpAny(seenFor / animationDuration.toFloat(), lastFrameData.initialPosition, lastFrameData.target, lastFrameData.mode)

            // the region has moved - should transition between the two
            if (lastFrameData.target != region) {
                thisFrame.add(CachedRegionDraw(
                    positionedAt = thisFrameTime,
                    initialPosition = currentPosition,
                    mode = PositionMode.Transition,
                    target = region,
                    clipRegion = clipRegion,
                    mount ?: lastFrameData.mount, id, draw
                ))
            }
            // we're aiming for the same place, keep the same cached data (except the draw fn might've changed)
            else {
                thisFrame.add(lastFrameData.copy(
                    draw = draw,
                    clipRegion = clipRegion,
                    mount = mount ?: lastFrameData.mount
                ))
            }

            currentPosition
        }
    }

    @Undocumented
    fun endFrame(): List<ExitDraw> {
        val removedRegions = lastFrame.values.filter { r -> !thisFrame.any { it.id == r.id } }

        lastFrame.clear()

        for (r in thisFrame) {
            lastFrame[r.id] = r
        }

        for (r in removedRegions) {
            val seenFor = thisFrameTime - r.positionedAt
            val currentPosition = if (seenFor >= animationDuration) r.target
            else lerpAny(seenFor / animationDuration.toFloat(), r.initialPosition, r.target, r.mode)

            if (r.mount == null) continue

            removedEntities.add(ExitDrawData(
                removedAt = thisFrameTime,
                initialPosition = currentPosition,
                target = evaluateMountRegion(currentPosition, r.clipRegion, r.mount),
                clipRegion = r.clipRegion,
                draw = r.draw
            ))
        }

        removedEntities.removeAll {
            it.removedAt + animationDuration <= thisFrameTime
        }

        return removedEntities.map { d ->
            val t = (thisFrameTime - d.removedAt) / animationDuration.toFloat()
            val region = lerpAny(t, d.initialPosition, d.target, PositionMode.Exit)
            ExitDraw(region, d.clipRegion, d.draw)
        }
    }

    ////////////////////////////////////////////////////////////

    @Undocumented
    data class ExitDraw(
        val region: Region,
        val clipRegion: Region,
        val draw: DrawContext.() -> Unit,
    )

    ////////////////////////////////////////////////////////////

    @Undocumented
    private fun evaluateMountRegion(
        targetRegion: Region,
        clipRegion: Region,
        mount: MountPoint,
    )  = when (mount) {
        MountPoint.Top -> targetRegion.copy(y = clipRegion.y - targetRegion.height)
        MountPoint.Right -> targetRegion.copy(x = clipRegion.x + clipRegion.width)
        MountPoint.Bottom -> targetRegion.copy(y = clipRegion.y + clipRegion.height)
        MountPoint.Left -> targetRegion.copy(x = clipRegion.x - targetRegion.width)
        MountPoint.InPlace -> targetRegion.copy(
            x = targetRegion.x + targetRegion.width / 2,
            y = targetRegion.y + targetRegion.height / 2,
            width = 0f, height = 0f,
        )
    }

    @Undocumented
    private fun lerpIn(t: Float, a: Float, b: Float) =
        a + sqrt(t) * (b - a)

    @Undocumented
    private fun lerpOut(t: Float, a: Float, b: Float) =
        a + (t * t) * (b - a)

    @Undocumented
    private fun lerpBetween(t: Float, a: Float, b: Float) =
        a + (3 * t * t - 2 * t * t * t) * (b - a)

    @Undocumented
    private fun lerpAny(t: Float, a: Region, b: Region, mode: PositionMode): Region {
        val lerp = when (mode) {
            PositionMode.Entrance -> this::lerpIn
            PositionMode.Transition -> this::lerpBetween
            PositionMode.Exit -> this::lerpOut
        }
        return Region(
            x = lerp(t, a.x, b.x),
            y = lerp(t, a.y, b.y),
            width = lerp(t, a.width, b.width),
            height = lerp(t, a.height, b.height),
        )
    }

    ////////////////////////////////////////////////////////////

    @Undocumented
    private val animationDuration = 250000000L

    @Undocumented
    private val thisFrame: MutableList<CachedRegionDraw> = mutableListOf()

    @Undocumented
    private val removedEntities: MutableList<ExitDrawData> = mutableListOf()

    @Undocumented
    private val lastFrame: MutableMap<StaticIdentifier, CachedRegionDraw> = mutableMapOf()

    @Undocumented
    private var thisFrameTime: Long = 0L

    @Undocumented
    private data class CachedRegionDraw(
        val positionedAt: Long,
        val initialPosition: Region,
        val mode: PositionMode,
        val target: Region,
        val clipRegion: Region,
        val mount: MountPoint?,
        val id: StaticIdentifier,
        val draw: DrawContext.() -> Unit,
    )

    @Undocumented
    data class ExitDrawData(
        val removedAt: Long,
        val initialPosition: Region,
        val target: Region,
        val clipRegion: Region,
        val draw: DrawContext.() -> Unit,
    )

    @Undocumented
    private enum class PositionMode {
        Entrance,
        Transition,
        Exit,
    }
}
