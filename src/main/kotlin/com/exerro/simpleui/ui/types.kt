package com.exerro.simpleui.ui

import com.exerro.simpleui.DrawContext
import com.exerro.simpleui.UndocumentedExperimental
import com.exerro.simpleui.WindowEvent

@UndocumentedExperimental
typealias AnyParentContext = ParentContext<*, *, *, *, *>

@UndocumentedExperimental
typealias ParentDefinedContext<Model> = ParentContext<Model, Float, Float, Nothing?, Nothing?>

@UndocumentedExperimental
typealias ComponentDefinedContext<Model> = ParentContext<Model, Nothing?, Nothing?, Float, Float>

@UndocumentedExperimental
typealias ComponentEventHandler = (WindowEvent) -> Boolean

@UndocumentedExperimental
typealias ComponentDrawFunction = DrawContext.() -> Unit
