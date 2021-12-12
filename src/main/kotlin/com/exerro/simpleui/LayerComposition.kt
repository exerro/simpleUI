package com.exerro.simpleui

// TODO: some way of disabling layers in an efficient way (so withLayer {})
//       doesn't run the callback.

/** [LayerComposition] allows the user to order [Layer]s. */
fun interface LayerComposition {
    /** Specify when to draw layers using [Context]. See [Context]. */
    fun Context.draw()

    /** Contains a single method [drawLayer]. See [drawLayer]. */
    interface Context {
        /** Draw the given [layer]. */
        fun drawLayer(layer: Layer)
    }

    companion object {
        /** Default layer composition. */
        val Default = LayerComposition {
            drawLayer(Layer.Background)
            drawLayer(Layer.Default)
            drawLayer(Layer.Foreground)
        }

        /** Default debug layer composition. */
        val DefaultDebug = LayerComposition {
            drawLayer(Layer.Background)
            drawLayer(Layer.Default)
            drawLayer(Layer.Foreground)
            drawLayer(Layer.Debug)
        }
    }
}
