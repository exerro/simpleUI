package com.exerro.simpleui.ui.components

import com.exerro.simpleui.UndocumentedExperimentalUI
import com.exerro.simpleui.ui.*
import com.exerro.simpleui.ui.extensions.bind
import com.exerro.simpleui.ui.extensions.singleChild
import com.exerro.simpleui.ui.extensions.useState
import com.exerro.simpleui.ui.standardActions.*

// TODO: wrapping on min/max
// TODO: tabSelector

@UndocumentedExperimentalUI
fun <Model: UIModel, Width: WhoDefinesMe, Height: WhoDefinesMe>
ComponentChildrenContext<Model, Width, Height>.selector(
    focused: Boolean,
    decreaseSelectionActions: Set<Action>,
    increaseSelectionActions: Set<Action>,
    minimumValue: Int? = 0,
    maximumValue: Int? = null,
    initialValue: Int = 0,
    init: ComponentChildrenContext<Model, Width, Height>.(selected: Int?) -> ComponentIsResolved
) = component("selector") {
    val (selected, setSelected) = useState(initialValue)

    if (focused && (minimumValue == null || selected > minimumValue)) {
        for (decreaseSelectionAction in decreaseSelectionActions) bind(decreaseSelectionAction) {
            setSelected(selected - 1)
            true
        }
    }

    if (focused && (maximumValue == null || selected < maximumValue)) {
        for (increaseSelectionAction in increaseSelectionActions) bind(increaseSelectionAction) {
            setSelected(selected + 1)
            true
        }
    }

    singleChild.init(selected.takeIf { focused })
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
    decreaseSelectionActions = setOf(MoveFocusLeft, FocusPreviousElement),
    increaseSelectionActions = setOf(MoveFocusRight, FocusNextElement),
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
    decreaseSelectionActions = setOf(MoveFocusUp, FocusPreviousElement),
    increaseSelectionActions = setOf(MoveFocusDown, FocusNextElement),
    minimumValue = minimumValue,
    maximumValue = maximumValue,
    initialValue = initialValue,
    init = init,
)
