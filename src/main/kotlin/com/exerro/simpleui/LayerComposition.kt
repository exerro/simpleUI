package com.exerro.simpleui

@UndocumentedExperimental
fun interface LayerComposition {
    @UndocumentedExperimental
    fun Context.draw()

    @UndocumentedExperimental
    interface Context {
        @UndocumentedExperimental
        fun drawLayer(layer: Layer)
    }

    companion object {
        @UndocumentedInternal
        val Default = LayerComposition {
            drawLayer(Layer.Background)
            drawLayer(Layer.Default)
            drawLayer(Layer.Foreground)
        }

        @UndocumentedInternal
        val DefaultDebug = LayerComposition {
            drawLayer(Layer.Background)
            drawLayer(Layer.Default)
            drawLayer(Layer.Foreground)
            drawLayer(Layer.Debug)
        }
    }
}
