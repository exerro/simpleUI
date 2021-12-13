package com.exerro.simpleui.ui.extensions

import com.exerro.simpleui.Region
import com.exerro.simpleui.UndocumentedExperimentalUI
import com.exerro.simpleui.ui.*
import com.exerro.simpleui.ui.ComponentSizeResolver

@UndocumentedExperimentalUI
data class ModifiedSizes<Width: WhoDefinesMe, Height: WhoDefinesMe>(
    val width: SizeForChild<Width>,
    val height: SizeForChild<Height>,
    val availableWidth: Float,
    val availableHeight: Float,
)

@UndocumentedExperimentalUI
fun <Model: UIModel, OldWidth: WhoDefinesMe, OldHeight: WhoDefinesMe, NewWidth: WhoDefinesMe, NewHeight: WhoDefinesMe>
ComponentChildrenContext<Model, OldWidth, OldHeight>.modifier(
    modifyParentSize: (SizeForChild<OldWidth>, SizeForChild<OldHeight>, Float, Float) -> ModifiedSizes<NewWidth, NewHeight>,
    modify: (SizeForChild<OldWidth>, SizeForChild<OldHeight>, Float, Float, ModifiedSizes<NewWidth, NewHeight>, ResolvedComponentSizePhase<NewWidth, NewHeight>) -> ResolvedComponentSizePhase<OldWidth, OldHeight>
): ComponentChildrenContext<Model, NewWidth, NewHeight> {
    val parentContext = this
    return object : ComponentChildrenContext<Model, NewWidth, NewHeight> {
        override val ids = parentContext.ids
        override val model get() = parentContext.model
        override fun setModel(model: Model) = parentContext.setModel(model)

        override fun component(
            elementType: String,
            id: Id,
            init: ComponentContext<Model, NewWidth, NewHeight>.() -> ComponentIsResolved
        ) = parentContext.component(elementType, id) {
            val pContext = this

            object: ComponentContext<Model, NewWidth, NewHeight> {
                override val thisComponentId get() = pContext.thisComponentId
                override val model get() = pContext.model
                override fun setModel(model: Model) = pContext.setModel(model)
                override fun refresh() = pContext.refresh()
                override fun attachHook(hook: LifecycleHook) = pContext.attachHook(hook)
                override fun <T> useOrderedStorageCell(getInitialValue: () -> T) = pContext.useOrderedStorageCell(getInitialValue)
                override fun <T: Any> useOrderedLateInitStorageCell() = pContext.useOrderedLateInitStorageCell<T>()
                override fun onDraw(draw: ComponentDrawFunction) = pContext.onDraw(draw)
                override fun connectEventHandler(handler: ComponentEventHandler) = pContext.connectEventHandler(handler)

                override fun <SubWidth : WhoDefinesMe, SubHeight : WhoDefinesMe> children(
                    getChildren: ComponentChildrenContext<Model, SubWidth, SubHeight>.() -> Unit,
                    resolveComponentSize: (width: SizeForChild<NewWidth>, height: SizeForChild<NewHeight>, availableWidth: Float, availableHeight: Float, drawFunctions: List<ComponentDrawFunction>, eventHandlers: List<ComponentEventHandler>, children: List<ComponentSizeResolver<SubWidth, SubHeight>>) -> ResolvedComponentSizePhase<NewWidth, NewHeight>
                ) = pContext.children(getChildren) { width, height, availableWidth, availableHeight, drawFunctions, eventHandlers, children ->
                    val m = modifyParentSize(width, height, availableWidth, availableHeight)
                    modify(width, height, availableWidth, availableHeight, m, resolveComponentSize(m.width, m.height, m.availableWidth, m.availableHeight, drawFunctions, eventHandlers, children))
                }
            } .init()
        }
    }
}

// TODO: not sure this is correct
@UndocumentedExperimentalUI
fun <Model: UIModel, OldWidth: WhoDefinesMe, OldHeight: WhoDefinesMe, NewWidth: WhoDefinesMe, NewHeight: WhoDefinesMe>
ComponentChildrenContext<Model, OldWidth, OldHeight>.regionModifier(
    modifyParentSize: (SizeForChild<OldWidth>, SizeForChild<OldHeight>, Float, Float) -> ModifiedSizes<NewWidth, NewHeight>,
    modifyRegion: (
        parentWidth: SizeForChild<NewWidth>,
        parentHeight: SizeForChild<NewHeight>,
        childWidth: SizeForParent<NewWidth>,
        childHeight: SizeForParent<NewHeight>,
        region: Region,
    ) -> Region,
) = modifier(modifyParentSize) { _, _, _, _, m, (childWidth, childHeight, positionResolver) ->
    ResolvedComponentSizePhase(
        width = fixForParentAny(fixFromChildAny(childWidth)),
        height = fixForParentAny(fixFromChildAny(childHeight))
    ) { r -> positionResolver(modifyRegion(m.width, m.height, childWidth, childHeight, r)) }
}
