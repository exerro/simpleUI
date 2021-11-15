package com.exerro.simpleui.ui

import com.exerro.simpleui.UndocumentedExperimental

@UndocumentedExperimental
@UIContextType
interface ComponentContext<
        Model: UIModel,
        /** Width provided by parent to this component. */
        ParentWidth: Float?,
        /** Height provided by parent to this component. */
        ParentHeight: Float?,
        /** Width provided by this component to parent. */
        ChildWidth: Float?,
        /** Height provided by this component to parent. */
        ChildHeight: Float?,
>: SharedContext<Model> {
    @UndocumentedExperimental
    val thisComponentId: Any?

    @UndocumentedExperimental
    fun refresh()

    @UndocumentedExperimental
    fun <H: HookState> getHookStateOrRegister(newHook: () -> H): H

    @UndocumentedExperimental
    fun onDraw(draw: ComponentDrawFunction)

    @UndocumentedExperimental
    fun connectEventHandler(handler: ComponentEventHandler)

    @UndocumentedExperimental
    // TODO: add extra configuration for controlling child tracking
    fun <SubParentWidth: Float?, SubParentHeight: Float?, SubChildWidth: Float?, SubChildHeight: Float?> children(
        getChildren: ParentContext<Model, SubParentWidth, SubParentHeight, SubChildWidth, SubChildHeight>.() -> Unit,
        resolveComponent: (
            width: ParentWidth,
            height: ParentHeight,
            availableWidth: Float,
            availableHeight: Float,
            drawFunctions: List<ComponentDrawFunction>,
            eventHandlers: List<ComponentEventHandler>,
            children: List<(SubParentWidth, SubParentHeight, Float, Float) -> ResolvedComponent<SubChildWidth, SubChildHeight>>
        ) -> ResolvedComponent<ChildWidth, ChildHeight>,
    ): ComponentReturn
}
