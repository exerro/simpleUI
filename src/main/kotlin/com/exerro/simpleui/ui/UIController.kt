package com.exerro.simpleui.ui

import com.exerro.simpleui.*
import com.exerro.simpleui.ui.internal.ComponentInstance
import com.exerro.simpleui.ui.internal.PersistentComponentData

@UndocumentedExperimentalUI
class UIController<Model: UIModel>(
    initialModel: Model,
    init: ComponentChildContext<Model, ParentDefinesMe, ParentDefinesMe>.() -> ComponentIsResolved,
) {
    val events = PushableEventBus<Event>()

    @UndocumentedExperimentalUI
    fun pushEvent(event: WindowEvent): Boolean {
        for (eventHandler in eventHandlers.reversed()) {
            if (eventHandler(event)) return true
        }
        return false
    }

    @UndocumentedExperimentalUI
    fun reposition(width: Float, height: Float) {
        positionResolvedContent = c.transient
            .sizeResolver(fixForChild(width), fixForChild(height), width, height)
            .positionResolver(Region(0f, 0f, width, height))

        eventHandlers = positionResolvedContent.eventHandlers
    }

    @UndocumentedExperimentalUI
    fun draw(context: DrawContext) {
        positionResolvedContent.draw(context)
    }

    @UndocumentedExperimentalUI
    fun repositionAndDraw(context: DrawContext) {
        reposition(context.region.width, context.region.height)
        draw(context)
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

    private lateinit var positionResolvedContent: PositionResolvedComponent
    private var currentModel = initialModel
    private val persistentData = mutableMapOf<Id, PersistentComponentData>()
    private var eventHandlers = emptyList<ComponentEventHandler>()
    private var refreshedDuringWait = false
    private var waitingForRefresh = 0
    private var refreshingCounter = 0
    private val c = ComponentInstance(
        controller = this,
        persistent = PersistentComponentData(Id.Root, "root"),
        init = ComponentInstance.convertComponentFunction<Model, ParentDefinesMe, ParentDefinesMe> {
            singleChild.init()
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
                if (waitingForRefresh == 0) onRefreshed()
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
                onRefreshed()
                refreshedDuringWait = false
            }
        }
    }

    private fun onRefreshed() {
        events.push(Event.Refreshed)
    }

    @UndocumentedExperimentalUI
    sealed interface Event {
        @UndocumentedExperimentalUI
        object Refreshed: Event
    }

    companion object {
        @UndocumentedExperimentalUI
        operator fun invoke(
            init: ComponentChildContext<UIModel, ParentDefinesMe, ParentDefinesMe>.() -> ComponentIsResolved
        ) = UIController(UIModel(), init)

        @UndocumentedExperimentalUI
        fun runDefaultApp(
            title: String = "Default UI App",
            init: ComponentChildContext<UIModel, ParentDefinesMe, ParentDefinesMe>.() -> ComponentIsResolved
        ) {
            val window = GLFWWindowCreator.createWindow(title)
            val controller = UIController(UIModel(), init)

            controller.events.connect { window.draw { controller.repositionAndDraw(this) } }
            window.events.connect(controller::pushEvent)
            controller.load()

            while (!window.isClosed) GLFWWindowCreator.update()
        }
    }
}
