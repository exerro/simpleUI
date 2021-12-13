package com.exerro.simpleui.ui.internal

import com.exerro.simpleui.DrawContext
import com.exerro.simpleui.UndocumentedExperimentalUI
import com.exerro.simpleui.ui.*

@UndocumentedExperimentalUI
internal class ComponentInstance<
        Model: UIModel,
        Width: WhoDefinesMe,
        Height: WhoDefinesMe,
>(
    private val controller: UIController<Model>,
    private val persistent: PersistentComponentData,
    private val init: ComponentInstance<Model, Width, Height>.() -> TransientComponentData<Width, Height>,
) {
    lateinit var transient: TransientComponentData<Width, Height>; private set

    internal fun update() {
        controller.notifyUpdating(completed = false)
        transient = init()
        controller.notifyUpdating(completed = true)
    }

    companion object {
        @UndocumentedExperimentalUI
        fun <
                Model: UIModel,
                Width: WhoDefinesMe,
                Height: WhoDefinesMe,
        > convertComponentFunction(
            fn: ComponentContext<Model, Width, Height>.() -> ComponentIsResolved
        ): ComponentInstance<Model, Width, Height>.() -> TransientComponentData<Width, Height> = {
            val instance = this
            val lifecycleHooks = mutableListOf<LifecycleHook>()
            lateinit var children: List<ComponentInstance<*, *, *>>
            lateinit var resolver: ComponentSizeResolver<Width, Height>

            persistent.storage.resetIndex()

            object: ComponentContext<Model, Width, Height> {
                override val thisComponentId = persistent.id
                override val model get() = controller.getModel()
                override fun refresh() = instance.update()
                override fun setModel(model: Model) = controller.setModel(model)
                override fun updateModel(update: (Model) -> Model) = controller.updateModel(update)

                override fun <T> useOrderedStorageCell(getInitialValue: () -> T) =
                    persistent.storage.getNth(getInitialValue)

                override fun <T: Any> useOrderedLateInitStorageCell() =
                    persistent.storage.getNthLateInit<T>()

                override fun attachHook(hook: LifecycleHook) {
                    lifecycleHooks.add(hook)
                }

                override fun onDraw(draw: ComponentDrawFunction) {
                    lifecycleHooks.add(LifecycleHook.DrawHook(draw))
                }

                override fun connectEventHandler(handler: ComponentEventHandler) {
                    lifecycleHooks.add(LifecycleHook.WindowEventHook(handler))
                }

                override fun <SubWidth : WhoDefinesMe, SubHeight : WhoDefinesMe> children(
                    getChildren: ComponentChildrenContext<Model, SubWidth, SubHeight>.() -> Unit,
                    resolveComponentSize: (width: SizeForChild<Width>, height: SizeForChild<Height>, availableWidth: Float, availableHeight: Float, drawFunctions: List<ComponentDrawFunction>, eventHandlers: List<ComponentEventHandler>, children: List<ComponentSizeResolver<SubWidth, SubHeight>>) -> ResolvedComponentSizePhase<Width, Height>
                ): ComponentIsResolved {
                    val thisDrawFunctions = lifecycleHooks.filterIsInstance<LifecycleHook.DrawHook>().map<LifecycleHook.DrawHook, DrawContext.() -> Unit> { { it.run { draw() } } }
                    val thisEventHandlers = lifecycleHooks.filterIsInstance<LifecycleHook.WindowEventHook>().map { it::handleEvent }
                    val deferredChildren = mutableListOf<Triple<String, Id, ComponentContext<Model, SubWidth, SubHeight>.() -> ComponentIsResolved>>()
                    val context = object: ComponentChildrenContext<Model, SubWidth, SubHeight> {
                        override val ids = IdProvider(parent = persistent.id)
                        override val model get() = controller.getModel()
                        override fun setModel(model: Model) = controller.setModel(model)
                        override fun updateModel(update: (Model) -> Model) = controller.updateModel(update)

                        override fun component(
                            elementType: String,
                            id: Id,
                            init: ComponentContext<Model, SubWidth, SubHeight>.() -> ComponentIsResolved
                        ): ComponentIsResolved {
                            deferredChildren.add(Triple(elementType, id, init))
                            return ComponentIsResolved.INSTANCE
                        }
                    }

                    context.getChildren()

                    val childObjects = deferredChildren.map { (type, id, fn) ->
                        ComponentInstance(
                            persistent = controller.getPersistentData(id, type),
                            controller = controller,
                            init = convertComponentFunction(fn),
                        )
                    }

                    children = childObjects
                    resolver = { width, height, availableWidth, availableHeight ->
                        resolveComponentSize(width, height, availableWidth, availableHeight, thisDrawFunctions, thisEventHandlers, childObjects.map { it.transient.sizeResolver })
                    }

                    return ComponentIsResolved.INSTANCE
                }
            }.fn()

            val lastChildrenIds = persistent.childIds
            val currentChildrenIds = children.map { it.persistent.id } .toSet()
            val removedChildrenIds = lastChildrenIds - currentChildrenIds
            val insertedChildrenIds = currentChildrenIds - lastChildrenIds

            for (childId in removedChildrenIds)
                controller.updateRefCountLater(childId, -1)

            for (child in children) {
                child.update()
                if (child.persistent.id in insertedChildrenIds)
                    controller.updateRefCountLater(child.persistent.id, 1)
            }

            persistent.childIds = currentChildrenIds
            persistent.lifecycleHooks = lifecycleHooks

            TransientComponentData(resolver)
        }
    }
}
