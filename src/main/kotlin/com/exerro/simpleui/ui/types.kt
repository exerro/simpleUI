package com.exerro.simpleui.ui

import com.exerro.simpleui.DrawContext
import com.exerro.simpleui.UndocumentedExperimentalUI
import com.exerro.simpleui.WindowEvent

@UndocumentedExperimentalUI
typealias AnyComponentChildrenContext = ComponentChildrenContext<*, *, *>

@UndocumentedExperimentalUI
typealias ParentDefinedContext<Model> = ComponentChildrenContext<Model, ParentDefinesMe, ParentDefinesMe>

@UndocumentedExperimentalUI
typealias ComponentDefinedContext<Model> = ComponentChildrenContext<Model, ChildDefinesMe, ChildDefinesMe>

@UndocumentedExperimentalUI
typealias ComponentEventHandler = (WindowEvent) -> Boolean

@UndocumentedExperimentalUI
typealias ComponentDrawFunction = DrawContext.() -> Unit
