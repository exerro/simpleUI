package com.exerro.simpleui.ui.internal

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

    internal fun refresh() {
        controller.notifyRefreshing(completed = false)
        persistent.hooks.reset()
        transient = init()
        controller.notifyRefreshing(completed = true)
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
            val drawFunctions = mutableListOf<ComponentDrawFunction>()
            val eventHandlers = mutableListOf<ComponentEventHandler>()
            lateinit var resolver: GenericResolver<Width, Height>

            object: ComponentContext<Model, Width, Height> {
                override val thisComponentId = persistent.id
                override val model get() = controller.getModel()
                override fun refresh() = instance.refresh()
                override fun setModel(model: Model) = controller.setModel(model)
                override fun updateModel(update: (Model) -> Model) = controller.updateModel(update)

                override fun <H : HookState> getHookStateOrNew(newHook: () -> H) =
                    persistent.hooks.getHookStateOrNew(newHook)

                override fun onDraw(draw: ComponentDrawFunction) {
                    drawFunctions.add(draw)
                }

                override fun connectEventHandler(handler: ComponentEventHandler) {
                    eventHandlers.add(handler)
                }

                override fun <SubWidth : WhoDefinesMe, SubHeight : WhoDefinesMe> children(
                    getChildren: ComponentChildrenContext<Model, SubWidth, SubHeight>.() -> Unit,
                    resolveComponent: (width: SomeValueForChild<Width>, height: SomeValueForChild<Height>, availableWidth: Float, availableHeight: Float, drawFunctions: List<ComponentDrawFunction>, eventHandlers: List<ComponentEventHandler>, children: List<GenericResolver<SubWidth, SubHeight>>) -> SizeResolvedComponent<Width, Height>
                ): ComponentIsResolved {
                    val thisDrawFunctions = drawFunctions.toList()
                    val thisEventHandlers = eventHandlers.toList()
                    val deferredChildren = mutableListOf<Triple<String, Id, ComponentContext<Model, SubWidth, SubHeight>.() -> ComponentIsResolved>>()
                    val context = object: ComponentChildrenContext<Model, SubWidth, SubHeight> {
                        override val ids = IdProvider(parent = persistent.id)
                        override val model get() = controller.getModel()
                        override fun setModel(model: Model) = controller.setModel(model)
                        override fun updateModel(update: (Model) -> Model) = controller.updateModel(update)

                        override fun rawComponent(
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
                        val c = ComponentInstance(
                            persistent = controller.getPersistentData(id, type),
                            controller = controller,
                            init = convertComponentFunction(fn),
                        )

                        c.refresh()
                        c
                    }

                    resolver = { width, height, availableWidth, availableHeight ->
                        resolveComponent(width, height, availableWidth, availableHeight, thisDrawFunctions, thisEventHandlers, childObjects.map { it.transient.resolver })
                    }

                    return ComponentIsResolved.INSTANCE
                }

                override fun setResolver(resolveComponent: (width: SomeValueForChild<Width>, height: SomeValueForChild<Height>, availableWidth: Float, availableHeight: Float, drawFunctions: List<ComponentDrawFunction>, eventHandlers: List<ComponentEventHandler>) -> SizeResolvedComponent<Width, Height>): ComponentIsResolved {
                    val thisDrawFunctions = drawFunctions.toList()
                    val thisEventHandlers = eventHandlers.toList()

                    resolver = { width, height, availableWidth, availableHeight ->
                        resolveComponent(width, height, availableWidth, availableHeight, thisDrawFunctions, thisEventHandlers)
                    }

                    return ComponentIsResolved.INSTANCE
                }
            }.fn()

            TransientComponentData(drawFunctions, eventHandlers, resolver)
        }
    }
}
