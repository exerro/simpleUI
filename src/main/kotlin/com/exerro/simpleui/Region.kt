package com.exerro.simpleui

import kotlin.math.floor

@Undocumented
data class Region internal constructor(
    @Undocumented
    val x: Float,
    @Undocumented
    val y: Float,
    @Undocumented
    val width: Float,
    @Undocumented
    val height: Float,
) {
    @Undocumented
    fun shrink(
        horizontallyBy: Pixels = 0.px,
        verticallyBy: Pixels = 0.px,
        horizontalAlignment: Alignment = 0.5f,
        verticalAlignment: Alignment = 0.5f,
    ): Region {
        val newWidth = (100.percent - horizontallyBy).apply(width)
        val newHeight = (100.percent - verticallyBy).apply(height)
        val newX = x + (width - newWidth) * horizontalAlignment
        val newY = y + (height - newHeight) * verticalAlignment

        return Region(newX, newY, newWidth, newHeight)
    }

    /** Shrinks by [by] pixels in both axes. See [shrink]. */
    fun shrink(
        by: Pixels,
        horizontalAlignment: Alignment = 0.5f,
        verticalAlignment: Alignment = 0.5f,
    ) = shrink(by, by, horizontalAlignment, verticalAlignment)

    ////////////////////////////////////////////////////////////////////////////

    @Undocumented
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

    @Undocumented
    fun withPadding(
        horizontal: Pixels = 0.px,
        vertical: Pixels = 0.px,
    ) = withPadding(vertical, horizontal, vertical, horizontal)

    @Undocumented
    fun withPadding(
        padding: Pixels,
    ) = withPadding(padding, padding, padding, padding)

    ////////////////////////////////////////////////////////////////////////////

    @Undocumented
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

    @Undocumented
    fun withAspectRatio(
        aspectRatio: Float,
        grow: Boolean = false,
        horizontalAlignment: Alignment = 0.5f,
        verticalAlignment: Alignment = 0.5f,
    ) {
        TODO()
    }

    ////////////////////////////////////////////////////////////////////////////

    @Undocumented
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

    @Undocumented
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

    @Undocumented
    fun listHorizontally(
        size: Pixels,
        offset: Pixels = 0.px,
        spacing: Pixels = 0.px,
    ): LazyRegionList {
        val x0 = x + offset.apply(width)
        val ww = size.apply(width)
        val dx = ww + spacing.apply(width)

        return LazyRegionList { n -> Region(x0 + n * dx, y, ww, height) }
    }

    @Undocumented
    fun listVertically(
        size: Pixels,
        offset: Pixels = 0.px,
        spacing: Pixels = 0.px,
    ): LazyRegionList {
        val y0 = y + offset.apply(height)
        val hh = size.apply(height)
        val dy = hh + spacing.apply(height)

        return LazyRegionList { n -> Region(y, y0 + n * dy, width, hh) }
    }

    ////////////////////////////////////////////////////////////////////////////

    @Undocumented
    fun splitHorizontally(
        at: Pixels = 50.percent,
    ): Pair<Region, Region> {
        val w0 = at.apply(width)
        val w1 = width - w0
        return Region(x, y, w0, height) to Region(x + w0, y, w1, height)
    }

    @Undocumented
    fun splitVertically(
        at: Pixels = 50.percent,
    ): Pair<Region, Region> {
        val h0 = at.apply(height)
        val h1 = height - h0
        return Region(x, y, width, h0) to Region(x, y + h0, width, h1)
    }

    @Undocumented
    fun splitHorizontally(
        at1: Pixels = 40.percent,
        at2: Pixels = 60.percent,
    ): Triple<Region, Region, Region> {
        TODO()
    }

    @Undocumented
    fun splitVertically(
        at1: Pixels = 40.percent,
        at2: Pixels = 60.percent,
    ): Triple<Region, Region, Region> {
        TODO()
    }
}
