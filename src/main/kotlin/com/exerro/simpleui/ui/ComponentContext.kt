package com.exerro.simpleui.ui

import com.exerro.simpleui.UndocumentedExperimentalUI

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
    fun <T> useOrderedStorageCell(getInitialValue: () -> T): PersistentStorageCell<T>

    @UndocumentedExperimentalUI
    fun <T: Any> useOrderedLateInitStorageCell(): PersistentStorageCell<T>

    @UndocumentedExperimentalUI
    fun attachHook(hook: LifecycleHook)

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
