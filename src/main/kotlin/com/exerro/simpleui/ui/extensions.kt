package com.exerro.simpleui.ui

import com.exerro.simpleui.EKeyPressed
import com.exerro.simpleui.UndocumentedExperimental

@UndocumentedExperimental
fun ComponentContext<*, *, *, *, *>.bind(keybind: ActionKeybind, behaviour: () -> Boolean) = connectEventHandler { event ->
    if (event !is EKeyPressed) return@connectEventHandler false
    val handled = keybind.keyName == event.name && keybind.modifiers == event.modifiers && (keybind.allowRepeats || !event.isRepeat)

    handled && behaviour()
}

@UndocumentedExperimental
fun ComponentContext<*, *, *, *, *>.bind(action: Action, behaviour: () -> Boolean) = connectEventHandler { event ->
    if (event !is EKeyPressed) return@connectEventHandler false
    val keybinds = model.keybinds[action]
    val handled = keybinds.any {
        it.keyName == event.name && it.modifiers == event.modifiers && (it.allowRepeats || !event.isRepeat)
    }

    handled && behaviour()
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@UndocumentedExperimental
data class ModifiedSizes<Width: Float?, Height: Float?>(
    val width: Width,
    val height: Height,
    val availableWidth: Float,
    val availableHeight: Float,
)

@UndocumentedExperimental
fun <ParentWidth: Float?, ParentHeight: Float?, ChildWidth: Float?, ChildHeight: Float?> ComponentContext<*, ParentWidth, ParentHeight, ChildWidth, ChildHeight>.noChildren(
    resolveComponent: (width: ParentWidth, height: ParentHeight, availableWidth: Float, availableHeight: Float, drawFunctions: List<ComponentDrawFunction>, eventHandlers: List<ComponentEventHandler>) -> ResolvedComponent<ChildWidth, ChildHeight>
) = children<Nothing?, Nothing?, Nothing?, Nothing?>({}) { w, h, aw, ah, drawFunctions, eventHandlers, _ ->
    resolveComponent(w, h, aw, ah, drawFunctions, eventHandlers)
}

@UndocumentedExperimental
inline fun <ParentWidth: Float?, ParentHeight: Float?, ChildWidth: Float?, ChildHeight: Float?> ComponentContext<*, ParentWidth, ParentHeight, ChildWidth, ChildHeight>.noChildren(
    crossinline resolveChildSize: (width: ParentWidth, height: ParentHeight, availableWidth: Float, availableHeight: Float) -> Pair<ChildWidth, ChildHeight>
) = children<Nothing?, Nothing?, Nothing?, Nothing?>({}) { w, h, aw, ah, drawFunctions, eventHandlers, _ ->
    val (cw, ch) = resolveChildSize(w, h, aw, ah)
    ResolvedComponent(cw, ch, eventHandlers) {
        for (f in drawFunctions) f(this)
    }
}

@UndocumentedExperimental
fun <ChildWidth: Float?, ChildHeight: Float?> ComponentContext<*, *, *, ChildWidth, ChildHeight>.noChildrenDeclareSize(
    width: ChildWidth,
    height: ChildHeight
) = noChildren { _, _, _, _ -> width to height }

@UndocumentedExperimental
@JvmName("noChildrenW")
fun <ChildWidth: Float?> ComponentContext<*, *, *, ChildWidth, Nothing?>.noChildrenDeclareWidth(width: ChildWidth): ComponentReturn =
    noChildren { _, _, _, _ -> width to null }

@UndocumentedExperimental
@JvmName("noChildrenH")
fun <ChildHeight: Float?> ComponentContext<*, *, *, Nothing?, ChildHeight>.noChildrenDeclareHeight(height: ChildHeight) =
    noChildren { _, _, _, _ -> null to height }

@UndocumentedExperimental
fun ComponentContext<*, *, *, Nothing?, Nothing?>.noChildren() =
    noChildren { _, _, _, _ -> null to null }

@UndocumentedExperimental
@JvmName("noChildrenWD")
fun <ChildWidth: Float?, ChildHeight: Float?> ComponentContext<*, *, *, ChildWidth, ChildHeight>.noChildrenDeclareDefaultSize(
    width: Float,
    height: Float,
) = noChildren { w, h, _, _ -> (if (w == null) width else null) as ChildWidth to (if (h == null) height else null) as ChildHeight }

@UndocumentedExperimental
@JvmName("noChildrenWD")
fun <ChildWidth: Float?> ComponentContext<*, *, *, ChildWidth, Nothing?>.noChildrenDeclareDefaultWidth(
    width: Float
) = noChildren { w, _, _, _ -> (if (w == null) width else null) as ChildWidth to null }

@UndocumentedExperimental
@JvmName("noChildrenHD")
fun <ChildHeight: Float?> ComponentContext<*, *, *, Nothing?, ChildHeight>.noChildrenDeclareDefaultHeight(
    height: Float
) = noChildren { _, h, _, _ -> null to (if (h == null) height else null) as ChildHeight }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@UndocumentedExperimental
fun <Model: UIModel, OldParentWidth: Float?, OldParentHeight: Float?, OldChildWidth: Float?, OldChildHeight: Float?, NewParentWidth: Float?, NewParentHeight: Float?, NewChildWidth: Float?, NewChildHeight: Float?>
ComponentChildrenContext<Model, OldParentWidth, OldParentHeight, OldChildWidth, OldChildHeight>.modifier(
    modifyParentSize: (OldParentWidth, OldParentHeight, Float, Float) -> ModifiedSizes<NewParentWidth, NewParentHeight>,
    modify: (OldParentWidth, OldParentHeight, Float, Float, ModifiedSizes<NewParentWidth, NewParentHeight>, ResolvedComponent<NewChildWidth, NewChildHeight>) -> ResolvedComponent<OldChildWidth, OldChildHeight>
): ComponentChildrenContext<Model, NewParentWidth, NewParentHeight, NewChildWidth, NewChildHeight> {
    val parentContext = this
    return object : ComponentChildrenContext<Model, NewParentWidth, NewParentHeight, NewChildWidth, NewChildHeight> {
        override val model: Model get() = parentContext.model
        override fun setModel(model: Model) = parentContext.setModel(model)

        override fun rawComponent(
            elementType: String,
            trackingId: Any?,
            init: ComponentContext<Model, NewParentWidth, NewParentHeight, NewChildWidth, NewChildHeight>.() -> ComponentReturn
        ) = parentContext.rawComponent(elementType, trackingId) {
            val pContext = this

            object: ComponentContext<Model, NewParentWidth, NewParentHeight, NewChildWidth, NewChildHeight> {
                override val thisComponentId get() = pContext.thisComponentId
                override val model: Model get() = pContext.model
                override fun setModel(model: Model) = pContext.setModel(model)
                override fun refresh() = pContext.refresh()
                override fun <ParentHeight : HookState> getHookStateOrRegister(newHook: () -> ParentHeight) = pContext.getHookStateOrRegister(newHook)
                override fun onDraw(draw: ComponentDrawFunction) = pContext.onDraw(draw)
                override fun connectEventHandler(handler: ComponentEventHandler) = pContext.connectEventHandler(handler)

                override fun <SubParentWidth : Float?, SubParentHeight : Float?, SubChildWidth : Float?, SubChildHeight : Float?> children(
                    getChildren: ComponentChildrenContext<Model, SubParentWidth, SubParentHeight, SubChildWidth, SubChildHeight>.() -> Unit,
                    resolveComponent: (NewParentWidth, NewParentHeight, Float, Float, List<ComponentDrawFunction>, List<ComponentEventHandler>, List<(SubParentWidth, SubParentHeight, Float, Float) -> ResolvedComponent<SubChildWidth, SubChildHeight>>) -> ResolvedComponent<NewChildWidth, NewChildHeight>
                ) = pContext.children(getChildren) { width, height, availableWidth, availableHeight, drawFunctions, eventHandlers, children ->
                    val m = modifyParentSize(width, height, availableWidth, availableHeight)
                    modify(width, height, availableWidth, availableHeight, m, resolveComponent(m.width, m.height, m.availableWidth, m.availableHeight, drawFunctions, eventHandlers, children))
                }
            } .init()
        }
    }
}
