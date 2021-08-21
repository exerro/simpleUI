package com.exerro.simpleui.internal

import com.exerro.simpleui.*
import com.exerro.simpleui.colour.Colour

internal fun convertDrawFunction(
    fn: DrawContext.() -> Unit
): (DrawContextImpl, Long) -> Boolean = { impl, delta ->
    var animating = false

    val context = object: DrawContext {
        override val region = impl.region
        override val clipRegion = impl.clipRegion

        override fun fill(colour: Colour) {
            impl.fill(colour)
        }

        override fun roundedRectangle(cornerRadius: Pixels, colour: Colour, borderColour: Colour, borderWidth: Pixels) {
            impl.roundedRectangle(cornerRadius, colour, borderColour, borderWidth)
        }

        override fun ellipse(colour: Colour, borderColour: Colour, borderWidth: Pixels) {
            impl.ellipse(colour, borderColour, borderWidth)
        }

        override fun shadow(colour: Colour, radius: Pixels, offset: Pixels, cornerRadius: Pixels) {
            impl.shadow(colour, radius, offset, cornerRadius)
        }

        override fun image(path: String, tint: Colour?, isResource: Boolean) {
            impl.image(path, tint, isResource)
        }

        override fun write(
            font: Font,
            horizontalAlignment: Alignment,
            verticalAlignment: Alignment,
            indentationSize: Int,
            initialIndentation: Int,
            wrap: Boolean,
            writer: TextDrawContext.() -> Unit
        ) {
            impl.write(font, horizontalAlignment, verticalAlignment, indentationSize, initialIndentation, wrap, writer)
        }

        override fun Region.draw(clip: Boolean, draw: DrawContext.() -> Unit) {
            val d = convertDrawFunction(draw)
            impl.draw(this, clip) { animating = d(it, delta) || animating }
        }

//        override fun AnimatedValue<Region>.draw(clip: Boolean, draw: DrawContext.() -> Unit) {
//            update(delta).draw(clip, draw)
//            animating = animating || !isFinished
//        }
    }

    context.fn()

    animating
}
