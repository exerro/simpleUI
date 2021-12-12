package com.exerro.simpleui.ui.standardActions

import com.exerro.simpleui.UndocumentedExperimentalUI
import com.exerro.simpleui.ui.Action

@UndocumentedExperimentalUI
object MoveFocusLeft: Action(
    description = "Move the focus one to the left."
)

@UndocumentedExperimentalUI
object MoveFocusRight: Action(
    description = "Move the focus one to the right."
)

@UndocumentedExperimentalUI
object MoveFocusUp: Action(
    description = "Move the focus one upwards."
)

@UndocumentedExperimentalUI
object MoveFocusDown: Action(
    description = "Move the focus one downwards."
)

@UndocumentedExperimentalUI
object FocusNextElement: Action(
    description = "Move the focus to the next component."
)

@UndocumentedExperimentalUI
object FocusPreviousElement: Action(
    description = "Move the focus to the previous component."
)
