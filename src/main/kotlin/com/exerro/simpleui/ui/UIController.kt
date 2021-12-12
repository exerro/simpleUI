package com.exerro.simpleui.ui

import com.exerro.simpleui.DrawContext
import com.exerro.simpleui.EventBus
import com.exerro.simpleui.UndocumentedExperimentalUI
import com.exerro.simpleui.WindowEvent
import com.exerro.simpleui.ui.internal.ComponentObject
import com.exerro.simpleui.ui.internal.PersistentComponentData
import com.exerro.simpleui.ui.internal.RootComponentData

@UndocumentedExperimentalUI
class UIController<Model: UIModel> private constructor(
    private val co: ComponentObject<Model, Float, Float, Nothing?, Nothing?>,
    val events: EventBus<Event>,
) {
    private var eventHandlers: List<ComponentEventHandler> = emptyList()

    @UndocumentedExperimentalUI
    fun draw(context: DrawContext) {
        val readyToDraw = co.resolveChildren(context.region.width, context.region.height, context.region.width, context.region.height)
        eventHandlers = readyToDraw.eventHandlers
        readyToDraw.draw(context)
    }

    @UndocumentedExperimentalUI
    fun pushEvent(event: WindowEvent) {
        for (eventHandler in eventHandlers.reversed()) {
            if (eventHandler(event)) break
        }
    }

    @UndocumentedExperimentalUI
    fun load() {
        co.refresh()
    }

    @UndocumentedExperimentalUI
    sealed interface Event {
        @UndocumentedExperimentalUI
        object Refreshed: Event
    }

    companion object {
        @UndocumentedExperimentalUI
        operator fun <Model: UIModel> invoke(
            initialModel: Model,
            init: DeferredComponentContext<Model, Float, Float, Nothing?, Nothing?>.() -> ComponentIsResolved
        ): UIController<Model> {
            lateinit var co: ComponentObject<Model, Float, Float, Nothing?, Nothing?>
            var currentModel = initialModel
            var refreshingCounter = 0
            val eventBus = PushableEventBus<Event>()
            val rootComponentData = object: RootComponentData<Model> {
                override fun getModel() = currentModel

                override fun setModel(model: Model) {
                    currentModel = model
                    co.refresh()
                }

                override fun parentNotifyRefreshed(completed: Boolean) {
                    if (completed) { if (--refreshingCounter == 0) eventBus.push(Event.Refreshed) }
                    else ++refreshingCounter
                }
            }

            co = ComponentObject(rootComponentData, PersistentComponentData(null, "<root>")) {
                DeferredComponentContext(this).init()
            }

            return UIController(co, eventBus)
        }

        @UndocumentedExperimentalUI
        operator fun invoke(
            init: DeferredComponentContext<UIModel, Float, Float, Nothing?, Nothing?>.() -> ComponentIsResolved
        ): UIController<UIModel> = invoke(UIModel(), init)
    }
}
