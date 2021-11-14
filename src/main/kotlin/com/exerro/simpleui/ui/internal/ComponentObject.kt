package com.exerro.simpleui.ui.internal

import com.exerro.simpleui.DrawContext
import com.exerro.simpleui.UndocumentedInternal
import com.exerro.simpleui.WindowEvent
import com.exerro.simpleui.ui.*

@UndocumentedInternal
internal data class ComponentObject<
        ParentWidth: Float?,
        ParentHeight: Float?,
        ChildWidth: Float?,
        ChildHeight: Float?,
>(
    private val thisComponentType: String,
    private val thisComponentId: Any?,
    private val generator: ComponentContext<ParentWidth, ParentHeight, ChildWidth, ChildHeight>.() -> ChildReturn,
    private val parentNotifyChanged: (completed: Boolean) -> Unit,
    private val hooks: HookManager = HookManager(),
    private var children: List<ComponentObject<*, *, *, *>> = emptyList(),
) {
    private val eventHandlers = mutableListOf<(WindowEvent) -> Unit>()
    lateinit var resolveChildren: (ParentWidth, ParentHeight, Float, Float) -> ResolvedChild<ChildWidth, ChildHeight>; private set

    @UndocumentedInternal
    fun createContext() = object: ComponentContext<ParentWidth, ParentHeight, ChildWidth, ChildHeight> {
        private val onDrawFunctions = mutableListOf<DrawContext.() -> Unit>()

        override val thisComponentId = this@ComponentObject.thisComponentId

        override fun <H : HookState> getHookStateOrRegister(newHook: () -> H) =
            hooks.getHookStateOrNew(newHook)

        override fun refresh() {
            this@ComponentObject.refresh()
        }

        override fun onDraw(draw: DrawContext.() -> Unit) {
            onDrawFunctions.add(draw)
        }

        override fun connectEventHandler(handler: (WindowEvent) -> Unit) {
            eventHandlers.add(handler)
        }

        override fun <SubParentWidth: Float?, SubParentHeight: Float?, SubChildWidth: Float?, SubChildHeight: Float?> children(
            getChildren: ParentContext<SubParentWidth, SubParentHeight, SubChildWidth, SubChildHeight>.() -> Unit,
            resolveChildren: (ParentWidth, ParentHeight, Float, Float, List<DrawContext.() -> Unit>, List<(SubParentWidth, SubParentHeight, Float, Float) -> ResolvedChild<SubChildWidth, SubChildHeight>>) -> ResolvedChild<ChildWidth, ChildHeight>
        ): ChildReturn {
            val standaloneDrawFunctions = onDrawFunctions.toList()
            val deferredChildren = mutableListOf<Triple<String, Any?, ComponentContext<SubParentWidth, SubParentHeight, SubChildWidth, SubChildHeight>.() -> ChildReturn>>()
            val context = object: ParentContext<SubParentWidth, SubParentHeight, SubChildWidth, SubChildHeight> {
                override fun rawComponent(
                    elementType: String,
                    trackingId: Any?,
                    init: ComponentContext<SubParentWidth, SubParentHeight, SubChildWidth, SubChildHeight>.() -> ChildReturn
                ): ChildReturn {
                    deferredChildren.add(Triple(elementType, trackingId, init))
                    return ChildReturn.INSTANCE
                }
            }

            context.getChildren()

            val currentWithoutId = children.filter { it.thisComponentId == null }
            val newWithoutId = deferredChildren.filter { it.second == null }
            val idLessTypesMatch = currentWithoutId.map { it.thisComponentType } == newWithoutId.map { it.first }
            var idLessIndex = 0
            val childObjects = deferredChildren.map { (type, id, fn) ->
                val existingComponent = when (id) {
                    null -> if (idLessTypesMatch) currentWithoutId[idLessIndex++] else null
                    else -> children.firstOrNull { it.thisComponentId == id } ?.takeIf { it.thisComponentType == type }
                }

                val newComponent = ComponentObject(
                    type, id, fn, parentNotifyChanged,
                    hooks = existingComponent?.hooks ?: HookManager(),
                    children = existingComponent?.children ?: emptyList(),
                )

                newComponent.refresh()
                newComponent
            }

            this@ComponentObject.children = childObjects
            this@ComponentObject.resolveChildren = { width, height, availableWidth, availableHeight ->
                resolveChildren(width, height, availableWidth, availableHeight, standaloneDrawFunctions, childObjects.map { it.resolveChildren })
            }

            return ChildReturn.INSTANCE
        }
    }

    @UndocumentedInternal
    fun refresh() {
        parentNotifyChanged(false)
        eventHandlers.clear()
        hooks.reset()
        createContext().generator()
        parentNotifyChanged(true)
    }
}
