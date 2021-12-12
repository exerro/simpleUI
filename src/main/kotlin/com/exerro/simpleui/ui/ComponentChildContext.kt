package com.exerro.simpleui.ui

import com.exerro.simpleui.UndocumentedExperimentalUI

@UndocumentedExperimentalUI
@UIContextType
interface ComponentChildContext<
        Model: UIModel,
        Width: WhoDefinesMe,
        Height: WhoDefinesMe,
>: ComponentChildrenContext<Model, Width, Height>, ComponentContext<Model, Width, Height>
