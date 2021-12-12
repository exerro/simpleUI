package com.exerro.simpleui.ui.components

import com.exerro.simpleui.UndocumentedExperimentalUI
import com.exerro.simpleui.ui.*
import com.exerro.simpleui.ui.hooks.useState
import com.exerro.simpleui.ui.standardActions.MoveFocusDown
import com.exerro.simpleui.ui.standardActions.MoveFocusLeft
import com.exerro.simpleui.ui.standardActions.MoveFocusRight
import com.exerro.simpleui.ui.standardActions.MoveFocusUp

// TODO: wrapping on min/max
// TODO: tabSelector

@UndocumentedExperimentalUI
fun <Model: UIModel, Width: WhoDefinesMe, Height: WhoDefinesMe>
ComponentChildrenContext<Model, Width, Height>.selector(
    focused: Boolean,
    decreaseSelectionAction: Action,
    increaseSelectionAction: Action,
    minimumValue: Int? = 0,
    maximumValue: Int? = null,
    initialValue: Int = 0,
    init: ComponentChildrenContext<Model, Width, Height>.(selected: Int?) -> ComponentIsResolved
) = component("selector") {
    val (selected, setSelected) = useState(initialValue)

    if (focused && (minimumValue == null || selected > minimumValue)) bind(decreaseSelectionAction) {
        setSelected(selected - 1)
        true
    }

    if (focused && (maximumValue == null || selected < maximumValue)) bind(increaseSelectionAction) {
        setSelected(selected + 1)
        true
    }

    init(selected.takeIf { focused })
}

@UndocumentedExperimentalUI
fun <Model: UIModel, Width: WhoDefinesMe, Height: WhoDefinesMe>
ComponentChildrenContext<Model, Width, Height>.horizontalSelector(
    focused: Boolean,
    minimumValue: Int? = 0,
    maximumValue: Int? = null,
    initialValue: Int = 0,
    init: ComponentChildrenContext<Model, Width, Height>.(selected: Int?) -> ComponentIsResolved
) = selector(
    focused = focused,
    decreaseSelectionAction = MoveFocusLeft,
    increaseSelectionAction = MoveFocusRight,
    minimumValue = minimumValue,
    maximumValue = maximumValue,
    initialValue = initialValue,
    init = init,
)

@UndocumentedExperimentalUI
fun <Model: UIModel, Width: WhoDefinesMe, Height: WhoDefinesMe>
        ComponentChildrenContext<Model, Width, Height>.verticalSelector(
    focused: Boolean,
    minimumValue: Int? = 0,
    maximumValue: Int? = null,
    initialValue: Int = 0,
    init: ComponentChildrenContext<Model, Width, Height>.(selected: Int?) -> ComponentIsResolved
) = selector(
    focused = focused,
    decreaseSelectionAction = MoveFocusUp,
    increaseSelectionAction = MoveFocusDown,
    minimumValue = minimumValue,
    maximumValue = maximumValue,
    initialValue = initialValue,
    init = init,
)
