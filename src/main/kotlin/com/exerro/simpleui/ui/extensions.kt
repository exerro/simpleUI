package com.exerro.simpleui.ui

import com.exerro.simpleui.EKeyPressed
import com.exerro.simpleui.UndocumentedExperimentalUI
import com.exerro.simpleui.ui.internal.calculateInverse

@UndocumentedExperimentalUI
fun ComponentContext<*, *, *, *, *>.bind(keybind: ActionKeybind, behaviour: () -> Boolean) = connectEventHandler { event ->
    if (event !is EKeyPressed) return@connectEventHandler false
    val handled = keybind.keyName == event.name && keybind.modifiers == event.modifiers && (keybind.allowRepeats || !event.isRepeat)

    handled && behaviour()
}

@UndocumentedExperimentalUI
fun ComponentContext<*, *, *, *, *>.bind(action: Action, behaviour: () -> Boolean) = connectEventHandler { event ->
    if (event !is EKeyPressed) return@connectEventHandler false
    val keybinds = model.keybinds[action]
    val handled = keybinds.any {
        it.keyName == event.name && it.modifiers == event.modifiers && (it.allowRepeats || !event.isRepeat)
    }

    handled && behaviour()
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@UndocumentedExperimentalUI
fun ComponentContext<*, *, *, Nothing?, Nothing?>.noChildren() = setResolver { _, _, _, _, drawFunctions, eventHandlers ->
    ResolvedComponent(null, null, eventHandlers) { for (f in drawFunctions) f(this) }
}

@UndocumentedExperimentalUI
inline fun <ParentWidth: Float?, ParentHeight: Float?, ChildWidth: Float?, ChildHeight: Float?> ComponentContext<*, ParentWidth, ParentHeight, ChildWidth, ChildHeight>.noChildren(
    crossinline resolveChildSize: (width: ParentWidth, height: ParentHeight, availableWidth: Float, availableHeight: Float) -> Pair<ChildWidth, ChildHeight>
) = setResolver { w, h, aw, ah, drawFunctions, eventHandlers ->
    val (cw, ch) = resolveChildSize(w, h, aw, ah)
    ResolvedComponent(cw, ch, eventHandlers) { for (f in drawFunctions) f(this) }
}

@UndocumentedExperimentalUI
fun <ChildWidth: Float?, ChildHeight: Float?> ComponentContext<*, *, *, ChildWidth, ChildHeight>.noChildrenDefineDefaultSize(
    width: Float,
    height: Float,
) = setResolver { w, h, _, _, drawFunctions, eventHandlers ->
    val cw = calculateInverse<ChildWidth>(w) { width }
    val ch = calculateInverse<ChildHeight>(h) { height }
    ResolvedComponent(cw, ch, eventHandlers) { for (f in drawFunctions) f(this) }
}

@UndocumentedExperimentalUI
fun <ChildWidth: Float?> ComponentContext<*, *, *, ChildWidth, Nothing?>.noChildrenDefineDefaultWidth(
    width: Float
) = setResolver { w, _, _, _, drawFunctions, eventHandlers ->
    val cw = calculateInverse<ChildWidth>(w) { width }
    ResolvedComponent(cw, null, eventHandlers) { for (f in drawFunctions) f(this) }
}

@UndocumentedExperimentalUI
fun <ChildHeight: Float?> ComponentContext<*, *, *, Nothing?, ChildHeight>.noChildrenDefineDefaultHeight(
    height: Float
) = setResolver { _, h, _, _, drawFunctions, eventHandlers ->
    val ch = calculateInverse<ChildHeight>(h) { height }
    ResolvedComponent(null, ch, eventHandlers) { for (f in drawFunctions) f(this) }
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@UndocumentedExperimentalUI
data class ModifiedSizes<Width: Float?, Height: Float?>(
    val width: Width,
    val height: Height,
    val availableWidth: Float,
    val availableHeight: Float,
)

@UndocumentedExperimentalUI
fun <Model: UIModel, OldParentWidth: Float?, OldParentHeight: Float?, OldChildWidth: Float?, OldChildHeight: Float?, NewParentWidth: Float?, NewParentHeight: Float?, NewChildWidth: Float?, NewChildHeight: Float?>
ComponentChildrenContext<Model, OldParentWidth, OldParentHeight, OldChildWidth, OldChildHeight>.modifier(
    modifyParentSize: (OldParentWidth, OldParentHeight, Float, Float) -> ModifiedSizes<NewParentWidth, NewParentHeight>,
    modify: (OldParentWidth, OldParentHeight, Float, Float, ModifiedSizes<NewParentWidth, NewParentHeight>, ResolvedComponent<NewChildWidth, NewChildHeight>) -> ResolvedComponent<OldChildWidth, OldChildHeight>
): ComponentChildrenContext<Model, NewParentWidth, NewParentHeight, NewChildWidth, NewChildHeight> {
    val parentContext = this
    return object : ComponentChildrenContext<Model, NewParentWidth, NewParentHeight, NewChildWidth, NewChildHeight> {
        override val ids = parentContext.ids
        override val model get() = parentContext.model
        override fun setModel(model: Model) = parentContext.setModel(model)

        override fun rawComponent(
            elementType: String,
            id: Id,
            init: ComponentContext<Model, NewParentWidth, NewParentHeight, NewChildWidth, NewChildHeight>.() -> ComponentIsResolved
        ) = parentContext.rawComponent(elementType, id) {
            val pContext = this

            object: ComponentContext<Model, NewParentWidth, NewParentHeight, NewChildWidth, NewChildHeight> {
                override val thisComponentId get() = pContext.thisComponentId
                override val model get() = pContext.model
                override fun setModel(model: Model) = pContext.setModel(model)
                override fun refresh() = pContext.refresh()
                override fun <ParentHeight : HookState> getHookStateOrNew(newHook: () -> ParentHeight) = pContext.getHookStateOrNew(newHook)
                override fun onDraw(draw: ComponentDrawFunction) = pContext.onDraw(draw)
                override fun connectEventHandler(handler: ComponentEventHandler) = pContext.connectEventHandler(handler)

                override fun <SubParentWidth : Float?, SubParentHeight : Float?, SubChildWidth : Float?, SubChildHeight : Float?> children(
                    getChildren: ComponentChildrenContext<Model, SubParentWidth, SubParentHeight, SubChildWidth, SubChildHeight>.() -> Unit,
                    resolveComponent: (NewParentWidth, NewParentHeight, Float, Float, List<ComponentDrawFunction>, List<ComponentEventHandler>, List<(SubParentWidth, SubParentHeight, Float, Float) -> ResolvedComponent<SubChildWidth, SubChildHeight>>) -> ResolvedComponent<NewChildWidth, NewChildHeight>
                ) = pContext.children(getChildren) { width, height, availableWidth, availableHeight, drawFunctions, eventHandlers, children ->
                    val m = modifyParentSize(width, height, availableWidth, availableHeight)
                    modify(width, height, availableWidth, availableHeight, m, resolveComponent(m.width, m.height, m.availableWidth, m.availableHeight, drawFunctions, eventHandlers, children))
                }

                override fun setResolver(
                    resolveComponent: (width: NewParentWidth, height: NewParentHeight, availableWidth: Float, availableHeight: Float, drawFunctions: List<ComponentDrawFunction>, eventHandlers: List<ComponentEventHandler>) -> ResolvedComponent<NewChildWidth, NewChildHeight>
                ) = pContext.setResolver { width, height, availableWidth, availableHeight, drawFunctions, eventHandlers ->
                    val m = modifyParentSize(width, height, availableWidth, availableHeight)
                    modify(width, height, availableWidth, availableHeight, m, resolveComponent(m.width, m.height, m.availableWidth, m.availableHeight, drawFunctions, eventHandlers))
                }
            } .init()
        }
    }
}
