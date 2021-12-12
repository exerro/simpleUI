package com.exerro.simpleui.ui.internal

import com.exerro.simpleui.UndocumentedInternal
import com.exerro.simpleui.ui.*

@UndocumentedInternal
internal data class ComponentObject<
        Model: UIModel,
        ParentWidth: Float?,
        ParentHeight: Float?,
        ChildWidth: Float?,
        ChildHeight: Float?,
>(
    private val root: RootComponentData<Model>,
    private val persistent: PersistentComponentData,
    private var children: List<ComponentObject<Model, *, *, *, *>> = emptyList(),
    private val generator: ComponentContext<Model, ParentWidth, ParentHeight, ChildWidth, ChildHeight>.() -> ComponentIsResolved,
) {
    lateinit var resolveChildren: (ParentWidth, ParentHeight, Float, Float) -> ResolvedComponent<ChildWidth, ChildHeight>; private set

    @UndocumentedInternal
    fun createContext() = object: ComponentContext<Model, ParentWidth, ParentHeight, ChildWidth, ChildHeight> {
        private val onDrawFunctions = mutableListOf<ComponentDrawFunction>()
        private val eventHandlers = mutableListOf<ComponentEventHandler>()

        override val thisComponentId = persistent.id
        override val model get() = root.getModel()
        override fun setModel(model: Model) = root.setModel(model)

        override fun <H : HookState> getHookStateOrNew(newHook: () -> H) =
            persistent.hooks.getHookStateOrNew(newHook)

        override fun refresh() {
            this@ComponentObject.refresh()
        }

        override fun onDraw(draw: ComponentDrawFunction) {
            onDrawFunctions.add(draw)
        }

        override fun connectEventHandler(handler: ComponentEventHandler) {
            eventHandlers.add(handler)
        }

        override fun <SubParentWidth: Float?, SubParentHeight: Float?, SubChildWidth: Float?, SubChildHeight: Float?> children(
            getChildren: ComponentChildrenContext<Model, SubParentWidth, SubParentHeight, SubChildWidth, SubChildHeight>.() -> Unit,
            resolveComponent: (ParentWidth, ParentHeight, Float, Float, List<ComponentDrawFunction>, List<ComponentEventHandler>, List<(SubParentWidth, SubParentHeight, Float, Float) -> ResolvedComponent<SubChildWidth, SubChildHeight>>) -> ResolvedComponent<ChildWidth, ChildHeight>
        ): ComponentIsResolved {
            val thisDrawFunctions = onDrawFunctions.toList()
            val thisEventHandlers = eventHandlers.toList()
            val deferredChildren = mutableListOf<Triple<String, Any?, ComponentContext<Model, SubParentWidth, SubParentHeight, SubChildWidth, SubChildHeight>.() -> ComponentIsResolved>>()
            val context = object: ComponentChildrenContext<Model, SubParentWidth, SubParentHeight, SubChildWidth, SubChildHeight> {
                override val model get() = root.getModel()
                override fun setModel(model: Model) = root.setModel(model)

                override fun rawComponent(
                    elementType: String,
                    trackingId: Any?,
                    init: ComponentContext<Model, SubParentWidth, SubParentHeight, SubChildWidth, SubChildHeight>.() -> ComponentIsResolved
                ): ComponentIsResolved {
                    deferredChildren.add(Triple(elementType, trackingId, init))
                    return ComponentIsResolved.INSTANCE
                }
            }

            context.getChildren()

            val currentWithoutId = children.filter { it.persistent.id == null }
            val newWithoutId = deferredChildren.filter { it.second == null }
            val idLessTypesMatch = currentWithoutId.map { it.persistent.type } == newWithoutId.map { it.first }
            var idLessIndex = 0
            val childObjects = deferredChildren.map { (type, id, fn) ->
                val existingComponent = when (id) {
                    null -> if (idLessTypesMatch) currentWithoutId[idLessIndex++] else null
                    else -> children.firstOrNull { it.persistent.id == id } ?.takeIf { it.persistent.type == type }
                }

                val newComponent = ComponentObject(
                    root = root,
                    persistent = existingComponent?.persistent ?: PersistentComponentData(id, type),
                    generator = fn,
                    children = existingComponent?.children ?: emptyList(),
                )

                newComponent.refresh()
                newComponent
            }

            this@ComponentObject.children = childObjects
            this@ComponentObject.resolveChildren = { width, height, availableWidth, availableHeight ->
                resolveComponent(width, height, availableWidth, availableHeight, thisDrawFunctions, thisEventHandlers, childObjects.map { it.resolveChildren })
            }

            return ComponentIsResolved.INSTANCE
        }

        override fun setResolver(resolveComponent: (width: ParentWidth, height: ParentHeight, availableWidth: Float, availableHeight: Float, drawFunctions: List<ComponentDrawFunction>, eventHandlers: List<ComponentEventHandler>) -> ResolvedComponent<ChildWidth, ChildHeight>): ComponentIsResolved {
            val thisDrawFunctions = onDrawFunctions.toList()
            val thisEventHandlers = eventHandlers.toList()

            this@ComponentObject.resolveChildren = { width, height, availableWidth, availableHeight ->
                resolveComponent(width, height, availableWidth, availableHeight, thisDrawFunctions, thisEventHandlers)
            }

            return ComponentIsResolved.INSTANCE
        }
    }

    @UndocumentedInternal
    fun refresh() {
        root.parentNotifyRefreshed(false)
        persistent.hooks.reset()
        createContext().generator()
        root.parentNotifyRefreshed(true)
    }
}
