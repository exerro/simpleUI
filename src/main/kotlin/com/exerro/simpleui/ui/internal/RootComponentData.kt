package com.exerro.simpleui.ui.internal

import com.exerro.simpleui.UndocumentedInternal

@UndocumentedInternal
internal interface RootComponentData<Model> {
    @UndocumentedInternal
    fun getModel(): Model

    @UndocumentedInternal
    fun setModel(model: Model)

    @UndocumentedInternal
    fun parentNotifyRefreshed(completed: Boolean)
}
