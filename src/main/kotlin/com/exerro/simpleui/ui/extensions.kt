package com.exerro.simpleui.ui

import com.exerro.simpleui.EKeyPressed
import com.exerro.simpleui.UndocumentedExperimentalUI
import com.exerro.simpleui.ui.internal.GenericResolver

@UndocumentedExperimentalUI
fun ComponentContext<*, *, *>.bind(keybind: ActionKeybind, behaviour: () -> Boolean) = connectEventHandler { event ->
    if (event !is EKeyPressed) return@connectEventHandler false
    val handled = keybind.keyName == event.name && keybind.modifiers == event.modifiers && (keybind.allowRepeats || !event.isRepeat)

    handled && behaviour()
}

@UndocumentedExperimentalUI
fun ComponentContext<*, *, *>.bind(action: Action, behaviour: () -> Boolean) = connectEventHandler { event ->
    if (event !is EKeyPressed) return@connectEventHandler false
    val keybinds = model.keybinds[action]
    val handled = keybinds.any {
        it.keyName == event.name && it.modifiers == event.modifiers && (it.allowRepeats || !event.isRepeat)
    }

    handled && behaviour()
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@UndocumentedExperimentalUI
fun ComponentContext<*, ParentDefinesMe, ParentDefinesMe>.noChildren() = setResolver { _, _, _, _, drawFunctions, eventHandlers ->
    SizeResolvedComponent(nothingForParent(), nothingForParent(), eventHandlers) { for (f in drawFunctions) f(this) }
}

@UndocumentedExperimentalUI
inline fun <Width: WhoDefinesMe, Height: WhoDefinesMe> ComponentContext<*, Width, Height>.noChildren(
    crossinline resolveChildSize: (width: SomeValueForChild<Width>, height: SomeValueForChild<Height>, availableWidth: Float, availableHeight: Float) -> Pair<SomeValueForParent<Width>, SomeValueForParent<Height>>
) = setResolver { w, h, aw, ah, drawFunctions, eventHandlers ->
    val (cw, ch) = resolveChildSize(w, h, aw, ah)
    SizeResolvedComponent(cw, ch, eventHandlers) { for (f in drawFunctions) f(this) }
}

@UndocumentedExperimentalUI
inline fun <reified Width: WhoDefinesMe, reified Height: WhoDefinesMe> ComponentContext<*, Width, Height>.noChildrenDefineDefaultSize(
    width: Float,
    height: Float,
) = setResolver { w, h, _, _, drawFunctions, eventHandlers ->
    val cw = fixForParentAny<Width>(fixFromParentAnyOptional(w) ?: width)
    val ch = fixForParentAny<Height>(fixFromParentAnyOptional(h) ?: height)
    SizeResolvedComponent(cw, ch, eventHandlers) { for (f in drawFunctions) f(this) }
}

@UndocumentedExperimentalUI
inline fun <reified Width: WhoDefinesMe> ComponentContext<*, Width, ParentDefinesMe>.noChildrenDefineDefaultWidth(
    width: Float
) = setResolver { w, _, _, _, drawFunctions, eventHandlers ->
    val cw = fixForParentAny<Width>(fixFromParentAnyOptional(w) ?: width)
    SizeResolvedComponent(cw, nothingForParent(), eventHandlers) { for (f in drawFunctions) f(this) }
}

@UndocumentedExperimentalUI
inline fun <reified Height: WhoDefinesMe> ComponentContext<*, ParentDefinesMe, Height>.noChildrenDefineDefaultHeight(
    height: Float
) = setResolver { _, h, _, _, drawFunctions, eventHandlers ->
    val ch = fixForParentAny<Height>(fixFromParentAnyOptional(h) ?: height)
    SizeResolvedComponent(nothingForParent(), ch, eventHandlers) { for (f in drawFunctions) f(this) }
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@UndocumentedExperimentalUI
data class ModifiedSizes<Width: WhoDefinesMe, Height: WhoDefinesMe>(
    val width: SomeValueForChild<Width>,
    val height: SomeValueForChild<Height>,
    val availableWidth: Float,
    val availableHeight: Float,
)

@UndocumentedExperimentalUI
fun <Model: UIModel, OldWidth: WhoDefinesMe, OldHeight: WhoDefinesMe, NewWidth: WhoDefinesMe, NewHeight: WhoDefinesMe>
        ComponentChildrenContext<Model, OldWidth, OldHeight>.modifier(
    modifyParentSize: (SomeValueForChild<OldWidth>, SomeValueForChild<OldHeight>, Float, Float) -> ModifiedSizes<NewWidth, NewHeight>,
    modify: (SomeValueForChild<OldWidth>, SomeValueForChild<OldHeight>, Float, Float, ModifiedSizes<NewWidth, NewHeight>, SizeResolvedComponent<NewWidth, NewHeight>) -> SizeResolvedComponent<OldWidth, OldHeight>
): ComponentChildrenContext<Model, NewWidth, NewHeight> {
    val parentContext = this
    return object : ComponentChildrenContext<Model, NewWidth, NewHeight> {
        override val ids = parentContext.ids
        override val model get() = parentContext.model
        override fun setModel(model: Model) = parentContext.setModel(model)

        override fun rawComponent(
            elementType: String,
            id: Id,
            init: ComponentContext<Model, NewWidth, NewHeight>.() -> ComponentIsResolved
        ) = parentContext.rawComponent(elementType, id) {
            val pContext = this

            object: ComponentContext<Model, NewWidth, NewHeight> {
                override val thisComponentId get() = pContext.thisComponentId
                override val model get() = pContext.model
                override fun setModel(model: Model) = pContext.setModel(model)
                override fun refresh() = pContext.refresh()
                override fun <H : HookState> getHookStateOrNew(newHook: () -> H) = pContext.getHookStateOrNew(newHook)
                override fun onDraw(draw: ComponentDrawFunction) = pContext.onDraw(draw)
                override fun connectEventHandler(handler: ComponentEventHandler) = pContext.connectEventHandler(handler)

                override fun setResolver(resolveComponent: (width: SomeValueForChild<NewWidth>, height: SomeValueForChild<NewHeight>, availableWidth: Float, availableHeight: Float, drawFunctions: List<ComponentDrawFunction>, eventHandlers: List<ComponentEventHandler>) -> SizeResolvedComponent<NewWidth, NewHeight>) = pContext.setResolver { width, height, availableWidth, availableHeight, drawFunctions, eventHandlers ->
                    val m = modifyParentSize(width, height, availableWidth, availableHeight)
                    modify(width, height, availableWidth, availableHeight, m, resolveComponent(m.width, m.height, m.availableWidth, m.availableHeight, drawFunctions, eventHandlers))
                }

                override fun <SubWidth : WhoDefinesMe, SubHeight : WhoDefinesMe> children(
                    getChildren: ComponentChildrenContext<Model, SubWidth, SubHeight>.() -> Unit,
                    resolveComponent: (width: SomeValueForChild<NewWidth>, height: SomeValueForChild<NewHeight>, availableWidth: Float, availableHeight: Float, drawFunctions: List<ComponentDrawFunction>, eventHandlers: List<ComponentEventHandler>, children: List<GenericResolver<SubWidth, SubHeight>>) -> SizeResolvedComponent<NewWidth, NewHeight>
                ) = pContext.children(getChildren) { width, height, availableWidth, availableHeight, drawFunctions, eventHandlers, children ->
                    val m = modifyParentSize(width, height, availableWidth, availableHeight)
                    modify(width, height, availableWidth, availableHeight, m, resolveComponent(m.width, m.height, m.availableWidth, m.availableHeight, drawFunctions, eventHandlers, children))
                }
            } .init()
        }
    }
}
