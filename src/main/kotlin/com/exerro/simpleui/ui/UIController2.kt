package com.exerro.simpleui.ui

import com.exerro.simpleui.DrawContext
import com.exerro.simpleui.UndocumentedExperimentalUI
import com.exerro.simpleui.ui.internal.ComponentInstance
import com.exerro.simpleui.ui.internal.PersistentComponentData2

@UndocumentedExperimentalUI
class UIController2<Model: UIModel>(
    initialModel: Model,
    init: DeferredComponentContext<Model, Float, Float, Nothing?, Nothing?>.() -> ComponentIsResolved,
) {
    private val c = ComponentInstance(
        controller = this,
        persistent = PersistentComponentData2(ID.Root, "root"),
        init = ComponentInstance.convertComponentFunction<Model, Float, Float, Nothing?, Nothing?> {
            init(DeferredComponentContext(this))
        }
    )
    private var currentModel = initialModel
    private val persistentData = mutableMapOf<ID, PersistentComponentData2>()
    private var eventHandlers = emptyList<ComponentEventHandler>()
    private var refreshedDuringWait = false
    private var waitingForRefresh = 0
    private var refreshingCounter = 0

    internal fun getPersistentData(id: ID, elementType: String): PersistentComponentData2 {
        if (id in persistentData) {
            val existing = persistentData[id]!!
            if (existing.elementType == elementType) return existing
        }
        val newPersistentData = PersistentComponentData2(id, elementType)
        persistentData[id] = newPersistentData
        return newPersistentData
    }

    val events = PushableEventBus<UIController.Event>()

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
        notifyRefreshed(false)
        currentModel = model
        notifyRefreshed(true)
    }

    @UndocumentedExperimentalUI
    fun updateModel(update: (Model) -> Model) =
        setModel(update(getModel()))

    @UndocumentedExperimentalUI
    fun notifyRefreshed(completed: Boolean) {
        if (!completed) ++refreshingCounter
        else {
            if (waitingForRefresh == 0) events.push(UIController.Event.Refreshed)
            else refreshedDuringWait = true
        }
    }

    @UndocumentedExperimentalUI
    fun setWaitingForRefresh(done: Boolean = false) {
        if (!done) {
            ++waitingForRefresh
        }
        if (done) {
            if (--waitingForRefresh == 0 && refreshedDuringWait) {
                events.push(UIController.Event.Refreshed)
                refreshedDuringWait = false
            }
        }
    }

    @UndocumentedExperimentalUI
    fun load() = c.refresh()

    companion object {
        @UndocumentedExperimentalUI
        operator fun invoke(
            init: DeferredComponentContext<UIModel, Float, Float, Nothing?, Nothing?>.() -> ComponentIsResolved
        ) = UIController2(UIModel(), init)
    }
}
