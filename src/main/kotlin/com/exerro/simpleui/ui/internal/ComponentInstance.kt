package com.exerro.simpleui.ui.internal

import com.exerro.simpleui.UndocumentedExperimentalUI
import com.exerro.simpleui.ui.*

@UndocumentedExperimentalUI
internal class ComponentInstance<
        Model: UIModel,
        ParentWidth: Float?,
        ParentHeight: Float?,
        ChildWidth: Float?,
        ChildHeight: Float?,
>(
    private val controller: UIController2<Model>,
    private val persistent: PersistentComponentData2,
    private val init: ComponentInstance<Model, ParentWidth, ParentHeight, ChildWidth, ChildHeight>.() -> TransientComponentData<ParentWidth, ParentHeight, ChildWidth, ChildHeight>,
) {
    internal lateinit var transient: TransientComponentData<ParentWidth, ParentHeight, ChildWidth, ChildHeight>

    internal fun refresh() {
        controller.notifyRefreshed(false)
        persistent.hooks.reset()
        transient = init()
        controller.notifyRefreshed(true)
    }

    companion object {
        @UndocumentedExperimentalUI
        fun <
                Model: UIModel,
                ParentWidth: Float?,
                ParentHeight: Float?,
                ChildWidth: Float?,
                ChildHeight: Float?,
        > convertComponentFunction(
            fn: ComponentContext<Model, ParentWidth, ParentHeight, ChildWidth, ChildHeight>.() -> ComponentIsResolved
        ): ComponentInstance<Model, ParentWidth, ParentHeight, ChildWidth, ChildHeight>.() -> TransientComponentData<ParentWidth, ParentHeight, ChildWidth, ChildHeight> = {
            val instance = this
            val drawFunctions = mutableListOf<ComponentDrawFunction>()
            val eventHandlers = mutableListOf<ComponentEventHandler>()
            lateinit var resolver: (ParentWidth, ParentHeight, Float, Float) -> ResolvedComponent<ChildWidth, ChildHeight>

            object: ComponentContext<Model, ParentWidth, ParentHeight, ChildWidth, ChildHeight> {
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

                override fun <SubParentWidth : Float?, SubParentHeight : Float?, SubChildWidth : Float?, SubChildHeight : Float?> children(
                    getChildren: ComponentChildrenContext<Model, SubParentWidth, SubParentHeight, SubChildWidth, SubChildHeight>.() -> Unit,
                    resolveComponent: (width: ParentWidth, height: ParentHeight, availableWidth: Float, availableHeight: Float, drawFunctions: List<ComponentDrawFunction>, eventHandlers: List<ComponentEventHandler>, children: List<(SubParentWidth, SubParentHeight, Float, Float) -> ResolvedComponent<SubChildWidth, SubChildHeight>>) -> ResolvedComponent<ChildWidth, ChildHeight>
                ): ComponentIsResolved {
                    val thisDrawFunctions = drawFunctions.toList()
                    val thisEventHandlers = eventHandlers.toList()
                    val deferredChildren = mutableListOf<Triple<String, ID, ComponentContext<Model, SubParentWidth, SubParentHeight, SubChildWidth, SubChildHeight>.() -> ComponentIsResolved>>()
                    val context = object: ComponentChildrenContext<Model, SubParentWidth, SubParentHeight, SubChildWidth, SubChildHeight> {
                        override val model get() = controller.getModel()
                        override fun setModel(model: Model) = controller.setModel(model)
                        override fun updateModel(update: (Model) -> Model) = controller.updateModel(update)

                        override fun rawComponent(
                            elementType: String,
                            trackingId: Any?,
                            init: ComponentContext<Model, SubParentWidth, SubParentHeight, SubChildWidth, SubChildHeight>.() -> ComponentIsResolved
                        ): ComponentIsResolved {
                            deferredChildren.add(Triple(elementType, trackingId as ID, init)) // TODO
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

                override fun setResolver(resolveComponent: (width: ParentWidth, height: ParentHeight, availableWidth: Float, availableHeight: Float, drawFunctions: List<ComponentDrawFunction>, eventHandlers: List<ComponentEventHandler>) -> ResolvedComponent<ChildWidth, ChildHeight>): ComponentIsResolved {
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
