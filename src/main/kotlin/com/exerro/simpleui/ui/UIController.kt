package com.exerro.simpleui.ui

import com.exerro.simpleui.DrawContext
import com.exerro.simpleui.EventBus
import com.exerro.simpleui.UndocumentedExperimental
import com.exerro.simpleui.WindowEvent
import com.exerro.simpleui.extensions.PushableEventBus
import com.exerro.simpleui.ui.internal.ComponentObject
import com.exerro.simpleui.ui.internal.PersistentComponentData
import com.exerro.simpleui.ui.internal.RootComponentData

@UndocumentedExperimental
class UIController<Model: UIModel> private constructor(
    private val co: ComponentObject<Model, Float, Float, Nothing?, Nothing?>,
    val events: EventBus<Event>,
    private val root: RootComponentData<Model>,
) {
    private var eventHandlers: List<ComponentEventHandler> = emptyList()

    @UndocumentedExperimental
    fun draw(context: DrawContext) {
        val readyToDraw = co.resolveChildren(context.region.width, context.region.height, context.region.width, context.region.height)
        eventHandlers = readyToDraw.eventHandlers
        readyToDraw.draw(context)
    }

    @UndocumentedExperimental
    fun pushEvent(event: WindowEvent) {
        for (eventHandler in eventHandlers.reversed()) {
            if (eventHandler(event)) break
        }
    }

    @UndocumentedExperimental
    fun load() {
        co.refresh()
    }

    @UndocumentedExperimental
    fun updateModel(update: (Model) -> Model) {
        root.setModel(update(root.getModel()))
    }

    @UndocumentedExperimental
    fun setModel(model: Model) {
        root.setModel(model)
    }

    @UndocumentedExperimental
    sealed interface Event {
        @UndocumentedExperimental
        object Refreshed: Event
    }

    companion object {
        operator fun <Model: UIModel> invoke(
            initialModel: Model,
            init: DeferredComponentContext<Model, Float, Float, Nothing?, Nothing?>.() -> ComponentReturn
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

            return UIController(co, eventBus, rootComponentData)
        }
    }
}
