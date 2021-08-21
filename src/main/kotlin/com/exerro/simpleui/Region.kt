package com.exerro.simpleui

import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

/** A rectangular region within a window. */
data class Region internal constructor(
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
) {
    /** Return a copy of this region with all properties rounded to the nearest
     *  integer. */
    fun rounded() = Region(
        floor(x + 0.5f), floor(y + 0.5f),
        floor(width + 0.5f), floor(height + 0.5f)
    )

    ////////////////////////////////////////////////////////////////////////////

    /** Return the region of intersection between [this] and [other]. If there
     *  is no intersection, the [width] or [height] of the returned region will
     *  be 0. */
    infix fun intersectionWith(other: Region): Region {
        val ix = max(x, other.x)
        val iy = max(y, other.y)

        return Region(
            ix, iy,
            width = max(0f, min(x + width, other.x + other.width) - ix),
            height = max(0f, min(y + height, other.y + other.height) - iy),
        )
    }

    ////////////////////////////////////////////////////////////////////////////

    /** Shorthand for [withPadding]. */
    fun translateBy(
        dx: Pixels = 0.px,
        dy: Pixels = 0.px,
    ) = Region(x + dx.apply(width), y + dy.apply(width), width, height)

    ////////////////////////////////////////////////////////////////////////////

    /** Return a region with each edge moved inwards by the respective value. */
    fun withPadding(
        top: Pixels = 0.px,
        right: Pixels = 0.px,
        bottom: Pixels = 0.px,
        left: Pixels = 0.px,
    ): Region {
        val dx = left.apply(width)
        val dy = top.apply(height)
        val dw = dx + right.apply(width)
        val dh = dy + bottom.apply(height)

        return Region(x + dx, y + dy, width - dw, height - dh)
    }

    /** Shorthand for [withPadding]. */
    fun withPadding(
        horizontal: Pixels = 0.px,
        vertical: Pixels = 0.px,
    ) = withPadding(vertical, horizontal, vertical, horizontal)

    /** Shorthand for [withPadding]. */
    fun withPadding(
        padding: Pixels,
    ) = withPadding(padding, padding, padding, padding)

    ////////////////////////////////////////////////////////////////////////////

    /** Return a region resized to ([width] x [height]). [horizontalAlignment]
     *  and [verticalAlignment] control the positioning of the returned region,
     *  essentially defining a relative anchor point within each region that
     *  will be aligned after the resizing. E.g. if [horizontalAlignment] is 0,
     *  then the leftmost pixels of each region will be aligned. If
     *  [verticalAlignment] is 0.5, then the vertical centres of each region
     *  will be aligned. */
    fun resizeTo(
        width: Pixels = this.width.px,
        height: Pixels = this.height.px,
        horizontalAlignment: Alignment = 0.5f,
        verticalAlignment: Alignment = 0.5f,
    ): Region {
        val newWidth = width.apply(this.width)
        val newHeight = height.apply(this.height)
        val newX = x + (this.width - newWidth) * horizontalAlignment
        val newY = y + (this.height - newHeight) * verticalAlignment

        return Region(newX, newY, newWidth, newHeight)
    }

    ////////////////////////////////////////////////////////////////////////////

    /** Return a region with the given [aspectRatio]. If [grow] is true, the
     *  returned region will contain the original. Otherwise, the returned
     *  region will be contained within the original. [horizontalAlignment]
     *  and [verticalAlignment] control the positioning of the returned region
     *  as with [resizeTo]. */
    fun withAspectRatio(
        aspectRatio: Float,
        grow: Boolean = false,
        horizontalAlignment: Alignment = 0.5f,
        verticalAlignment: Alignment = 0.5f,
    ) = when {
        width / height == aspectRatio -> this
        grow && height * aspectRatio > width -> copy(
            x = x - (height * aspectRatio - width) * horizontalAlignment,
            width = height * aspectRatio,
        )
        grow && height * aspectRatio <= width -> copy(
            y = y - (width / aspectRatio - height) * verticalAlignment,
            height = width / aspectRatio,
        )
        height * aspectRatio > width -> copy(
            y = y + (width / aspectRatio - height) * verticalAlignment,
            height = width / aspectRatio,
        )
        else -> copy(
            x = x - (height * aspectRatio - width) * horizontalAlignment,
            width = height * aspectRatio,
        )
    }

    ////////////////////////////////////////////////////////////////////////////

    /** Align this region within another region according to the
     *  [horizontal][horizontalAlignment] and [vertical][verticalAlignment]
     *  alignments specified. */
    fun alignWithin(
        parent: Region,
        horizontalAlignment: Alignment = 0.5f,
        verticalAlignment: Alignment = 0.5f,
    ) = Region(
        x = parent.x + (parent.width - width) * horizontalAlignment,
        y = parent.y + (parent.height - height) * verticalAlignment,
        width = width,
        height = height,
    )

    ////////////////////////////////////////////////////////////////////////////

    /** Partition this region horizontally into [partitions] equally sized
     *  regions, with some amount of [spacing] between them. */
    fun partitionHorizontally(
        partitions: Int,
        spacing: Pixels = 0.px,
    ): List<Region> {
        val spacingValue = floor(spacing.apply(width))
        val totalSpacing = spacingValue * (partitions - 1)
        val partitionWidth = floor((width - totalSpacing) / partitions)
        val offset = spacingValue + partitionWidth
        val x1 = x + offset * (partitions - 1)

        return (0 until partitions - 1).map { index ->
            Region(x + offset * index, y, partitionWidth, height)
        } + listOf(Region(x1, y, width - x1 + x, height))
    }

    /** Partition this region vertically into [partitions] equally sized
     *  regions, with some amount of [spacing] between them. */
    fun partitionVertically(
        partitions: Int,
        spacing: Pixels = 0.px,
    ): List<Region> {
        val spacingValue = floor(spacing.apply(height))
        val totalSpacing = spacingValue * (partitions - 1)
        val partitionHeight = floor((height - totalSpacing) / partitions)
        val offset = spacingValue + partitionHeight
        val y1 = y + offset * (partitions - 1)

        return (0 until partitions - 1).map { index ->
            Region(x, y + offset * index, width, partitionHeight)
        } + listOf(Region(x, y1, width, height - y1 + y))
    }

    ////////////////////////////////////////////////////////////////////////////

    /** Return an infinite lazy list of regions with the given [width] stacked
     *  horizontally with some amount of [spacing]. [offset] controls an initial
     *  horizontal offset of the regions. */
    fun listHorizontally(
        width: Pixels,
        offset: Pixels = 0.px,
        spacing: Pixels = 0.px,
    ): LazyRegionList {
        val x0 = x + offset.apply(this.width)
        val ww = width.apply(this.width)
        val dx = ww + spacing.apply(this.width)

        return LazyRegionList { n -> Region(x0 + n * dx, y, ww, height) }
    }

    /** Return an infinite lazy list of regions with the given [height] stacked
     *  vertically with some amount of [spacing]. [offset] controls an initial
     *  vertical offset of the regions. */
    fun listVertically(
        height: Pixels,
        offset: Pixels = 0.px,
        spacing: Pixels = 0.px,
    ): LazyRegionList {
        val y0 = y + offset.apply(this.height)
        val hh = height.apply(this.height)
        val dy = hh + spacing.apply(this.height)

        return LazyRegionList { n -> Region(x, y0 + n * dy, width, hh) }
    }

    ////////////////////////////////////////////////////////////////////////////

    /** Split a region at the vertical line [at] into two regions stacked
     *  horizontally. */
    fun splitHorizontally(
        at: Pixels = 50.percent,
    ): Pair<Region, Region> {
        val w0 = at.apply(width)
        val w1 = width - w0
        return Region(x, y, w0, height) to Region(x + w0, y, w1, height)
    }

    /** Split a region at the horizontal line [at] into two regions stacked
     *  vertically. */
    fun splitVertically(
        at: Pixels = 50.percent,
    ): Pair<Region, Region> {
        val h0 = at.apply(height)
        val h1 = height - h0
        return Region(x, y, width, h0) to Region(x, y + h0, width, h1)
    }

    /** Split a region at the vertical line [at1] and [at2] into three regions
     *  stacked horizontally. */
    fun splitHorizontally(
        at1: Pixels = 40.percent,
        at2: Pixels = 60.percent,
    ): Triple<Region, Region, Region> {
        val w0 = at1.apply(width)
        val w1 = at2.apply(width) - w0
        val w2 = width - w1 - w0
        return Triple(Region(x, y, w0, height), Region(x + w0, y, w1, height), Region(x + w0 + w1, y, w2, height))
    }

    /** Split a region at the horizontal line [at1] and [at2] into three regions
     *  stacked vertically. */
    fun splitVertically(
        at1: Pixels = 40.percent,
        at2: Pixels = 60.percent,
    ): Triple<Region, Region, Region> {
        val h0 = at1.apply(height)
        val h1 = at2.apply(height) - h0
        val h2 = height - h0 - h1
        return Triple(Region(x, y, width, h0), Region(x, y + h0, width, h1), Region(x, y + h0 + h1, width, h2))
    }

    ////////////////////////////////////////////////////////////////////////////

    companion object
}

/** Return a [Region] containing every region provided. */
fun List<Region>.boundingRegion(): Region {
    val x = minOf { it.x }
    val y = minOf { it.y }
    val mx = maxOf { it.x + it.width }
    val my = maxOf { it.y + it.height }
    return Region(x, y, mx - x, my - y)
}
