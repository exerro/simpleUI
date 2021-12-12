package com.exerro.simpleui.ui

import com.exerro.simpleui.DrawContext
import com.exerro.simpleui.UndocumentedExperimentalUI
import com.exerro.simpleui.WindowEvent
import com.exerro.simpleui.ui.internal.ComponentInstance
import com.exerro.simpleui.ui.internal.PersistentComponentData

@UndocumentedExperimentalUI
class UIController<Model: UIModel>(
    initialModel: Model,
    init: DeferredComponentContext<Model, Float, Float, Nothing?, Nothing?>.() -> ComponentIsResolved,
) {
    val events = PushableEventBus<Event>()

    @UndocumentedExperimentalUI
    fun pushEvent(event: WindowEvent) {
        for (eventHandler in eventHandlers.reversed()) {
            if (eventHandler(event)) break
        }
    }

    @UndocumentedExperimentalUI
    fun draw(context: DrawContext) {
        val readyToDraw = c.transient.resolver(context.region.width, context.region.height, context.region.width, context.region.height)
        eventHandlers = readyToDraw.eventHandlers
        readyToDraw.draw(context)
    }

    @UndocumentedExperimentalUI
    fun getModel() = currentModel

    @UndocumentedExperimentalUI
    fun setModel(model: Model) {
        currentModel = model
        c.refresh()
    }

    @UndocumentedExperimentalUI
    fun updateModel(update: (Model) -> Model) =
        setModel(update(getModel()))

    @UndocumentedExperimentalUI
    fun load() = c.refresh()

    private var currentModel = initialModel
    private val persistentData = mutableMapOf<Id, PersistentComponentData>()
    private var eventHandlers = emptyList<ComponentEventHandler>()
    private var refreshedDuringWait = false
    private var waitingForRefresh = 0
    private var refreshingCounter = 0
    private val c = ComponentInstance(
        controller = this,
        persistent = PersistentComponentData(Id.Root, "root"),
        init = ComponentInstance.convertComponentFunction<Model, Float, Float, Nothing?, Nothing?> {
            init(DeferredComponentContext(this))
        }
    )

    internal fun getPersistentData(id: Id, elementType: String): PersistentComponentData {
        if (id in persistentData) {
            val existing = persistentData[id]!!
            if (existing.elementType == elementType) return existing
        }
        val newPersistentData = PersistentComponentData(id, elementType)
        persistentData[id] = newPersistentData
        return newPersistentData
    }

    @UndocumentedExperimentalUI
    internal fun notifyRefreshing(completed: Boolean) {
        if (completed) {
            if (--refreshingCounter == 0) {
                if (waitingForRefresh == 0) events.push(Event.Refreshed)
                else refreshedDuringWait = true
            }
        }
        else ++refreshingCounter
    }

    @UndocumentedExperimentalUI
    internal fun setWaitingForRefresh(done: Boolean = false) {
        if (!done) {
            ++waitingForRefresh
        }
        if (done) {
            if (--waitingForRefresh == 0 && refreshedDuringWait) {
                events.push(Event.Refreshed)
                refreshedDuringWait = false
            }
        }
    }

    @UndocumentedExperimentalUI
    sealed interface Event {
        @UndocumentedExperimentalUI
        object Refreshed: Event
    }

    companion object {
        @UndocumentedExperimentalUI
        operator fun invoke(
            init: DeferredComponentContext<UIModel, Float, Float, Nothing?, Nothing?>.() -> ComponentIsResolved
        ) = UIController(UIModel(), init)
    }
}
