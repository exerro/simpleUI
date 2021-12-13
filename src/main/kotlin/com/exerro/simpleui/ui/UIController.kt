package com.exerro.simpleui.ui

import com.exerro.simpleui.*
import com.exerro.simpleui.ui.extensions.singleChild
import com.exerro.simpleui.ui.internal.ComponentInstance
import com.exerro.simpleui.ui.internal.PersistentComponentData

@UndocumentedExperimentalUI
class UIController<Model: UIModel>(
    initialModel: Model,
    init: ComponentChildContext<Model, ParentDefinesMe, ParentDefinesMe>.() -> ComponentIsResolved,
) {
    val events = PushableEventBus<Event>()

    @UndocumentedExperimentalUI
    fun load(width: Float, height: Float) {
        c.update()
        setSizeDirect(width, height)
        recomputePositioning(force = true)
    }

    @UndocumentedExperimentalUI
    fun setSizeDirect(width: Float, height: Float) {
        contentWidth = width
        contentHeight = height
        recomputePositioning(force = false)
    }

    @UndocumentedExperimentalUI
    fun pushEvent(event: WindowEvent): Boolean {
        if (event is EWindowResized) {
            setSizeDirect(event.width.toFloat(), event.height.toFloat())
            return true
        }

        for (eventHandler in eventHandlers.reversed()) {
            if (eventHandler(event)) return true
        }

        return false
    }

    @UndocumentedExperimentalUI
    fun draw(context: DrawContext) {
        positionResolvedContent.draw(context)
    }

    ////////////////////////////////////////////////////////////////////////////

    @UndocumentedExperimentalUI
    fun getModel() = currentModel

    @UndocumentedExperimentalUI
    fun setModel(model: Model) {
        currentModel = model
        c.update()
    }

    @UndocumentedExperimentalUI
    fun updateModel(update: (Model) -> Model) =
        setModel(update(getModel()))

    private lateinit var positionResolvedContent: ResolvedComponentPositionPhase
    private val pendingRefCountChildrenIds = mutableSetOf<Id>()
    private var contentWidth = 0f
    private var contentHeight = 0f
    private var lastWidth = 0f
    private var lastHeight = 0f
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

    internal fun getPersistentData(id: Id): PersistentComponentData? {
        return persistentData[id]
    }

    internal fun updateRefCountLater(id: Id, refs: Int) {
        val persistent = persistentData[id] ?: return
        persistent.refCount += refs
        pendingRefCountChildrenIds.add(id)
    }

    internal fun notifyUpdating(completed: Boolean) {
        if (completed) {
            if (--refreshingCounter == 0) {
                if (waitingForRefresh == 0) onUpdate()
                else refreshedDuringWait = true
            }
        }
        else ++refreshingCounter
    }

    internal fun setWaitingForUpdatePropagation(done: Boolean = false) {
        if (!done) {
            ++waitingForRefresh
        }
        if (done) {
            if (--waitingForRefresh == 0 && refreshedDuringWait) {
                onUpdate()
                refreshedDuringWait = false
            }
        }
    }

    private fun onUpdate() {
        for (refsUpdatedId in pendingRefCountChildrenIds) {
            val refsUpdatedChild = persistentData[refsUpdatedId] ?: continue

            if (refsUpdatedChild.refCount == 0 && refsUpdatedChild.isMounted) {
                for (f in refsUpdatedChild.lifecycleHooks.filterIsInstance<LifecycleHook.UnloadHook>()) {
                    f.onUnload()
                }

                refsUpdatedChild.isMounted = false
                persistentData.remove(refsUpdatedChild.id)
            }
            else if (refsUpdatedChild.refCount > 0 && !refsUpdatedChild.isMounted) {
                for (f in refsUpdatedChild.lifecycleHooks.filterIsInstance<LifecycleHook.LoadHook>()) {
                    f.onLoad()
                }

                refsUpdatedChild.isMounted = true
            }
        }

        recomputePositioning(force = true)

        events.push(Event.Updated)
        pendingRefCountChildrenIds.clear()
    }

    private fun recomputePositioning(force: Boolean) {
        if (!force && contentWidth == lastWidth && contentHeight == lastHeight) return

        lastWidth = contentWidth
        lastHeight = contentHeight
        positionResolvedContent = c.transient
            .sizeResolver(fixForChild(contentWidth), fixForChild(contentHeight), contentWidth, contentHeight)
            .positionResolver(Region(0f, 0f, contentWidth, contentHeight))
        eventHandlers = positionResolvedContent.eventHandlers
    }

    @UndocumentedExperimentalUI
    sealed interface Event {
        @UndocumentedExperimentalUI
        object Updated: Event
    }

    companion object {
        @UndocumentedExperimentalUI
        operator fun invoke(
            init: ComponentChildContext<UIModel, ParentDefinesMe, ParentDefinesMe>.() -> ComponentIsResolved
        ) = UIController(UIModel(), init)

        @UndocumentedExperimentalUI
        fun runDefaultApp(
            title: String = "Default UI App",
            init: ComponentChildContext<UIModel, ParentDefinesMe, ParentDefinesMe>.(window: Window) -> ComponentIsResolved
        ) {
            val window = GLFWWindowCreator.createWindow(title)
            val controller = UIController(UIModel()) { init(window) }

            controller.events.connect { window.draw { controller.draw(this) } }
            window.events.connect(controller::pushEvent)
            controller.load(window.currentWidth.toFloat(), window.currentHeight.toFloat())

            while (!window.isClosed) GLFWWindowCreator.update()
        }
    }
}
