package com.exerro.simpleui.ui

import com.exerro.simpleui.UndocumentedExperimentalUI
import com.exerro.simpleui.ui.internal.standardChildRendering

@UndocumentedExperimentalUI
@UIContextType
interface ComponentContext<
        Model: UIModel,
        Width: WhoDefinesMe,
        Height: WhoDefinesMe,
>: SharedContext<Model> {
    @UndocumentedExperimentalUI
    val thisComponentId: Id

    @UndocumentedExperimentalUI
    fun refresh()

    @UndocumentedExperimentalUI
    fun <H: HookState> getHookStateOrNew(newHook: () -> H): H

    @UndocumentedExperimentalUI
    fun onDraw(draw: ComponentDrawFunction)

    @UndocumentedExperimentalUI
    fun connectEventHandler(handler: ComponentEventHandler)

    @UndocumentedExperimentalUI
    fun <SubWidth: WhoDefinesMe, SubHeight: WhoDefinesMe> children(
        getChildren: ComponentChildrenContext<Model, SubWidth, SubHeight>.() -> Unit,
        resolveComponentSize: (
            width: SizeForChild<Width>,
            height: SizeForChild<Height>,
            availableWidth: Float,
            availableHeight: Float,
            drawFunctions: List<ComponentDrawFunction>,
            eventHandlers: List<ComponentEventHandler>,
            children: List<ComponentSizeResolver<SubWidth, SubHeight>>
        ) -> ResolvedComponentSizePhase<Width, Height>,
    ): ComponentIsResolved
}
