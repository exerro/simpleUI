package com.exerro.simpleui.ui

import com.exerro.simpleui.DrawContext
import com.exerro.simpleui.EventBus
import com.exerro.simpleui.UndocumentedExperimental
import com.exerro.simpleui.extensions.PushableEventBus
import com.exerro.simpleui.ui.internal.ComponentObject

@UndocumentedExperimental
class UIController private constructor(
    private val co: ComponentObject<Float, Float, Nothing?, Nothing?>,
    val events: EventBus<Event>,
) {
    @UndocumentedExperimental
    fun draw(context: DrawContext) {
        val readyToDraw = co.resolveChildren(context.region.width, context.region.height, context.region.width, context.region.height)
        readyToDraw.draw(context)
    }

    @UndocumentedExperimental
    fun load() {
        co.refresh()
    }

    @UndocumentedExperimental
    sealed interface Event {
        @UndocumentedExperimental
        object Refreshed: Event
    }

    companion object {
        operator fun invoke(
            init: BasicComponentContext<Float, Float, Nothing?, Nothing?>.() -> ChildReturn
        ): UIController {
            var refreshingCounter = 0
            val eventBus = PushableEventBus<Event>()
            val co = ComponentObject<Float, Float, Nothing?, Nothing?>("<root>", null, {
                BasicComponentContext(this).init()
            }, { completed ->
                if (completed) { if (--refreshingCounter == 0) eventBus.push(Event.Refreshed) }
                else ++refreshingCounter
                Unit
            })

            return UIController(co, eventBus)
        }
    }
}